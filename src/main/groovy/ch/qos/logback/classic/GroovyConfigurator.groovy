/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic

import ch.qos.logback.classic.spi.Configurator
import ch.qos.logback.core.spi.ContextAwareBase
import groovy.transform.CompileStatic

/**
 * This class gets called by Logback-classic to configure logback using the Groovy Logback DSL. This
 */
@CompileStatic
class GroovyConfigurator extends ContextAwareBase implements Configurator {

    /**
     * This configures logback using the Groovy Logback DSL.
     *
     * @param loggerContext Logback context to configure.
     */
    @Override
    void configure(LoggerContext loggerContext) {
        String configName = System.getProperty('logback.config.file') ?: System.getenv('LOGBACK_CONFIG_FILE') ?: "logback-config.groovy"
        String externalConfig = System.getProperty('logback.config.external.file') ?: System.getenv('LOGBACK_CONFIG_EXTERNAL_FILE')
        InputStream inputStream = getClass()?.getClassLoader()?.getResourceAsStream(configName)

        if (externalConfig) {
            File file = new File(externalConfig)

            try {
                if (file && file.exists()) {
                    inputStream = file.newInputStream()
                    LogbackDSLInitializer.init(loggerContext, inputStream)
                    loggerContext.putObject('org.springframework.boot.logging.LoggingSystem', new Object())
                    return
                }
            }catch(Exception e){
                println "External config for ${externalConfig} failed falling back to internal config"
                e.printStackTrace()
            }

        }

        if (loggerContext && inputStream) {
            LogbackDSLInitializer.init(loggerContext, inputStream)

            //Added so that Spring Boot/Grails won't override this config with the Spring Boot defaults.
            loggerContext.putObject('org.springframework.boot.logging.LoggingSystem', new Object())
            return
        }

        println "logback-groovy-config dependency is installed but no groovy logback config was found in resources."
        println "Filenames checked: System.getProperty('logback.config.file'), System.getenv('LOGBACK_CONFIG_FILE'), logback-config.groovy."
    }
}
