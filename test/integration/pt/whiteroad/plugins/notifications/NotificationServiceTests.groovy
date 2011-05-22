package pt.whiteroad.plugins.notifications


class NotificationServiceTests extends GroovyTestCase {

  def transactional = false
  def notificationService
  def quartzScheduler

  public static final String defaultTopic = "test"
  public static final String defaultSubscriber = "subscriber"

  protected void setUp() {
    super.setUp()
    quartzScheduler.start()

    if(!NotificationTopic.findByTopic(defaultTopic)){
      new NotificationTopic(topic: defaultTopic).save(flush: true)
    }

    if(!Subscriber.findByAlias(defaultSubscriber)){
      def channels = [
              new Channel(channelImpl: ChannelType.Email.implementingClass, destination: "nuno.lopes.luis@gmail.com"),
              new Channel(channelImpl: ChannelType.Email.implementingClass, destination: "felix19350@gmail.com"),
              new Channel(channelImpl: ChannelType.Internal.implementingClass, destination: "TEST USER")
      ]
      def subscriber = notificationService.createSubscriber(defaultSubscriber, channels)

      assertNotNull subscriber
      assertEquals subscriber.channels.size(), channels.size()
    }

  }

  protected void tearDown() {
    super.tearDown()
  }

  void testPubSubNotScheduled(){
    createSubscription()

    def oldCount = Notification.count()
    def topic = NotificationTopic.findByTopic(defaultTopic)
    Notification notification = new Notification(message: "PUB/SUB notification", topic: topic)
    notification.save(flush: true)
    assertEquals oldCount+1 , Notification.count()
    notificationService.sendNotification(notification)

    //Sleep should be here since the sendNotification launches a new thread and the application may begin
    //its shutdown process in the meantime
    sleep 10000
  }

  void testPubSubScheduled(){
    createSubscription()

    def oldCount = Notification.count()
    def topic = NotificationTopic.findByTopic(defaultTopic)
    def date = new Date(System.currentTimeMillis() + 10000);

    println "Scheduled to: ${date}"

    Notification notification = new Notification(message: "Scheduled PUB/SUB notification", topic: topic, scheduledDate: date).save(flush: true)
    assertEquals oldCount+1 , Notification.count()
    notificationService.sendNotification(notification)

    //Sleep should be here since the sendNotification launches a new thread and the application may begin
    //its shutdown process in the meantime
    sleep 15000
  }

  /**
   * Test a custom channel implementation. Two new channels are added to the subscriber.
   * */
  void testPubSubCustomNotScheduled(){
    def customTopic = "Strange topic"
    if(!NotificationTopic.findByTopic(customTopic)){
      new NotificationTopic(topic: customTopic).save()
    }

    def channels = [
            new Channel(channelImpl: "pt.whiteroad.plugins.notifications.custom.CustomMailNotification", destination: "felix19350@gmail.com"),
            new Channel(channelImpl: "pt.whiteroad.plugins.notifications.custom.CustomMailNotification", destination: "nuno.lopes.luis@gmail.com")
    ]

    def subscriber = Subscriber.findByAlias(defaultSubscriber)
    channels.each{
      subscriber.addToChannels(it)
    }
    subscriber.save(flush: true)

    notificationService.subscribeTopic(subscriber, customTopic, subscriber.channels)

    def oldCount = Notification.count()
    def topic = NotificationTopic.findByTopic(customTopic)
    Notification notification = new Notification(message: "PUB/SUB notification", topic: topic)
    if(!notification.save(flush: true)){
      notification.errors.each{
        System.err.println(it)
      }
    }
    assertEquals oldCount+1 , Notification.count()
    notificationService.sendNotification(notification)

    //Sleep should be here since the sendNotification launches a new thread and the application may begin
    //its shutdown process in the meantime
    sleep 10000
  }

  void testUnsubscribeTopic(){
    createSubscription()

    def oldNum = Subscription.count()

    def subscriber = Subscriber.findByAlias(defaultSubscriber)
    def topic = NotificationTopic.findByTopic(defaultTopic)
    notificationService.unsubscribeTopic(subscriber, topic)

    assertEquals(oldNum-1, Subscription.count())
  }

  /**
   * Subscribes a topic using all the available communication channels.
   * */
  private void createSubscription(){
    def subscriber = Subscriber.findByAlias(defaultSubscriber)
    def notificationTopic = NotificationTopic.findByTopic(defaultTopic)
    if(!Subscription.findBySubscriberAndTopic(subscriber, notificationTopic)){
      def oldNumSubscriptions = subscriber?.subscriptions?.count() ?: 0
      def subscription = notificationService.subscribeTopic(subscriber, defaultTopic, subscriber.channels)
      subscriber.refresh()

      assertNotNull(subscription)
      assertEquals subscription.channels.size(), subscriber.channels.size()
      //Refresh the subscriber and count the subscriptions
      subscriber = Subscriber.get(subscriber.id)
      assertEquals subscriber?.subscriptions?.size(), oldNumSubscriptions + 1

    }

  }
}
