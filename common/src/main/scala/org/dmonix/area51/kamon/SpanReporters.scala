package org.dmonix.area51.kamon

import com.typesafe.config.Config
import kamon.module.{Module, ModuleFactory, SpanReporter}
import kamon.trace.Span

class PrintSpanReporterFactory extends ModuleFactory {
  override def create(settings: ModuleFactory.Settings): Module = {
    println("Creating SimpleSpanReporter")
    new PrintSpanReporter()
  }
}

abstract class BaseSpanReporter extends SpanReporter {
  override def stop(): Unit = {}
  override def reconfigure(newConfig: Config): Unit = {}
}

class PrintSpanReporter extends BaseSpanReporter {

  override def reportSpans(spans: Seq[Span.Finished]): Unit = {
    spans.foreach(println)
  }
}

