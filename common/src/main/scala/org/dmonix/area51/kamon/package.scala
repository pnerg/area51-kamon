package org.dmonix.area51

import com.typesafe.config.{Config, ConfigFactory}
/**
  * @author Peter Nerg
  */
package object kamon {
  private def configFromResource(resource:String): Config = {
    val src = scala.io.Source.fromInputStream (getClass.getResourceAsStream (resource))
    try {
      ConfigFactory.parseString(src.mkString)
    }
    finally {
      src.close()
    }
  }

  def kamonConfig = ConfigFactory.defaultApplication
    .withFallback(ConfigFactory.defaultOverrides())
    .withFallback(ConfigFactory.defaultReference())
    .resolve()
}
