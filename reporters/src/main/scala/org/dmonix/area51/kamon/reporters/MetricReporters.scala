package org.dmonix.area51.kamon.reporters

import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import kamon.metric.PeriodSnapshot
import kamon.module.{MetricReporter, Module, ModuleFactory}

class PrintMetricReporterFactory extends ModuleFactory with StrictLogging {
  override def create(settings: ModuleFactory.Settings): Module = {
    logger.info("Creating PrintMetricReporter")
    new PrintMetricReporter()
  }
}

abstract class BaseMetricReporter extends MetricReporter {
  override def stop(): Unit = {}
  override def reconfigure(config: Config): Unit = {}
}

class PrintMetricReporter extends BaseMetricReporter with StrictLogging {
  override def reportPeriodSnapshot(snapshot: PeriodSnapshot): Unit = {
    //snapshot.histograms.filter(_.name.startsWith("span_processing")).foreach(println)
    snapshot.rangeSamplers.filter(_.name.startsWith("http.server")).foreach{range =>
      println("-------------")
      println(range.name)
      range.instruments.map(_.value)foreach{distr =>
        println("min: "+distr.min)
        println("max: "+distr.max)
        println("sum: "+distr.sum)
        println("count: "+distr.count)
        println("count: "+distr.buckets.size)
      }

    }
  }
}

