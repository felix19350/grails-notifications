package pt.whiteroad.plugins.notifications

class NotificationTopic {

    String topic

    static constraints = {
      topic(blank: false, size: 1 .. 256, unique: true)
    }

  public String toString(){
    return topic
  }
}
