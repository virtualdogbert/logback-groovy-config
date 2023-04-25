package ch.qos.logback.classic.gaffer

import ch.qos.logback.core.boolex.Matcher
import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.control.customizers.SecureASTCustomizer

@CompileStatic
class ScriptExpressionChecker implements SecureASTCustomizer.ExpressionChecker {


    private static final List<String> AllowedStringMethods = [
            'bitwiseNegate', 'capitalize', 'center',
            'charAt', 'chars', 'codePointAt',
            'codePointBefore', 'codePointCount', 'codePoints',
            'compareTo', 'compareToIgnoreCase', 'concat',
            'contains', 'contains', 'contentEquals',
            'copyValueOf', 'count', 'decodeBase64',
            'denormalize', 'eachLine', 'eachMatch',
            'endsWith', 'equals', 'equalsIgnoreCase',
            'expand', 'expandLine', 'find',
            'findAll', 'format', 'getAt',
            'getChars', 'getChars', 'hashCode',
            'indexOf', 'intern', 'isAllWhitespace',
            'isBigDecimal', 'isBigInteger', 'isBlank',
            'isCase', 'isDouble', 'isEmpty',
            'isFloat', 'isInteger', 'isLong',
            'isNumber', 'join', 'lastIndexOf',
            'leftShift', 'length', 'lines',
            'matches', 'matches', 'minus',
            'multiply', 'next', 'normalize',
            'offsetByCodePoints', 'padLeft', 'padRight',
            'plus', 'previous', 'readLines',
            'regionMatches', 'repeat', 'replace',
            'replaceAll', 'replaceAll', 'replaceFirst',
            'reverse', 'size', 'split',
            'splitEachLine', 'startsWith', 'strip',
            'stripIndent', 'stripLeading', 'stripMargin',
            'stripTrailing', 'subSequence', 'substring',
            'toBigDecimal', 'toBigInteger', 'toBoolean',
            'toCharacter', 'toCharArray', 'toDouble',
            'toFloat', 'toInteger', 'tokenize',
            'toList', 'toLong', 'toLowerCase',
            'toShort', 'toString', 'toUpperCase',
            'tr', 'trim', 'unexpand',
            'unexpandLine', 'valueOf', 'start',


    ]
    private static final List<String> AllowedObjectMethods = [
            'clone', 'equals', 'toString',
            'any', 'asBoolean', 'collect',
            'contains', 'count', 'each',
            'eachWithIndex', 'equals', 'every',
            'find', 'findIndexOf', 'findIndexValues',
            'findLastIndexOf', 'findResult', 'flatten',
            'getAt', 'grep', 'groupBy',
            'inject', 'is', 'join',
            'putAt', 'size', 'sum',
            'with',


            'conversionRule', 'appender', 'encoder',
            'forName', 'isDevelopmentMode', 'logger',
            'root', 'layout', 'appenderRef',
            'putProperty', 'getProperty', 'filter',
            'evaluator'
    ]

    private static final List<String> AllowedSystemMethods = [
            'getenv',
            'currentTimeMillis',
            'nanoTime',
            'lineSeparator'
    ]


    private static final List<String> AllowedMatcherMethods = [
            'start'
    ]

    @Override
    boolean isAuthorized(Expression expression) {
        //Prohibits annotations like @Grab and @ASTTest which could be used execute code. While this could be changed to allow list
        // I can't think of any reason you would need annotations in logback config.
        if (expression.getAnnotations().size() > 0) {
            println expression.getAnnotations().size()
            return false
        }

        if (expression && expression instanceof MethodCallExpression) {
            MethodCallExpression methodCall = (MethodCallExpression) expression
            ConstantExpression methodExpression = (ConstantExpression) methodCall?.method


            if (methodExpression && methodCall.objectExpression.type.name == Matcher.class.name &&
                !AllowedMatcherMethods.contains(methodExpression?.value)) {

                return false
            }

            if (methodExpression && methodCall.objectExpression.type.name == String.class.name &&
                !AllowedStringMethods.contains(methodExpression?.value)) {

                return false
            }

            if (methodExpression && methodCall.objectExpression.type.name == Object.class.name &&
                !AllowedObjectMethods.contains(methodExpression?.value)) {

                //Some Method calls come wrapped in a objectExpression of type object, but have sub objectExpressions
                if (methodCall.objectExpression.hasProperty('objectExpression')) {
                    MethodCallExpression methodCallExpression = (MethodCallExpression) methodCall.objectExpression

                    //Checking the sub object expression for System calls.
                    if (methodCallExpression.objectExpression.type == System.class.name && !AllowedSystemMethods.contains(methodExpression?.value)) {
                        return false
                    }

                    //Checking the subcall for calls from Strings.
                    if (methodCallExpression.objectExpression.type == String.class.name &&
                        !AllowedStringMethods.contains(methodExpression?.value)) {

                        return false
                    }

                    //All other subcalls are deemed ok for now...
                    return true
                }

                //The method call from an object does not have a sub call and the method is now allowed
                return false
            }

        }

        //All checks passed the method is allowed
        return true
    }
}