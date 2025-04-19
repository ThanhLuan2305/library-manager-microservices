package com.project.libmanage.user_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.project.libmanager.service.impl.*.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info("Starting method execution: {}", joinPoint.getSignature().toShortString());
    }

    @After("execution(* com.project.libmanager.service.impl.*.*(..))")
    public void logAfterMethod(JoinPoint joinPoint) {
        log.info("Method execution completed: {}", joinPoint.getSignature().toShortString());
    }

    @AfterReturning(value = "execution(* com.project.libmanager.service.impl.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Method {} returned: {}", joinPoint.getSignature().toShortString(), result);
    }

    @AfterThrowing(value = "execution(* com.project.libmanager.service.impl.*.*(..))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("Method {} encountered an error: {}", joinPoint.getSignature().toShortString(),
                exception.getMessage());
    }

}
