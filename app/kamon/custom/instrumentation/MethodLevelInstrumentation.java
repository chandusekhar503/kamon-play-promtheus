package kamon.custom.instrumentation;

import kamon.Kamon;
import kamon.trace.Span;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

/**
 * Created by chandu on 19/11/18.
 */
@Aspect
public class MethodLevelInstrumentation {

    @Around("execution(* controllers.CountController.count(..))")
    public Object onHandle(ProceedingJoinPoint joinPoint) throws Throwable {
        Object res = null;
        try {
            long startTime = System.currentTimeMillis();
            System.out.println("start time: " + startTime+" : Thread Id: "+ Thread.currentThread().getId());
            res = joinPoint.proceed();
            System.out.println("end time: " + System.currentTimeMillis() +" : Thread Id: "+ Thread.currentThread().getId());
            long totalTime = System.currentTimeMillis() - startTime;
            System.out.println("Total time for execution of method " + joinPoint.getSignature().getName() + " totalTime: " + totalTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}