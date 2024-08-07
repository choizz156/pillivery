package com.team33.moduleapi.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LogAspect {

	private static final String TARGET = "TARGET";
	private static final Logger log = LoggerFactory.getLogger("fileLog");

	@Pointcut("execution(* com.team33.moduleapi.ui..*Controller.*(..))")
	public void controllerPointcut() {
	}

	@Pointcut("execution(* com.team33.moduleapi.exception.controller.ExceptionController.*(..))")
	public void exceptionControllerPointcut() {
	}

	@Before("controllerPointcut()")
	public void before(JoinPoint joinPoint) {
		MDC.put(TARGET, joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
	}

	@AfterReturning("exceptionControllerPointcut()")
	public void exceptionLog(JoinPoint joinPoint) {
		log.error("exception :: {}", joinPoint.getSignature());
	}
}
