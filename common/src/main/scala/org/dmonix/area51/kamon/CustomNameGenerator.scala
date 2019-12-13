package org.dmonix.area51.kamon

import kamon.instrumentation.http.HttpMessage.Request
import kamon.instrumentation.http.HttpOperationNameGenerator

/**
  * @author Peter Nerg
  */
class CustomNameGenerator extends HttpOperationNameGenerator {
  override def name(request: Request): Option[String] = {
    println("Name generator : "+request.path)
    Some(request.path)
  }
}


/**
  * @author Peter Nerg
  */
class FixedNameGenerator extends HttpOperationNameGenerator {
  override def name(request: Request): Option[String] = Some("APE")
}
