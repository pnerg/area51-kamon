package org.dmonix.area51.kamon

import com.typesafe.scalalogging.LazyLogging
import kamon.instrumentation.http.HttpMessage.Request
import kamon.instrumentation.http.HttpOperationNameGenerator

/**
  * @author Peter Nerg
  */
class ClientNameGenerator extends HttpOperationNameGenerator with LazyLogging {
  logger.info("Creating ClientNameGenerator")
  override def name(request: Request): Option[String] = {
    logger.info(request.path)
    Some(request.path)
  }
}

/**
  * @author Peter Nerg
  */
class ServerNameGenerator extends HttpOperationNameGenerator with LazyLogging {
  logger.info("Creating ServerNameGenerator")
  override def name(request: Request): Option[String] = {
    logger.info(request.path)
    Some(request.path)
  }
}

/**
  * @author Peter Nerg
  */
class FixedNameGenerator extends HttpOperationNameGenerator {
  override def name(request: Request): Option[String] = Some("APE")
}
