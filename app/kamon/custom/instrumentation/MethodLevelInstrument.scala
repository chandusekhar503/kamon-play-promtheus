package kamon.custom.instrumentation

import kamon.Kamon
import kamon.play.Play
import kamon.play.instrumentation.encodeContext
import kamon.trace.{Span, SpanCustomizer}
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.{Around, Aspect}


/**
  * Created by chandu on 19/11/18.
  */
@Aspect
class MethodLevelInstrument {


  @Around("wsClientUrl()")
  def aroundWSClientUrl(pjp: ProceedingJoinPoint): Any ={
    val currentContext = Kamon.currentContext()
    val parentSpan = currentContext.get(Span.ContextKey)

    val request = pjp.getSignature.getDeclaringTypeName +" "+pjp.getSignature.getName
    val clientSpanBuilder = Kamon.buildSpan(request)
      .asChildOf(parentSpan)
      .withMetricTag("span.kind", "client")
      .withMetricTag("component", "play.client.ws")
      .withMetricTag("http.method", pjp.getSignature.getName)
      .withTag("http.url", pjp.getSignature.getName)


    val clientRequestSpan = currentContext.get(SpanCustomizer.ContextKey)
      .customize(clientSpanBuilder)
      .start()


    val responseFuture =  pjp.proceed();




  }
}
