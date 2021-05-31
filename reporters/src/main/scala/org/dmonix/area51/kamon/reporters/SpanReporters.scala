package org.dmonix.area51.kamon.reporters

import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import kamon.module.{Module, ModuleFactory, SpanReporter}
import kamon.trace.Span

class PrintSpanReporterFactory extends ModuleFactory with StrictLogging{
  override def create(settings: ModuleFactory.Settings): Module = {
    logger.info("Creating PrintSpanReporter")
    new PrintSpanReporter()
  }
}

abstract class BaseSpanReporter extends SpanReporter {
  override def stop(): Unit = {}
  override def reconfigure(newConfig: Config): Unit = {}
}

class PrintSpanReporter extends BaseSpanReporter with StrictLogging {

  override def reportSpans(spans: Seq[Span.Finished]): Unit = {
    spans.foreach(s => logger.info(s.toString))
  }
}

