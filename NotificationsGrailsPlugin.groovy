import pt.whiteroad.plugins.notifications.NotificationTopic

class NotificationsGrailsPlugin {
  // the plugin version
  def version = "0.2.2"
  // the version or versions of Grails the plugin is designed for
  def grailsVersion = "1.3.7 > *"
  // the other plugins this plugin depends on
  def dependsOn = [executor: "* > 0.2", quartz: "* > 0.4.1", mail: "* > 1.0"]

  // resources that are excluded from plugin packaging
  def pluginExcludes = [
          "grails-app/views/error.gsp"
  ]

  def author = "Bruno Félix, Nuno Luís"
  def authorEmail = "felix19350@gmail.com, nuno.lopes.luis@gmail.com"
  def title = "Grails pub/sub notifications plugin"
  def description = '''\\
The grails notifications plugin enables the management of simple text notifications to the users.
These can be delivered through several channels such as e-mail (and in future versions, Apple Push Notifications, and Android C2DM, etc).
'''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/plugin/pubsub-notifications"

  /*def doWithSpring = {
    //TODO: Hook into grails runtime config
  }*/

  def doWithApplicationContext = { applicationContext ->
    def config = application.config.grails.plugins.notifications

    //Bootstrap any number of Notification topics required by the application
    String[] topics = config?.topics ? (config.topics as String).split(",") : []

    topics.each{
      NotificationTopic.withNewSession {
        new NotificationTopic(topic: it).save(flush: true)
      }
    }

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
