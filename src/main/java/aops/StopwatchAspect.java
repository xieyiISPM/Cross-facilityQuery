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
    private Stopwatch stopwatch = Stopwatch.createUnstarted();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable{
        logger.info("=================================================================================");

        logger.info(joinPoint.getSignature() + " starting.....");
        stopwatch = stopwatch.start();
        Object proceed = joinPoint.proceed();
        long mills = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        stopwatch.reset();

        logger.info("=================================================================================");
        logger.info(joinPoint.getTarget() + " executed in " + mills + " ms");
        logger.info("=================================================================================");
        return proceed;
    }

}
