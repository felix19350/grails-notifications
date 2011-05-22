package pt.whiteroad.plugins.notifications

/**
 * The notification service is the main entry point to the provided functionality. All the relevant methods
 * are exposed here. Note that simple topic management operations can be handled directly by the NotificationTopic object and GORM
 * so for now these will not be contemplated in the service.
 * */
class NotificationService {

  def static int interval = 1000

  static transactional = false

  def quartzScheduler


  def createSubscriber(String alias, Collection<Channel> channels){
    Subscriber.withTransaction{status ->
      try{
        def toInsert = []

        channels.each{
          if(!it.id){
            toInsert << it.save()
          }else{
            toInsert << it
          }
        }

        def subscriber = new Subscriber(alias: alias)
        toInsert.each{
          subscriber.addToChannels(it)
        }

        if(!subscriber.save()){
          throw new RuntimeException()
        }

        return subscriber
      }catch(Exception e){
        e.printStackTrace()
        status.setRollbackOnly()
        return null
      }
    }
  }

  /**
   * This is syntatic sugar. It creates a notification based on the arguments, and then calls
   * the sendNotification method. Note that if the topic does not exist it is created.
   * */
  def sendNotification(String topic, String message, Date scheduledDate){
    Notification.withTransaction{status ->
      try{
        def notificationTopic = NotificationTopic.findByTopic(topic) ?: new NotificationTopic(topic: topic).save(flush:true)
        def notification = new Notification(topic: notificationTopic, message: message, scheduledDate: scheduledDate)
        if(!notification.save()){
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
   * Immediately sends notifications to the subscribers, overlooking the scheduler.
   * */
  def sendNow(Notification notification){
    def subscriptions = Subscription.findAllByTopic(notification.topic)
    subscriptions.each{ subscription ->
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

  /**
   * Allows the subscription of a topic.
   * @return subscription - The susbcription that was created.
   * */
  def subscribeTopic(Subscriber subscriber, String topic, Collection<Channel> channels){
    Subscription.withTransaction{ status ->
      try{
        def notificationTopic = NotificationTopic.findByTopic(topic)
        def subscription = new Subscription(topic: notificationTopic)
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
   * */
  def unsubscribeTopic(Subscriber subscriber, String topic){
    def notificationTopic = NotificationTopic.findByTopic(topic)
    unsubscribeTopic(subscriber, notificationTopic)
  }

  /**
   * Unsubscribes a topic
   * */
  def unsubscribeTopic(Subscriber subscriber, NotificationTopic topic){
    Subscription.withTransaction{ status ->
      //Remove subscription
      try{
        println "Subscriber: ${subscriber?.alias} - Topic: ${topic?.topic}"
        Subscription.list().each{
          println "${it.subscriber.alias} - ${it.topic.topic}"
        }
        def subscription = Subscription.findBySubscriberAndTopic(subscriber, topic)

        subscriber.removeFromSubscriptions(subscription)
        subscription.delete()
      }catch(Exception e){
        e.printStackTrace()
        status.setRollbackOnly()
      }
    }
  }

}
