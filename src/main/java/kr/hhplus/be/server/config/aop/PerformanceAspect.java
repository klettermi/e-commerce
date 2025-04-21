package kr.hhplus.be.server.config.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    @Around("execution(public * kr.hhplus.be.server.application..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            return pjp.proceed();
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            String signature = pjp.getSignature().toShortString();
            log.info("▶ 실행시간 {}: {} ms", signature, elapsed);
        }
    }
}
