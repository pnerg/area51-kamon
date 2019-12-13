package org.dmonix.area51.kamon

import kamon.Kamon
import kamon.util.Filter

import scala.util.Try

object PathFilter {
  private val FilterConfigKey = "kamon.filters.path-filter"
  private val filter:Option[Filter] = Try(Kamon.filter(FilterConfigKey)).toOption
}

/**
  * @author Peter Nerg
  */
trait PathFilter {
  import PathFilter._
  def accept(path:String):Boolean = filter.map(_.accept(path)).getOrElse(true)
}
