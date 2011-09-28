package pt.whiteroad.plugins.notifications

import pt.whiteroad.plugins.notifications.config.NotificationUtils

/**
 * The notification service is the main entry point to the provided functionality. All the relevant methods
 * are exposed here. Note that simple topic management operations can be handled directly by the NotificationTopic object and GORM
 * so for now these will not be contemplated in the service.
 * */
class NotificationService {

  def static int interval = 1000

  static transactional = false

  def quartzScheduler
  def grailsApplication

  /**
   * This is syntatic sugar. It creates a notification based on the arguments, and then calls
   * the sendNotification method. Note that if the topic does not exist it is created.
   * @param topic - A String that represents the topic
   * @param message - A String that has the payload of the notification
   * @param scheduledDate - The date at which the notification should be sent. If it is null it is sent immediatly.
   * */
  def sendNotification(String topic, String message, Date scheduledDate = null){
    Notification.withTransaction{status ->
      try{
        def notificationTopic = NotificationTopic.findByTopic(topic) ?: new NotificationTopic(topic: topic).save(flush:true)
        def notification = new Notification(topic: notificationTopic, message: message, scheduledDate: scheduledDate)
        if(!notification.save()){
          notification.errors.each{
            println it
          }
          throw new RuntimeException()
        }
        sendNotification(notification)
      }catch(Exception e){
        e.printStackTrace()
        status.setRollbackOnly()
      }
    }
  }


  /**
   * Sends notifications to all subscribers - scheduling them if needed. If the notification is scheduled
   * to begin in less than a second from the current time then it is ignored and it is immediately run.
   * @param notification - An instance of a Notification that is to be sent.
   * */
  def sendNotification(Notification notification){
    if(notification.scheduledDate && notification.scheduledDate.time > (System.currentTimeMillis() + interval)){
      //Schedule event
      ScheduleNotificationJob.schedule(notification.scheduledDate, ['notificationId': notification.id])
    }else{
      //Send now!
      runAsync {
        sendNow(notification)
      }
    }
  }

  /**
   * Convenience method that allows a user to subscribe a topic through all
   * the available channels
   * @param subscriber - An instance of the subscriber class
   * @param topic - A string with the topic of a notification.
   * @return subscription - The susbcription that was created.
   * */
  def subscribeTopic(subscriber, topic){
    def channels = subscriber.channels
    return subscribeTopic(subscriber, topic, channels)
  }

  /**
   * Allows the subscription of a topic.
   * @param subscriber - An instance of the subscriber class
   * @param topic - A string with the topic of a notification.
   * @param channels - The channels the user wishes to use to receive
   * the notifications. These channels should be registered with the
   * subscriber.
   * @return subscription - The susbcription that was created.
   * */
  def subscribeTopic(subscriber, topic, channels){
    def Subscription = loadSubscription()

    Subscription.withTransaction{ status ->
      try{
        def notificationTopic = NotificationTopic.findByTopic(topic)
        def subscription = Subscription.newInstance()
        subscription.topic = notificationTopic
        channels.each{ channel ->
          subscription.addToChannels(channel)
        }

        subscriber.addToSubscriptions(subscription)
        if(!subscriber.save()){
          subscriber.errors.each{
            System.err.println(it)
          }
          throw new RuntimeException()
        }
        return subscription
      }catch(Exception e){
        e.printStackTrace()
        status.setRollbackOnly()
        return null
      }
    }
  }

  /**
   * Syntatic sugar method. Unsubscribes a topic
   * @param subscriber - String, The subscriber's alias
   * @param topic - String, the Notification's topic
   * */
  def unsubscribeTopic(String subscriber, String topic){
    def notificationTopic = NotificationTopic.findByTopic(topic)
    def Subscriber = loadSubscriber()
    def theSubscriber = Subscriber.findByAlias(subscriber)
    unsubscribeTopic(theSubscriber, notificationTopic)
  }

  /**
   * Unsubscribes a topic.
   * @param subscriber - A Subscriber
   * @param topic - the topic of a notificationTopic
   * */
  def unsubscribeTopic(subscriber, topic){
    def Subscription = loadSubscription()

    Subscription.withTransaction{ status ->
      //Remove subscription
      try{
        def notificationTopic = NotificationTopic.findByTopic(topic)
        def subscription = Subscription.findBySubscriberAndTopic(subscriber, notificationTopic)

        subscriber.removeFromSubscriptions(subscription)
        subscription.delete()
      }catch(Exception e){
        e.printStackTrace()
        status.setRollbackOnly()
      }
    }
  }

  /**
   * Immediately sends notifications to the subscribers, overlooking the scheduler.
   * */
  def sendNow(Notification notification){
    def subscriptionCls = loadSubscription()
    try{
      def subscriptions = subscriptionCls.findAllByTopic(notification.topic)
      subscriptions.each{ subscription ->
        if(!subscription.disabled){
          subscription.channels.each{ channel ->
            try{
              println "Creating instance of ${channel.channelImpl}"
              def loader = this.class.classLoader
              def context = Class.forName(channel.channelImpl, true, loader)
              IChannelSender sender = (IChannelSender)context.newInstance()
              sender.send(notification, channel.destination)
            }catch(Exception e){
              e.printStackTrace()
            }
          }
        }
      }

      notification.processed = true
      if(!notification.save(flush: true)){
        notification.errors.each{
          System.err.println it
        }
      }
    }catch(Exception e){
      e.printStackTrace()
    }
  }

  /**
   * Collects any delayed notifications, and returns them in a list.
   * @return List<Notification> - The list with all delayed notifications.
   * */
  def collectDelayedNotifications(){
    def now = new Date()
    def notifications = Notification.withCriteria{
      and{
        eq('processed', false)
        or{
          isNull('scheduledDate')
          lt('scheduledDate', now)
        }
      }

    }
    return notifications
  }

  private Class loadSubscriber(){
    def className = NotificationUtils.config.subscriberDomainClass
    return grailsApplication.getClassForName("${className}")
  }

  private Class loadSubscription(){
    def className = NotificationUtils.config.subscriptionDomainClass
    return grailsApplication.getClassForName("${className}")
  }

}