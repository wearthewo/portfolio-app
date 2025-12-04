package com.enterprise.portfolio.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(com.enterprise.portfolio.controller..*)" +
            " || within(com.enterprise.portfolio.service..*)" +
            " || within(com.enterprise.portfolio.repository..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Advice that logs when a method is entered and exited, including execution time.
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Enter: {}.{}() with argument[s] = {}", 
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    getParameterList(joinPoint.getArgs()));
        }
        
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            
            Object result = joinPoint.proceed();
            
            stopWatch.stop();
            
            if (log.isDebugEnabled()) {
                log.debug("Exit: {}.{}() with result = {}. Execution time = {} ms", 
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        result != null ? result.toString() : "null",
                        stopWatch.getTotalTimeMillis());
            }
            
            // Log slow method execution
            if (stopWatch.getTotalTimeMillis() > 1000) {
                log.warn("Method execution time exceeded 1000ms: {}.{}() took {} ms", 
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        stopWatch.getTotalTimeMillis());
            }
            
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", 
                    getParameterList(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
            throw e;
        } catch (Exception e) {
            log.error("Error in {}.{}() with cause = {}", 
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    e.getCause() != null ? e.getCause() : "NULL");
            throw e;
        }
    }

    /**
     * Helper method to get parameter list as string
     */
    private String getParameterList(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Object arg = args[i];
            if (arg != null) {
                // For sensitive data like passwords, we should not log the actual value
                if (arg.toString().toLowerCase().contains("password") || 
                    arg.toString().toLowerCase().contains("secret") ||
                    arg.toString().toLowerCase().contains("token") ||
                    arg.toString().toLowerCase().contains("key")) {
                    sb.append("*****");
                } else {
                    sb.append(arg);
                }
            } else {
                sb.append("null");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
