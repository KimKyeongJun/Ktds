package com.ktds.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaoParamAop {

	private Logger logger = LoggerFactory.getLogger(DaoParamAop.class);
	
	//around
	public Object getParam(ProceedingJoinPoint pjp) {
		
		String classAndMethod = pjp.getSignature().toShortString();
		logger.debug(classAndMethod);		//현재 실행되는 메소드
		
		Object[] paramArray = pjp.getArgs();
		for( Object param : paramArray) {
			logger.debug(classAndMethod+" = "+param.toString());
		}
		
		
		//before
		logger.debug("before");
		// 기존의 메소드 실행
		Object result = null;
		try {
			//after-returning
			result = pjp.proceed();
			logger.debug(classAndMethod+" = "+"Result: "+result.toString());
		} catch (Throwable e) {
			logger.debug("after-throwing");
			// after-Throwing
			logger.debug(classAndMethod+" = "+"Exception: "+e.getCause().toString() + ", "+e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
		
		return result;
		
	}

}
