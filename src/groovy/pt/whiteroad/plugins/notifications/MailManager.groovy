package pt.whiteroad.plugins.notifications

import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import grails.plugin.mail.MailService
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.ApplicationHolder

class MailManager implements IChannelSender{

  private Log log
  private MailService service

  public MailManager(){
    this.log = LogFactory.getLog(MailManager.class)
    ApplicationContext context = (ApplicationContext)ApplicationHolder.getApplication().getMainContext();;

    this.service = (MailService)context.getBean("mailService")
  }

  public void send(Notification notification, String destination){
    try{
      println "Sending notification ${notification?.id} - ${notification?.scheduledDate} at: ${new Date()} "
      service.sendMail{
        to destination
        subject notification.topic.toString()
        body notification.message
      }
    }catch(Exception e){
      e.printStackTrace()
      throw new NotificationException("An error occured sending mail")
    }

  }

}
