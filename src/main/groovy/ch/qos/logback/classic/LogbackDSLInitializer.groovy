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
