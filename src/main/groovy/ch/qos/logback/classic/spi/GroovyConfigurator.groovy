package ch.qos.logback.classic.spi

import ch.qos.logback.classic.LogbackDSLInitializer
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.Context
import ch.qos.logback.core.status.Status
import groovy.transform.CompileStatic

@CompileStatic
class GroovyConfigurator implements Configurator{
    Context context


    @Override
    void configure(LoggerContext loggerContext) {
        LogbackDSLInitializer.init(loggerContext, getClass().getClassLoader().getResourceAsStream("logback-config.groovy"))
    }

    @Override
    void setContext(Context context) {
        this.context = context
    }

    @Override
    Context getContext() {
        return context
    }

    @Override
    void addStatus(Status status) {

    }

    @Override
    void addInfo(String msg) {

    }

    @Override
    void addInfo(String msg, Throwable ex) {

    }

    @Override
    void addWarn(String msg) {

    }

    @Override
    void addWarn(String msg, Throwable ex) {

    }

    @Override
    void addError(String msg) {

    }

    @Override
    void addError(String msg, Throwable ex) {

    }
}