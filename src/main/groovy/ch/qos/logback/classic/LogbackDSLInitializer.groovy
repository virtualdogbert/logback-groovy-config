package ch.qos.logback.classic

import ch.qos.logback.classic.gaffer.GafferConfigurator

/**
 * This is to allow you to initialize the Logback DSL before the hook for doing that is put back into the main logback project.
 */
class LogbackDSLInitializer {
    static void init(LoggerContext context, InputStream inputStream ) {
        if (inputStream) {
            GafferConfigurator configurator = new GafferConfigurator(context)
            String dslText = inputStream.text
            configurator.run dslText
        }
    }
}
