import grails.util.GrailsNameUtils

USAGE = """
Usage: grails notificationSetup <domain-class-package> <subscriber-class-name> <subscription-class-name>

Example: grails notificationSetup com.myapp MySubscriber MySubscription
"""

includeTargets << new File("$notificationsPluginDir/scripts/_NotificationsCommon.groovy")
includeTargets << grailsScript("_GrailsBootstrap")

def packageName
def subscriberDomainClass
def subscriptionDomainClass

target(notificationSetup: "The description of the script goes here!") {
  depends(checkVersion, configureProxy, packageApp, classpath)

  configure()
  createArtifacts()
  updateConfig()

  ant.echo """
            **************************************************************
            Created domain classes, and your grails-app/conf/Config.groovy
            has been updated with the class names of the configured domain
            classes. Please verify that the values are correct.
            **************************************************************
            """
}

/**
 * Read arguments from the command line and prepare template arguments.
 * */
private void configure() {
  def argValues = parseArgs()
  (packageName, subscriberDomainClass, subscriptionDomainClass) = argValues

  templateAttributes = [packageName: packageName,
          subscriberDomainClass: subscriberDomainClass,
          subscriptionDomainClass: subscriptionDomainClass]
}

/**
 * Creates artifacts (currently only domain classes based on the arguments)
 * */
private void createArtifacts() {
  String dir = packageToDir(packageName)
  generateFile "$templateDir/Subscriber.groovy.template", "$appDir/domain/${dir}${subscriberDomainClass}.groovy"
  generateFile "$templateDir/Subscription.groovy.template", "$appDir/domain/${dir}${subscriptionDomainClass}.groovy"

}

/**
 * Updates the configuration
 * */
private void updateConfig() {
  def configFile = new File(appDir, 'conf/Config.groovy')
  if (configFile.exists()) {
    configFile.withWriterAppend {
      it.writeLine '\n// Notifications plugin - BEGIN GENERATED CONFIG'
      it.writeLine "grails.plugins.notifications.domainClassPackage = '${packageName}'"
      it.writeLine "grails.plugins.notifications.subscriberDomainClass = '${packageName}.$subscriberDomainClass'"
      it.writeLine "grails.plugins.notifications.subscriptionDomainClass = '${packageName}.$subscriptionDomainClass'"
      it.writeLine '\n// Notifications plugin - END GENERATED CONFIG'
      it.writeLine 'Multi-threaded notifications are disabled by default. To change this, please add the following line to the Config.groovy file:'
      it.writeLine 'grails.plugins.notifications.multiThread = false'
    }
  }
}

private parseArgs() {
  args = args ? args.split('\n') : []
  switch (args.size()) {
    case 3:
      ant.echo message: "Creating Subscriber class ${args[1]} and Subscription class ${args[2]} in package ${args[0]}"
      return args
    default:
      ant.echo message: USAGE
      System.exit(1)
      break
  }
}


setDefaultTarget 'notificationSetup'
