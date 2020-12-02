package com.lzx.frame.core.aspect.log;

import com.lzx.frame.common.annotation.log.Log;
import com.lzx.frame.common.toolkit.ArrayUtils;
import com.lzx.frame.common.toolkit.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * The class Log aspect.
 */
@Aspect
@Component
public class LogAspect {

    private ThreadLocal<Date> threadLocal = new ThreadLocal<>();

    //private static final int MAX_SIZE = 2000;

    /**
     * Log annotation.
     */
    @Pointcut("@annotation(com.lzx.frame.common.annotation.log.Log)")
    public void log() {

    }

    /**
     * Do before.
     */
    @Before("log()")
    public void doBefore() {
        this.threadLocal.set(new Date(System.currentTimeMillis()));
    }

    /**
     * Do after.
     *
     * @param joinPoint   the join point
     * @param returnValue the return value
     */
    @AfterReturning(pointcut = "log()", returning = "returnValue")
    public void doAfter(JoinPoint joinPoint, Object returnValue) {
        //目标方法实体
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        //获取日志注解
        Log log = AnnotationUtils.findAnnotation(method, Log.class);

        //存在日志注解
        if (StringUtils.checkValNotNull(log)) {
            if (StringUtils.checkValNotNull(log.describe())) {
                EvaluationContext context = getContext(joinPoint.getArgs(), method);
                returnValue = getValue(context, log.describe(), String.class);
                System.out.println(returnValue);
            }
        }

    }

    /**
     * 获取spel 定义的参数值
     *
     * @param context 参数容器
     * @param key     key
     * @param clazz   需要返回的类型
     * @param <T>     返回泛型
     * @return 参数值
     */
    @SuppressWarnings({"all"})
    private <T> T getValue(EvaluationContext context, String key, Class<T> clazz) {
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        Expression expression = spelExpressionParser.parseExpression(key);
        return expression.getValue(context, clazz);
    }


    /**
     * 获取参数容器
     *
     * @param arguments       方法的参数列表
     * @param signatureMethod 被执行的方法体
     * @return 装载参数的容器
     */
    @SuppressWarnings({"all"})
    private EvaluationContext getContext(Object[] arguments, Method signatureMethod) {
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(signatureMethod);
        EvaluationContext context = new StandardEvaluationContext();
        if (ArrayUtils.isNotEmpty(parameterNames)) {
            for (int i = 0; i < ArrayUtils.getLength(arguments); i++) {
                context.setVariable(parameterNames[i], arguments[i]);
            }
        }
        return context;
    }

}
