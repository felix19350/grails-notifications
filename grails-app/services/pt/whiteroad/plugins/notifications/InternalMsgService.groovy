package pt.whiteroad.plugins.notifications

class InternalMsgService {

  static transactional = true

  //MOCK ONLY
  def sendInternalMessage(recipient, topic, message) {
      println "Sending ${topic} to ${recipient} with message: ${message}"
  }
}
