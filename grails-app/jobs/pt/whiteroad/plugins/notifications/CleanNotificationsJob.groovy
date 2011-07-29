package pt.whiteroad.plugins.notifications;

class CleanNotificationsJob {

  def concurrent = false
  def notificationService
  def quartzScheduler

  static triggers = {
    cron name: 'Trigger_12_Hour', cronExpression: "0 0 0/12 * * ?" 
  }

  //def group = "NotificationJobs"

  def execute(context){
    def notifications = notificationService.collectDelayedNotifications()
    notifications.each{
      notificationService.sendNotification(it)
    }
  }
}
