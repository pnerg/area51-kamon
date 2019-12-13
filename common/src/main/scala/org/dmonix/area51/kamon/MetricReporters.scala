package org.dmonix.area51.kamon

import com.typesafe.config.Config
import kamon.metric.PeriodSnapshot
import kamon.module.{MetricReporter, Module, ModuleFactory}
import kamon.prometheus.PrometheusReporter

class PrintMetricReporterFactory extends ModuleFactory {
  override def create(settings: ModuleFactory.Settings): Module = {
    println("Creating SimpleMetricReporter")
    new PrintMetricReporter()
  }
}

abstract class BaseMetricReporter extends MetricReporter {
  override def stop(): Unit = {}
  override def reconfigure(config: Config): Unit = {}
}

class PrintMetricReporter extends BaseMetricReporter {
  override def reportPeriodSnapshot(snapshot: PeriodSnapshot): Unit = {
    println(s"------ Got metrics")
    //snapshot.histograms.filter(_.name.startsWith("span_processing")).foreach(println)
    snapshot.histograms.map(_.name).foreach(println)
  }
}

class FilteredPrometheusReporterFactory extends ModuleFactory {
  override def create(settings: ModuleFactory.Settings): Module = {
    println("Creating FilteredPrometheusReporter")
    new FilteredPrometheusReporter()
  }
}

class FilteredPrometheusReporter extends PrometheusReporter {
  override def reportPeriodSnapshot(snapshot: PeriodSnapshot): Unit = {
    //println(s"------ Got metrics")
    //snapshot.histograms.filter(_.name.startsWith("span_processing")).foreach(println)
    //snapshot.histograms.map(_.name).foreach(println)
    //snapshot.counters.map(_.name).foreach(println)
    super.reportPeriodSnapshot(snapshot)
  }
  
}
