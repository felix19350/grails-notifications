grails{
  plugins{
    notifications{
      domainClassPackage = "pt.whiteroad.plugins.notifications"
      subscriberDomainClass = "pt.whiteroad.plugins.notifications.Subscriber"
      subscriptionDomainClass = "pt.whiteroad.plugins.notifications.Subscription"
    }
  }
}