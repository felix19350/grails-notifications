package pt.whiteroad.plugins.notifications;

class ScheduleNotificationJob {

  def concurrent = true
  def notificationService
  def quartzScheduler

  static triggers = {
    //No triggers - this job should only be called programaticaly
  }

  def execute(context){
    Notification notification = Notification.get(context.mergedJobDataMap.get('notificationId') as long)
    notificationService.sendNow(notification)
  }
}