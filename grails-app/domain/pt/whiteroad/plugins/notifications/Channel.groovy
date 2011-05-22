package pt.whiteroad.plugins.notifications

class Channel {

    String destination
    String channelImpl

    static constraints = {
      destination(nullable: false, blank: false)
      channelImpl(nullable: true)
    }
}
