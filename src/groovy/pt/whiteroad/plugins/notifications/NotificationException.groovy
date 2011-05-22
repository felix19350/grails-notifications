package pt.whiteroad.plugins.notifications

/**
 * Default exception for dealing with errors on notifications.
 * */
class NotificationException extends Exception {

  public NotificationException(){
    super();
  }

  public NotificationException(String message){
    super(message);
  }
}
