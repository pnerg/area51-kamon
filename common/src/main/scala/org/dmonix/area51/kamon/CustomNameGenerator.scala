package org.dmonix.area51.kamon

import com.typesafe.scalalogging.LazyLogging
import kamon.instrumentation.http.HttpMessage.Request
import kamon.instrumentation.http.HttpOperationNameGenerator

/**
  * @author Peter Nerg
  */
class CustomNameGenerator extends HttpOperationNameGenerator with LazyLogging {
  logger.info("Creating CustomNameGenerator")
  override def name(request: Request): Option[String] = {
    logger.info("Name generator : "+request.path)
    Some(request.path)
  }
}


/**
  * @author Peter Nerg
  */
class FixedNameGenerator extends HttpOperationNameGenerator {
  override def name(request: Request): Option[String] = Some("APE")
}
