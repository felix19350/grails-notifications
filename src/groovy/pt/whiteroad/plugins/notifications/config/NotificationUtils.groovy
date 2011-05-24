package pt.whiteroad.plugins.notifications.config

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class NotificationUtils {

  private static final String defaultConfigFile = "DefaultConfig"

  private static ConfigObject config

  /**
   * Accessor for the config object. If it has not been loaded so far,
   * it is loaded.
   * @return ConfigObject
   * */
  public static ConfigObject getConfig(){
    if(!config){
      config = loadConfig()
    }
    return config
  }

  /**
   * Reloads the configuration
   * */
  public static void resetConfig(){
    //println "RELOADING APP CONFIG"
    config = loadConfig()
    setAppConfig(config)
  }

  /**
   * Parse and load configuration. The resulting configuration is the result of
   * merging the default plugin config with the parameters defined in the config.groovy file.
   * @return ConfigObject
   * */
  public static ConfigObject loadConfig(){
    GroovyClassLoader classLoader = new GroovyClassLoader(NotificationUtils.class.getClassLoader());
    def defaultConfig = new ConfigSlurper().parse(classLoader.loadClass(defaultConfigFile))
            .grails.plugins.notifications

    def currentConfig = getAppConfig()

    def mergedConfig = new ConfigObject()
    if(currentConfig == null){
      mergedConfig.putAll(defaultConfig)
    }else{
      mergedConfig.putAll(defaultConfig.merge(currentConfig))
    }

    //println "PLUGIN DEFAULT CONFIG: ${defaultConfig.dump()}"
    //println "APP CONFIG: ${currentConfig.dump()}"
    //println "MERGED CONFIG ${mergedConfig.dump()}"

    return mergedConfig
  }

  static ConfigObject getAppConfig() {
    return CH.config.grails.plugins.notifications
  }
  static void setAppConfig(ConfigObject c) {
    CH.config.grails.plugins.notifications = c
  }

}
