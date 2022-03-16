package org.dmonix.area51.kamon

import kamon.Kamon

object TestApp extends App {
  Kamon.init()
  println("TestApp")
}
