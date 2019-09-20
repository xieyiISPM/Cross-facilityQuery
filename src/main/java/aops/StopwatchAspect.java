package aops;

import com.google.common.base.Stopwatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class StopwatchAspect {
    private Stopwatch stopwatch;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable{
        stopwatch = Stopwatch.createStarted();
        Object proceed = joinPoint.proceed();
        stopwatch.stop();
        long mills = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("===================================================");
        logger.info(joinPoint.getSignature() + " executed in " + mills + " ms");
        System.out.println("====================================================");
        return proceed;
    }

}
