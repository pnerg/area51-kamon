package org.dmonix.area51.kamon

import akka.http.scaladsl.model.Uri
import com.typesafe.scalalogging.LazyLogging
import kamon.tag.Lookups
import kamon.trace.{Span, SpanBuilder}
import kamon.trace.Tracer.{PreFinishHook, PreStartHook}

class CustomPreStartHook extends PreStartHook with PathFilter with LazyLogging {
  
  override def beforeStart(builder: SpanBuilder): Unit = {
    val path = builder.tags().get(Lookups.option("http.url")).map(Uri(_).path.toString).getOrElse("")
    

    Some(builder)
      .map(b => if(!accept(path)) b.doNotTrackMetrics() else b)
//      .map(b => ContextUtil.xTraceTokenValue.map(b.tag("x-trace-token", _)).getOrElse(b))
//      .map(b => ContextUtil.xTraceTokenValue.map(b.tag("x.trace.token", _)).getOrElse(b))
//      .map(b => ContextUtil.xTraceTokenValue.map(b.tag("xtracetoken", _)).getOrElse(b))
      .get
      
    
    //ContextUtil.xTraceTokenValue
    //if(!accept(path))
    //  builder.doNotTrackMetrics()
  }
}

class CustomPreFinishHook extends PreFinishHook {
  override def beforeFinish(span: Span): Unit = {
    //add the x-trace-token if we have it to the reported span
    ContextUtil.xTraceTokenValue.map(span.tag("x-trace-token", _))
  }
}