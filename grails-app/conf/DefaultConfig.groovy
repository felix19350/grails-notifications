grails{
  plugins{
    notifications{
      domainClassPackage = "pt.whiteroad.plugins.notifications"
      subscriberDomainClass = "pt.whiteroad.plugins.notifications.Subscriber"
      subscriptionDomainClass = "pt.whiteroad.plugins.notifications.Subscription"
      multiThread = false  //When a notification is sent it spawns a new thread. Disabled by default
    }
  }
}