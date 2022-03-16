package org.dmonix.area51.kamon

import com.typesafe.scalalogging.LazyLogging
import kamon.trace.Tracer.{PreFinishHook, PreStartHook}
import kamon.trace.{Span, SpanBuilder}

class CustomPreStartHook extends PreStartHook with PathFilter with LazyLogging {
  
  override def beforeStart(builder: SpanBuilder): Unit = {
    // xxxx
  }
}

class CustomPreFinishHook extends PreFinishHook {
  override def beforeFinish(span: Span): Unit = {
    //add the x-trace-token if we have it to the reported span
    ContextUtil.xTraceTokenValue.map(span.tag("x-trace-token", _))
  }
}