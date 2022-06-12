


import ch.qos.logback.classic.Level
import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
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
def testEnv = System.getenv('LANGUAGE')
def testProperty = System.getProperty('path.separator')

appender("C", ConsoleAppender) {
  encoder(LayoutWrappingEncoder) {
    layout(PatternLayout) {
      pattern = "%m%n"
    }
  }
}
root Level.WARN, ["C"]
