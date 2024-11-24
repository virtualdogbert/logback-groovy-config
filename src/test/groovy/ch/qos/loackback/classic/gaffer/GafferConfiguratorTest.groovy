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
package ch.qos.loackback.classic.gaffer

import ch.qos.logback.classic.*
import ch.qos.logback.classic.boolex.JaninoEventEvaluator
import ch.qos.logback.classic.gaffer.GafferConfigurator
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.boolex.Matcher
import ch.qos.logback.core.filter.EvaluatorFilter
import ch.qos.logback.core.testUtil.ClassicTestConstants
import ch.qos.logback.core.testUtil.RandomUtil
import ch.qos.logback.core.testUtil.SampleConverter
import ch.qos.logback.core.testUtil.StringListAppender
import ch.qos.logback.core.util.StatusPrinter
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static junit.framework.Assert.*
import static org.junit.Assert.assertNotEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
/**
 * @author Ceki G&uuml;c&uuml;
 */
class GafferConfiguratorTest {

    LoggerContext context = new LoggerContext();
    Logger root   = context.getLogger(Logger.ROOT_LOGGER_NAME)
    Logger logger = context.getLogger(this.getClass())
    int    diff   = RandomUtil.getPositiveInt();
    GafferConfigurator configurator = new GafferConfigurator(context);
    final shouldFail = new GroovyTestCase().&shouldFail

    @Before
    void setUp() {

    }

    @Test
    void smoke() {
        File file = new File(ClassicTestConstants.GAFFER_INPUT_PREFIX + "smoke.groovy")
        String dslText = file.text
        configurator.run dslText
        Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
        assertEquals(Level.WARN, root.level)
        assertNotNull(root.getAppender("C"))
        ConsoleAppender ca = root.getAppender("C")
        assertNotNull(ca.encoder)
        assertNotNull(ca.encoder.layout)
        PatternLayout layout = ca.encoder.layout
        assertEquals("%m%n", layout.pattern)
    }

    @Test
    void smoke2() {
        File file = new File(ClassicTestConstants.GAFFER_INPUT_PREFIX + "smoke2.groovy")
        String dslText = file.text
        context.putProperty('path.separator', '/')
        configurator.run dslText
        Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
        assertEquals(Level.WARN, root.level)
        assertNotNull(root.getAppender("C"))
        ConsoleAppender ca = root.getAppender("C")
        assertNotNull(ca.encoder)
        assertNotNull(ca.encoder.layout)
        PatternLayout layout = ca.encoder.layout
        assertEquals("%m%n", layout.pattern)
    }

    @Test
    void contextName() {
        String dslText = "context.name = 'a'"
        configurator.run dslText
        assertEquals("a", context.name)
    }

    @Test
    void contextProperty() {
        String dslText = "context.putProperty('x', 'a')"
        configurator.run dslText
        assertEquals("a", context.getProperty("x"))
    }

    @Test
    void conversionRule() {
        File file = new File(ClassicTestConstants.GAFFER_INPUT_PREFIX + "conversionRule.groovy")
        String dslText = file.text
        configurator.run dslText

        StringListAppender<ILoggingEvent> sla = (StringListAppender<ILoggingEvent>) root.getAppender("LIST");
        assertNotNull(sla);
        assertEquals(0, sla.strList.size());

        assertEquals(Level.DEBUG, root.level);

        String msg = "Simon says";
        logger.debug(msg);
        StatusPrinter.print context
        assertEquals(1, sla.strList.size());
        assertEquals(SampleConverter.SAMPLE_STR + " - " + msg, sla.strList.get(0));
    }

    @Test
    void evaluatorWithMatcher() {
        File file = new File(ClassicTestConstants.GAFFER_INPUT_PREFIX + "evaluatorWithMatcher.groovy")
        String dslText = file.text
        configurator.run dslText

        ConsoleAppender ca = (ConsoleAppender) root.getAppender("STDOUT");
        assertTrue ca.isStarted()

        EvaluatorFilter ef = ca.getCopyOfAttachedFiltersList()[0];
        assertTrue ef.isStarted()

        JaninoEventEvaluator jee = ef.evaluator
        assertTrue jee.isStarted()
        Matcher m = jee.matcherList[0]
        assertTrue m.isStarted()
    }

    @Test
    void propertyCascading0() {
        File file = new File(ClassicTestConstants.GAFFER_INPUT_PREFIX + "propertyCascading0.groovy")
        String dslText = file.text
        configurator.run dslText

        ConsoleAppender ca = (ConsoleAppender) root.getAppender("STDOUT");
        assertTrue ca.isStarted()

        assertEquals("HELLO %m%n", ca.encoder.layout.pattern)
    }

