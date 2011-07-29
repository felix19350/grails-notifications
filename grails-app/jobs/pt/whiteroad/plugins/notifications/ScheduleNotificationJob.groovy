package pt.whiteroad.plugins.notifications;

class ScheduleNotificationJob {

  def concurrent = true
  def notificationService
  def quartzScheduler

  //def group = "NotificationJobs"

  def execute(context){
    Notification notification = Notification.get(context.mergedJobDataMap.get('notificationId') as long)
    notificationService.sendNow(notification)
  }
}
