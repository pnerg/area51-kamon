package org.dmonix.kamon.instrumentation

import kamon.module.{Module, ModuleFactory}
import kamon.instrumentation._
import kamon.instrumentation.context._
import kanela.agent.api.instrumentation.InstrumentationBuilder

/**
  * @author Peter Nerg
  */
class Area51Instrumentation extends InstrumentationBuilder {
  print("Created Area51Instrumentation")
}
