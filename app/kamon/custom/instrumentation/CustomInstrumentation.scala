package kamon.custom.instrumentation

import java.util.concurrent.{CompletableFuture, CompletionStage}

import kamon.Kamon
import kamon.Kamon.buildSpan
import kamon.trace.{Span, SpanCustomizer}
import kamon.util.CallingThreadExecutionContext
import org.aspectj.lang.{JoinPoint, ProceedingJoinPoint, Signature}
import org.aspectj.lang.annotation.{Around, Aspect}
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.{Logger, LoggerFactory}

import scala.compat.java8.FutureConverters
import scala.concurrent.{Future, Promise}


/**
  * Created by chandu on 25/11/18.
  */
@Aspect
class CustomInstrumentation {

  private val LOG = LoggerFactory.getLogger(classOf[CustomInstrumentation]);

  @Around("execution(* controllers..*(..)) || execution(* services..*(..))")
  def aroundAspect(pjp: ProceedingJoinPoint): Any = {
    val methodSig = pjp.getSignature.toString
    LOG.info("Inside Method {}", methodSig);

    val methodReturnType = getMethodReturnType(pjp)
    LOG.debug("Method Return Type: {}", methodReturnType);

    val customSpan = Kamon.currentContext.get(SpanCustomizer.ContextKey).customize {
      val builder = buildSpan(pjp.getSignature.getName)
        .asChildOf(Kamon.currentSpan)
        .withFrom(Kamon.clock.instant)
        .withTag("method signature", methodSig)
      builder
    }.start

    if ("class java.util.concurrent.CompletableFuture".equals(methodReturnType) || "interface java.util.concurrent.CompletionStage".equals(methodReturnType)) {
      traceFututres(pjp, methodSig, customSpan)
    } else {
      trace(pjp, methodSig, customSpan)
    }
  }

  def getMethodReturnType(pjp: ProceedingJoinPoint): String = {
    val sig: Signature = pjp.getSignature;
    val methodSignature: MethodSignature = sig.asInstanceOf[MethodSignature]
    methodSignature.getReturnType.toString
  }

  def trace(pjp: ProceedingJoinPoint, methodSig: String, customSpan: Span): Any = {
    val response = Kamon.withContext(Kamon.currentContext().withKey(Span.ContextKey, customSpan)) {
      pjp.proceed.asInstanceOf[Any]
    }
    endSpan(customSpan)
    response
  }

  def traceFututres(pjp: ProceedingJoinPoint, methodSig: String, customSpan: Span): Any = {
    val rawStage = Kamon.withContext(Kamon.currentContext().withKey(Span.ContextKey, customSpan)) {
      pjp.proceed.asInstanceOf[Any]
    }

    LOG.info("rawStage: {}", rawStage.getClass)
    //val response : DefaultPromise[Any] = rawStage.asInstanceOf[DefaultPromise[Any]]
    //LOG.info("rawStage: {}",response.getClass)


    val responseFuture: CompletionStage[Any] = rawStage.asInstanceOf[CompletableFuture[Any]]
    //var stage1: CompletionStage[Any] = FutureConverters.toJava(responseFuture)
    val stage: Future[Any] = FutureConverters.toScala(responseFuture)

    stage.transform(s = response1 => {
      endSpan(customSpan)
      response1
    },
      f = error => {
        customSpan.addError("error.object", error)
        endSpan(customSpan)
        error
      }
    )(CallingThreadExecutionContext)
  }

  def endSpan(customSpan: Span): Span = {
    val endTimestamp = Kamon.clock.instant
    customSpan.finish(endTimestamp)
    customSpan
  }
}
