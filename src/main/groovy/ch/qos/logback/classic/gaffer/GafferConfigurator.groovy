package ch.qos.logback.classic.gaffer

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil
import ch.qos.logback.core.status.OnConsoleStatusListener
import ch.qos.logback.core.util.ContextUtil
import ch.qos.logback.core.util.OptionHelper
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.control.customizers.SecureASTCustomizer

import static DefaultAcceptLists.*

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
class GafferConfigurator {

    LoggerContext context

    static final String DEBUG_SYSTEM_PROPERTY_KEY = "logback.debug"

    GafferConfigurator(LoggerContext context) {
        this.context = context
    }

    protected void informContextOfURLUsedForConfiguration(URL url) {
        ConfigurationWatchListUtil.setMainWatchURL(context, url)
    }

    void run(URL url) {
        informContextOfURLUsedForConfiguration(url)
        run(url.text)
    }

    void run(File file) {
        informContextOfURLUsedForConfiguration(file.toURI().toURL())
        run(file.text)
    }

    void run(String dslText) {
        ConfigObject config
        Set<String> importsList = [] as Set
        Set<String> staticImportsList = [] as Set
        Set<String> starImportsList = [] as Set
        Set<String> staticStarImportsList = [] as Set
        Set<String> tokens = [] as Set
        Set<String> constantTypesClasses = [] as Set

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("logbackCompiler.groovy")

        if (inputStream) {
            ConfigSlurper configSlurper = new ConfigSlurper()
            config = configSlurper.parse(inputStream.text)
        }

        importsList.addAll(importsAcceptList)
        staticImportsList.addAll(staticImportsAcceptList)
        starImportsList.addAll(starImportsAcceptList)
        staticStarImportsList.addAll(staticStarImportsAcceptList)
        tokens.addAll(tokensAcceptList)
        constantTypesClasses.addAll(constantTypesClassesAcceptList)

        importsList.addAll(config?.importsAcceptList ?: [])
        staticImportsList.addAll(config?.staticImportsAcceptList ?: [])
        starImportsList.addAll(config?.starImportsAcceptList ?: [])
        staticStarImportsList.addAll(config?.staticStarImportsAcceptList ?: [])
        tokens.addAll(config?.tokensAcceptList ?: [])
        constantTypesClasses.addAll(config?.constantTypesClassesAcceptList ?: [])


        Binding binding = new Binding()
        binding.setProperty("hostname", ContextUtil.localHostName)

        // Define SecureASTCustomizer to limit allowed
        // language syntax in scripts.
        final SecureASTCustomizer astCustomizer = new SecureASTCustomizer(
                methodDefinitionAllowed: false,
                closuresAllowed: true,
                packageAllowed: false,
                indirectImportCheckEnabled: true,
                importsWhitelist: importsList.toList(),
                staticImportsWhitelist: staticImportsList.toList(),
                tokensWhitelist: tokens.toList(),
                constantTypesClassesWhiteList: constantTypesClasses.toList(),
                starImportsWhitelist: starImportsList.toList(),
                staticStarImportsWhitelist: staticStarImportsList.toList()
        )

        astCustomizer.addExpressionCheckers(new ScriptExpressionChecker())

        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.addCompilationCustomizers(importCustomizer(), astCustomizer)

        String debugAttrib = System.getProperty(DEBUG_SYSTEM_PROPERTY_KEY)

        if (OptionHelper.isEmpty(debugAttrib) || debugAttrib.equalsIgnoreCase("false") || debugAttrib.equalsIgnoreCase("null")) {
            // For now, Groovy/Gaffer configuration DSL does not support "debug" attribute. But in order to keep
            // the conditional logic identical to that in XML/Joran, we have this empty block.
        } else {
            OnConsoleStatusListener.addNewInstanceToContext(context)
        }

        // caller data should take into account groovy frames
        new ContextUtil(context).addGroovyPackages(context.getFrameworkPackages())

        Script dslScript = new GroovyShell(binding, configuration).parse(dslText)

        dslScript.metaClass.mixin(ConfigurationDelegate)
        dslScript.setContext(context)
        dslScript.metaClass.getDeclaredOrigin = { dslScript }

        dslScript.run()
    }

    protected ImportCustomizer importCustomizer() {
        ImportCustomizer customizer = new ImportCustomizer()

        customizer.addImports(PatternLayoutEncoder.class.name)
        customizer.addImports(LoggerContext.name)

        customizer.addStaticStars(Level.class.name)

        customizer.addStaticImport('OFF', Level.class.name, 'OFF')
        customizer.addStaticImport('ERROR', Level.class.name, 'ERROR')
        customizer.addStaticImport('WARN', Level.class.name, 'WARN')
        customizer.addStaticImport('INFO', Level.class.name, 'INFO')
        customizer.addStaticImport('DEBUG', Level.class.name, 'DEBUG')
        customizer.addStaticImport('TRACE', Level.class.name, 'TRACE')
        customizer.addStaticImport('ALL', Level.class.name, 'ALL')

        customizer
    }

}
