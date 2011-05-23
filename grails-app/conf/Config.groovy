// configuration for plugin testing - will not be included in the plugin zip
grails {
  mail {
    host = "smtp.gmail.com"
    port = 465
    username = "whiteroadtest@gmail.com"
    password = "euviumsapo"
    props = ["mail.smtp.auth":"true",
            "mail.smtp.socketFactory.port":"465",
            "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
            "mail.smtp.socketFactory.fallback":"false"]
  }
}


log4j = {
  // Example of changing the log pattern for the default console
  // appender:
  //
  //appenders {
  //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
  //}
  debug 'grails.app.task'
  error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
          'org.codehaus.groovy.grails.web.pages', //  GSP
          'org.codehaus.groovy.grails.web.sitemesh', //  layouts
          'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
          'org.codehaus.groovy.grails.web.mapping', // URL mapping
          'org.codehaus.groovy.grails.commons', // core / classloading
          'org.codehaus.groovy.grails.plugins', // plugins
          'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
          'org.springframework',
          'org.hibernate',
          'net.sf.ehcache.hibernate'

  warn   'org.mortbay.log'
}


grails{
  plugins{
    notifications{
      domainClassPackage= 'test'
      subscriberDomainClass = 'test.TestSubscriber'
      subscriptionDomainClass = 'test.TestSubscription'
    }
  }
}
