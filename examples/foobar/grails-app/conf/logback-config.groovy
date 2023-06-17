import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy
import ch.qos.logback.core.status.OnConsoleStatusListener
import grails.util.BuildSettings
import grails.util.Environment
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import java.nio.charset.Charset

//TODO Replace with the config that you are using, adding any notes about what works or
// commented out code with comments of what is not working.

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter

// See http://logback.qos.ch/manual/groovy.html for details on configuration

String LOG_NAME = System.getenv('LOG_NAME') ?: System.getProperty('log.name') ?: 'foobar'
String TARGET_LOG_DIRECTORY = System.getenv('TARGET_LOG_DIRECTORY') ?: System.getProperty('target.log.directory') ?:"."
Level logLevel =  Level.toLevel(System.getenv('LOG_LEVEL') ?: System.getProperty('log.level'), INFO)

statusListener(OnConsoleStatusListener)

if (Environment.isDevelopmentMode()) {
    TARGET_LOG_DIRECTORY = BuildSettings.TARGET_DIR
}

appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')

        pattern =
                '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
                        '%clr(%5p) ' + // Log level
                        '%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
                        '%clr(%-40.40logger{39}){cyan} %clr(:){faint} ' + // Logger
                        '%m%n%wex' // Message
    }
}

appender("ROLLING_FILE", RollingFileAppender) {
    withJansi = true
    file = "${TARGET_LOG_DIRECTORY}/${LOG_NAME}.log"
    append = true

    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${TARGET_LOG_DIRECTORY}/${LOG_NAME}.%d{yyyy-MM-dd}.log"
        minIndex = 1
        maxIndex = 10
    }

    triggeringPolicy(SizeBasedTriggeringPolicy) {
        maxFileSize = System.getenv('MAX_LOG_SIZE') ?: System.getProperty('max.log.size') ?:"10MB"
    }

    encoder(PatternLayoutEncoder) {
        pattern = "%date{ISO8601} %5level [%logger] %message%n"
    }
}

root(logLevel, ['STDOUT','ROLLING_FILE'])


if (Environment.isDevelopmentMode() && TARGET_LOG_DIRECTORY != null) {
    appender("FULL_STACKTRACE", FileAppender) {
        withJansi = true
        file = "${TARGET_LOG_DIRECTORY}/stacktrace.log"
        append = true

        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %message%n"
        }
    }

    logger("StackTrace", ERROR, ["FULL_STACKTRACE"], false)

    // loggers good for debugging but will slow processing down.
    // logger("org.hibernate.SQL", DEBUG, ["STDOUT"], false)
    // logger("org.hibernate.type.descriptor.sql.BasicBinder", TRACE, ["STDOUT"], false)
    // logger("org.springframework.security", TRACE, ['STDOUT'], false)

}

//logger for default package
logger('foobar', INFO, ['STDOUT','ROLLING_FILE'], false)

//loggers for grails related packages. Level can be turned up to debug.
logger("grails.app", INFO, ['STDOUT','ROLLING_FILE'], false)
logger('org.grails.web.util', WARN, ['STDOUT','ROLLING_FILE'], false)  //  controllers
logger('grails.web.pages', WARN, ['STDOUT','ROLLING_FILE'], false) //  GSP
logger('org.grails.web.sitemesh', WARN, ['STDOUT','ROLLING_FILE'], false) //  layouts
logger('org.grails.web.mapping', WARN, ['STDOUT','ROLLING_FILE'], false) // URL mapping
logger('org.grails.commons', ERROR, ['STDOUT','ROLLING_FILE'], false) // core / classloading
logger('org.grails.plugins', ERROR, ['STDOUT','ROLLING_FILE'], false) // plugins
logger('org.grails.orm.hibernate', ERROR, ['STDOUT','ROLLING_FILE'], false) // hibernate
logger('org.springframework', ERROR, ['STDOUT','ROLLING_FILE'], false)
logger('org.hibernate', ERROR, ['STDOUT','ROLLING_FILE'], false)