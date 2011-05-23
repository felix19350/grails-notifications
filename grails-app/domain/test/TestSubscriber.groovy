package test

import pt.whiteroad.plugins.notifications.Channel

class TestSubscriber {

  static hasMany = [subscriptions: TestSubscription, channels: Channel]

  String alias

  static constraints = {
    alias(nullable: false, unique: true)
  }

  static mapping = {
    subscriptions lazy: false
    channels lazy: false
  }

  /**
   * Convenience method to create a subscriber. User defined classes may need to
   * change this method.
   * @param alias - The alias of the subscriber
   * @param channels - A set of communication channels associated with the subscriber
   * @return subscriber - The instance of the created subscriber, or null if there is an error.
   * */
  static TestSubscriber createSubscriber(String alias, Collection<Channel> channels){
    TestSubscriber.withNewSession{ session ->
      TestSubscriber.withTransaction{status ->
        try{
          def toInsert = []

          channels.each{
            if(!it.id){
              toInsert << it.save()
            }else{
              toInsert << it
            }
          }

          def subscriber = new TestSubscriber(alias: alias)
          toInsert.each{
            subscriber.addToChannels(it)
          }

          if(!subscriber.save()){
            subscriber.errors.each{
              System.err.println it
            }
            throw new RuntimeException()
          }

          return subscriber
        }catch(Exception e){
          e.printStackTrace()
          status.setRollbackOnly()
          return null
        }
      }
    }
  }

}
