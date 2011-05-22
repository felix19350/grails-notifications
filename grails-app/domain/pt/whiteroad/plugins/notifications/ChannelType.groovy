package pt.whiteroad.plugins.notifications

public enum ChannelType {
  Email("pt.whiteroad.plugins.notifications.MailManager"),    //Email notifications
  Internal("pt.whiteroad.plugins.notifications.InternalNotificationManager"); //In-app notifications

  private String implementingClass;

  private ChannelType(String implementingClass){
    this.implementingClass = implementingClass;
  }

  public void setImplementingClass(String implementingClass){
    this.implementingClass = implementingClass;
  }

  public String getImplementingClass(){
    return this.implementingClass;
  }
}