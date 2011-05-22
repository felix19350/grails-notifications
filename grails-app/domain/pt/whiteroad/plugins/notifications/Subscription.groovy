package pt.whiteroad.plugins.notifications

class Subscription {

    static belongsTo = [subscriber: Subscriber]

    static hasMany = [channels: Channel]

    NotificationTopic topic
  
    static constraints = {
      topic(nullable: false)
      channels(minSize: 1, validator: {val, obj ->
        def allBelongToSubscriber = true
        val.each{
         allBelongToSubscriber = allBelongToSubscriber && (it in obj.subscriber.channels)  
        }
        return allBelongToSubscriber
      })
    }
}
