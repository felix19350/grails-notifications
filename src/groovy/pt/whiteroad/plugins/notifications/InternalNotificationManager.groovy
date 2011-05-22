package pt.whiteroad.plugins.notifications

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class InternalNotificationManager implements IChannelSender{

  private Log log
  private InternalMsgService service

  public InternalNotificationManager(){
    this.log = LogFactory.getLog(InternalMsgService.class)
    this.service = new InternalMsgService()
  }

  def void send(Notification notification, String destination) throws NotificationException {
    try{
      service.sendInternalMessage(destination, notification.topic.toString(), notification.message)
    }catch(Exception e){
      log.debug e.getMessage()
      throw new NotificationException("An error has occured during the sendInternalMessage method.")
    }

  }


}
