package org.dmonix.area51.kamon

import com.typesafe.scalalogging.StrictLogging
import kamon.instrumentation.http.HttpMessage.Request
import kamon.instrumentation.http.HttpOperationNameGenerator

/**
  * @author Peter Nerg
  */
class ClientNameGenerator extends HttpOperationNameGenerator with StrictLogging {
  logger.info("Creating ClientNameGenerator")
  override def name(request: Request): Option[String] = {
    Some(request.path)
  }
}

/**
  * @author Peter Nerg
  */
class ServerNameGenerator extends HttpOperationNameGenerator with StrictLogging {
  logger.info("Creating ServerNameGenerator")
  override def name(request: Request): Option[String] = {
    Some(request.path)
  }
}

/**
  * @author Peter Nerg
  */
class FixedNameGenerator extends HttpOperationNameGenerator {
  override def name(request: Request): Option[String] = Some("APE")
}
