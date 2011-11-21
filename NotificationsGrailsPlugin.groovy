import pt.whiteroad.plugins.notifications.NotificationTopic
import pt.whiteroad.plugins.notifications.config.NotificationUtils

class NotificationsGrailsPlugin {
  // the plugin version
  def version = "0.2.6"
  // the version or versions of Grails the plugin is designed for
  def grailsVersion = "1.3.7 > *"
  // the other plugins this plugin depends on
  def dependsOn = [hibernate: "1.3.7 > *" ,executor: "0.2 > *", quartz: "0.4.1 > *", mail: "1.0 > *", greenmail: "1.2.2 > *"]
  def loadAfter = ['hibernate']

  // resources that are excluded from plugin packaging
  def pluginExcludes = [
          "grails-app/views/error.gsp",
          "grails-app/domain/test/**"

  ]

  def author = "Bruno Félix, Nuno Luís"
  def authorEmail = "felix19350@gmail.com, nuno.lopes.luis@gmail.com"
  def title = "Grails pub/sub notifications plugin"
  def description = '''\\
The grails notifications plugin enables the management of simple text notifications to the users.
These can be delivered through several channels such as e-mail (and in future versions, Apple Push Notifications, and Android C2DM, etc).
'''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/plugin/notifications"

  /*def doWithSpring = {
    //TODO: Hook into grails runtime config
  }*/

  def doWithApplicationContext = { applicationContext ->
    //TODO: Hook this in application context
  }

  def onConfigChange = { event ->
    NotificationUtils.resetConfig()
  }

  /*def onChange = {event ->
    // TODO Implement code that is executed when any artefact that this plugin is
    // watching is modified and reloaded. The event contains: event.source,
    // event.application, event.manager, event.ctx, and event.plugin.
  }

  def doWithDynamicMethods = { ctx ->
    // TODO Implement registering dynamic methods to classes (optional)
  }*/
}
