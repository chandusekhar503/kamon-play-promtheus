package kamon.custom.instrumentation;

import kamon.Kamon;
import kamon.context.Context;
import kamon.context.Key;
import kamon.trace.Span;
import kamon.trace.SpanCustomizer;
import kamon.trace.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Function0;
import scala.runtime.AbstractFunction0;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by chandu on 05/12/18.
 */
@Aspect
public class MethodLevelInstrumentation {

    private static final Logger LOG = LoggerFactory.getLogger(MethodLevelInstrumentation.class);
    public static final String methodRegex = "execution(* controllers..*(..)) || execution(* services..*(..)) || execution(* dao..*(..))";
    @Around(value = methodRegex)
    public Object instrumentCustomMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodSignature = proceedingJoinPoint.getSignature().toString();
        String methodName = proceedingJoinPoint.getSignature().getName();
        LOG.info("Applying trace for method {}", methodSignature);
        Span customSpan = createSpan(methodName, methodSignature);
        Object response = proceed(customSpan, proceedingJoinPoint, methodSignature);
        return handle(response, customSpan, methodSignature);
    }

    private Span createSpan(String methodName, String methodSignature) {
        Span customSpan = null;
        try {
            SpanCustomizer spanCustomizer = new SpanCustomizer() {
                @Override
                public Tracer.SpanBuilder customize(Tracer.SpanBuilder spanBuilder) {
                    return spanBuilder;
                }
            };
            Key spanKey = Key.local("span-customizer", spanCustomizer);
            Tracer.SpanBuilder spanBuilder =
                    Kamon
                            .buildSpan(methodName)
                            .asChildOf(Kamon.currentSpan())
                            .withFrom(Kamon.clock().instant())
                            .withTag("ThreadName",Thread.currentThread().getName())
                            .withTag("MethodSignature", methodSignature);

            customSpan = ((SpanCustomizer) Kamon.currentContext().get(spanKey)).customize(spanBuilder).start();
        } catch (Exception e) {
            LOG.error("Exception while creating span {} for method {}", e.getMessage(), methodSignature);
        }
        return customSpan;
    }

    private Object proceed(Span customSpan, ProceedingJoinPoint proceedingJoinPoint, String methodSignature) throws Throwable {
        Object response = null;
        if (customSpan != null) {
            Context customContext = Kamon.currentContext().withKey(Span.ContextKey(), customSpan);
            Function0 proceedFuction = new AbstractFunction0() {
                @Override
                public Object apply() {
                    Object innerResp = null;
                    try {
                        innerResp = proceedingJoinPoint.proceed();
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                    return innerResp;
                }
            };
            try {
                response = Kamon.withContext(customContext, proceedFuction);
            } catch (Exception e) {
                LOG.error("Error while proceesing method {} is {}", methodSignature, e.getMessage());
                throw e.getCause();
            }

        } else {
            response = proceedingJoinPoint.proceed();
        }
        return response;
    }

    private Object handle(Object response, Span customSpan, String methodSignature) {
        LOG.debug("Handling Response for method {}", methodSignature);
        if (response != null && response instanceof CompletionStage || response instanceof CompletableFuture) {
            ((CompletionStage) response).thenRunAsync(() -> {
                endCustomSpan(customSpan);
            });
        } else {
            endCustomSpan(customSpan);
        }
        return response;
    }


    private void endCustomSpan(Span customSpan) {
        if (customSpan != null) {
            Instant endTimestamp = Kamon.clock().instant();
            customSpan.finish(endTimestamp);
        }
    }
}


