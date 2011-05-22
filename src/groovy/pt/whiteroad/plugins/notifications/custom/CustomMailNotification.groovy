package pt.whiteroad.plugins.notifications.custom

import pt.whiteroad.plugins.notifications.NotificationException
import pt.whiteroad.plugins.notifications.Notification
import pt.whiteroad.plugins.notifications.IChannelSender
import grails.plugin.mail.MailService
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.context.ApplicationContext


class CustomMailNotification implements IChannelSender{

  private MailService service

  public CustomMailNotification(){
    ApplicationContext context = (ApplicationContext)ApplicationHolder.getApplication().getMainContext();
    this.service = (MailService)context.getBean("mailService")
  }

  def void send(Notification notification, String destination) {
    try{
      def message = "MANGLED MESSAGE: " + notification.message

      service.sendMail{
        to destination
        subject notification.topic.toString()
        body message
      }
    }catch(Exception e){
      e.printStackTrace()
      throw new NotificationException("Exception occured during TestCustomChannel.send")
    }
  }
}
