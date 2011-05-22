package pt.whiteroad.plugins.notifications

public interface IChannelSender {

  /**
   * Immediately sends the notification. Scheduling it if necessary.
   * @param notification - The notification that is to be sent
   * @param destination - A parameter that depends on the underlying implementation, that represents
   * the destination of the notification in the context of a given channel.
   * @throws NotificationException - An exception that is thrown when there is an error sending the notification.
   * */
  public void send(Notification notification, String destination) throws NotificationException;

}