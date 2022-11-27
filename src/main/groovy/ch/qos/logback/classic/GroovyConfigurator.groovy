package ch.qos.logback.classic

import ch.qos.logback.classic.spi.Configurator
import ch.qos.logback.core.spi.ContextAwareBase
import groovy.transform.CompileStatic

@CompileStatic
class GroovyConfigurator extends ContextAwareBase implements Configurator {


    @Override
    void configure(LoggerContext loggerContext) {
        LoggerContext context = loggerContext
        String configName = System.getProperty('logback.config.file') ?: System.getenv('LOGBACK_CONFIG_FILE') ?: "logback-config.groovy"
        String externalConfig = System.getProperty('logback.config.external.file') ?: System.getenv('LOGBACK_CONFIG_EXTERNAL_FILE')
        InputStream inputStream = getClass()?.getClassLoader()?.getResourceAsStream(configName)

        if (externalConfig) {
            File file = new File(externalConfig)

            try {
                if (file && file.exists()) {
                    inputStream = file.newInputStream()
                    LogbackDSLInitializer.init(context, inputStream)
                    loggerContext.putObject('org.springframework.boot.logging.LoggingSystem', new Object())
                    return
                }
            }catch(Exception e){
                println "External config for ${externalConfig} failed falling back to internal config"
                e.printStackTrace()
            }

        }

        if (context && inputStream) {
            LogbackDSLInitializer.init(context, inputStream)
            loggerContext.putObject('org.springframework.boot.logging.LoggingSystem', new Object())
            return
        }

        println "logback-groovy-config dependency is installed but no groovy logback config was found in resources."
        println "Filenames checked checked: System.getProperty('logback.config.file'), System.getenv('LOGBACK_CONFIG_FILE'), logback-config.groovy."
    }
}