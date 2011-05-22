package pt.whiteroad.plugins.notifications

class Subscriber {

    static hasMany = [subscriptions: Subscription, channels: Channel]

    String alias

    static constraints = {
      alias(nullable: false, unique: true)
    }
}
