package ch.qos.logback.classic

import ch.qos.logback.classic.spi.Configurator
import ch.qos.logback.core.spi.ContextAwareBase
import groovy.transform.CompileStatic

@CompileStatic
class GroovyConfigurator extends ContextAwareBase  implements Configurator{


    @Override
    void configure(LoggerContext loggerContext) {
        LogbackDSLInitializer.init(loggerContext, getClass().getClassLoader().getResourceAsStream("logback-config.groovy"))
    }


}