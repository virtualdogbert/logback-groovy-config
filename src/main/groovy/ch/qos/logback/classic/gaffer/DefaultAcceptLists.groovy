package ch.qos.logback.classic.gaffer

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.boolex.Matcher
import groovy.transform.CompileStatic

import static org.codehaus.groovy.syntax.Types.*

/**
 * Default Accept Lists for the AST Customizer used to limit what can be done in the Groovy DSL for Logback.
 */
@CompileStatic
class DefaultAcceptLists {

    /**
     * This Limits the acceptable tokens that can bs use in the Groovy DSL file. Like +-/% etc.
     */
    static final List tokensAcceptList = [
            DIVIDE, PLUS, MINUS,
            MULTIPLY, MOD, POWER,
            PLUS_PLUS, MINUS_MINUS,
            PLUS_EQUAL, LOGICAL_AND, COMPARE_EQUAL,
            COMPARE_NOT_EQUAL, COMPARE_LESS_THAN, COMPARE_LESS_THAN_EQUAL,
            LOGICAL_OR, NOT, COMPARE_GREATER_THAN, COMPARE_GREATER_THAN_EQUAL,
            EQUALS, COMPARE_NOT_EQUAL, COMPARE_EQUAL, KEYWORD_INSTANCEOF
    ]

    /**
     * This limits the acceptable constant types that can be used in the DSL
     */
    static final List constantTypesClassesAcceptList = [
            Object,
            Integer,
            Float,
            Long,
            Double,
            BigDecimal,
            String,
            Integer.TYPE,
            Long.TYPE,
            Float.TYPE,
            Double.TYPE,
            Boolean.TYPE,
            Matcher,
            LoggerContext
    ]

    /**
     * This limits the acceptable static imports for the Groovy DSL
     */
    static final List<String> staticImportsAcceptList = [
            'java.nio.charset.Charset.forName',
            'java.lang.Object.conversionRule',
            'java.lang.Object.appender',
            'java.lang.Object.layout',
            'java.lang.Object.appenderRef',
            'java.lang.Object.encoder',
            'java.lang.Object.filter',
            'java.lang.Object.evaluator',
            'java.lang.Object.logger',
            'java.lang.Object.root',
            'ch.qos.logback.classic.Level',
            'ch.qos.logback.classic.Level.OFF',
            'ch.qos.logback.classic.Level.ERROR',
            'ch.qos.logback.classic.Level.WARN',
            'ch.qos.logback.classic.Level.INFO',
            'ch.qos.logback.classic.Level.DEBUG',
            'ch.qos.logback.classic.Level.TRACE',
            'ch.qos.logback.classic.Level.ALL',
            'ch.qos.logback.core.spi.FilterReply',
            'ch.qos.logback.core.spi.FilterReply.DENY',
            'ch.qos.logback.core.spi.FilterReply.NEUTRAL',
            'ch.qos.logback.core.spi.FilterReply.ACCEPT',
            'ch.qos.logback.core.boolex.Matcher.start',
            'java.lang.Object.putProperty',
            'java.lang.Object.getProperty',
            'java.lang.System',
            'java.lang.System.getenv',
            'java.lang.System.getProperty'
    ]

    /**
     * This limits the acceptable imports for the Groovy DSL.
     */
    static final List<String> importsAcceptList = [
            'ch.qos.logback.core.testUtil.StringListAppender',
            'java.lang.Object',
            'org.springframework.beans.factory.annotation.Autowired', //Grails requires this for some reason, but you can not autowire any service because those classes are not on the import list.
            'java.nio.charset.Charset.forName',
            'com.logentries.logback.LogentriesAppender',
            'grails.util.BuildSettings',
            'grails.util.Environment',
            'org.slf4j.MDC',
            'org.springframework.boot.logging.logback.ColorConverter',
            'org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter',
            'java.nio.charset.Charset',
            'java.nio.charset.StandardCharsets',

            'ch.qos.logback.classic.AsyncAppender',
            'ch.qos.logback.classic.boolex.JaninoEventEvaluator',
            'ch.qos.logback.classic.encoder.PatternLayoutEncoder',
            'ch.qos.logback.classic.Level',
            'ch.qos.logback.core.ConsoleAppender',
            'ch.qos.logback.core.FileAppender',
            'ch.qos.logback.classic.PatternLayout',
            'ch.qos.logback.core.encoder.LayoutWrappingEncoder',
            'ch.qos.logback.classic.LoggerContext',
            'ch.qos.logback.core.boolex.Matcher',
            'ch.qos.logback.core.filter.EvaluatorFilter',
            'java.lang.System',
            'java.lang.System.getenv',
            'java.lang.System.getProperty',
    ]

    /**
     * This limits the acceptable star imports for the Groovy DSL.
     */
    static final List<String> starImportsAcceptList = []

    /**
     * This limits the acceptable star static imports for the Groovy DSL.
     */
    static final List<String> staticStarImportsAcceptList = [
            'grails.util.Environment',
            'io.micronaut.context.env.Environment'
    ]
}
