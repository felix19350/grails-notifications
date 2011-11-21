package test

import pt.whiteroad.plugins.notifications.NotificationTopic
import pt.whiteroad.plugins.notifications.Channel

class TestSubscription {
    static belongsTo = [subscriber: TestSubscriber]

    static hasMany = [channels: Channel]

    NotificationTopic topic
    Boolean disabled = false

    static constraints = {
      topic(nullable: false)
      disabled(nullable: false)
      channels(minSize: 1, validator: {val, obj ->
        def allBelongToSubscriber = true
        val.each{
         allBelongToSubscriber = allBelongToSubscriber && (it in obj.subscriber.channels)
        }
        return allBelongToSubscriber
      })
    }
}