    @Test
    void propertyCascading1() {
        File file = new File(ClassicTestConstants.GAFFER_INPUT_PREFIX + "propertyCascading1.groovy")
        String dslText = file.text
        configurator.run dslText

        ConsoleAppender ca = (ConsoleAppender) root.getAppender("STDOUT");
        assertTrue ca.isStarted()
        assertEquals("HELLO %m%n", ca.encoder.getLayout().pattern)
    }

    @Test
    void propertyCascading2() {
        context.putProperty("p", "HELLO");
        File file = new File(ClassicTestConstants.GAFFER_INPUT_PREFIX + "propertyCascading2.groovy")
        String dslText = file.text
        configurator.run dslText

        ConsoleAppender ca = (ConsoleAppender) root.getAppender("STDOUT");
        assertTrue ca.isStarted()
        assertEquals("HELLO %m%n", ca.encoder.getLayout().pattern)
    }


    @Test
    @Ignore
    void receiver() {
        File file = new File(ClassicTestConstants.GAFFER_INPUT_PREFIX + "propertyCascading2.groovy")
        String dslText = file.text
        configurator.run dslText
    }

    @Test
    void appenderRefShouldWork() {
        File file = new File(ClassicTestConstants.GAFFER_INPUT_PREFIX + "asyncAppender.groovy")
        configurator.run file.text

        def aa = (AsyncAppender) root.getAppender('STDOUT-ASYNC');
        assertTrue aa.isStarted()
        def stdout = (ConsoleAppender) aa.getAppender('STDOUT')
        assertNotNull stdout
    }

    @Test
    void appenderRefWithNonAppenderAttachable() {
        String message = shouldFail(IllegalArgumentException) {
            File file = new File(ClassicTestConstants.GAFFER_INPUT_PREFIX + "appenderRefWithNonAppenderAttachable.groovy")
            configurator.run file.text
        }
        assertEquals message, "ch.qos.logback.core.ConsoleAppender does not implement ch.qos.logback.core.spi.AppenderAttachable."
    }

    @Test
    void withDifferentContextClassLoader() {

        // Given that there is a logbackCompiler.groovy resource, which configurator will use ConfigSlurper to load,
        InputStream inputStream = configurator.class.classLoader.getResourceAsStream("logbackCompiler.groovy")
        assertNotNull(inputStream)

        // logbackCompiler.groovy implicitly extends groovy.lang.Script.
        assert Script.isAssignableFrom(new GroovyClassLoader().parseClass(inputStream.text))

        // Normally the currentThread()'s contextClassLoader has the same Script class that ConfigSlurper has
        // in its parse(Script) parameter, so it is able to find that method.
        Thread ct = Thread.currentThread()
        URLClassLoader originalCL = (URLClassLoader) ct.contextClassLoader
        String gls = 'groovy.lang.Script'
        Class configSlurperScriptClass = ConfigSlurper.classLoader.loadClass(gls)
        assertEquals(originalCL.loadClass(gls), configSlurperScriptClass)

        // For that matter, those two ClassLoaders are the same (in this test, at least).
        assertEquals(originalCL, ConfigSlurper.classLoader)

        // However, if the currentThread()'s contextClassLoader is changed, so that no longer holds true
        // (as happens in gradle when compiling test classes for a certain Grails project with an AST that
        // triggers an init of a class with a static LOG property that triggers the configuration of logback),
        ClassLoader otherCL = new URLClassLoader(originalCL.URLs, (ClassLoader) null)
        assertNotEquals(otherCL.loadClass(gls), configSlurperScriptClass)
        ct.contextClassLoader = otherCL

        try {
            // when the configurator is run with a logbackCompiler.groovy resource in the environment,
            configurator.run "foo = 42"

            // then the following Exception is no longer thrown (although JUnit 4 does not have an assert for this):
            // groovy.lang.MissingMethodException: No signature of method: groovy.util.ConfigSlurper.parse() is applicable for argument types: (Script_eaf6d1dbdc2100d09c319091a0213ade) values: [Script_eaf6d1dbdc2100d09c319091a0213ade@1af7f54a]
            // Possible solutions: parse(groovy.lang.Script), parse(java.lang.Class), parse(java.lang.String), parse(java.net.URL), parse(java.util.Properties), parse(groovy.lang.Script, java.net.URL)
            // ...
            //	at groovy.util.ConfigSlurper.parse(ConfigSlurper.groovy:153)
            // ...
            //	at groovy.util.ConfigSlurper.parse(ConfigSlurper.groovy:144)
            // ...
            //	at ch.qos.logback.classic.gaffer.GafferConfigurator.run(GafferConfigurator.groovy:66)
            // ...
        } finally {
            ct.contextClassLoader = originalCL
        }
    }
}
