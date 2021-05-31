package org.dmonix.area51.kamon

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives.{complete, extractRequest, get, onComplete, pathPrefix, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.config.ConfigFactory
import kamon.Kamon

object DefaultDebugKamonServer extends App {

  Kamon.init(ConfigFactory.defaultReference())

  private implicit val system = ActorSystem("debug-kamon-server")
  private implicit val executionContext = system.dispatcher

  def debugPrint(path: String, request: HttpRequest): Unit = {
    val span = Kamon.currentSpan()
    println("--------------")
    println(s"Got request for '$path' parsed/generated context traceId[${span.trace.id.string}], spanId[${span.id.string}], parentSpanId[${span.parentId.string}]")
    request.headers.foreach(println)
  }

  private lazy val route: Route =
    pathPrefix("forward") {
      get {
        extractRequest { request =>
          debugPrint("/forward", request)
          onComplete(Http().singleRequest(HttpRequest(uri = "http://localhost:9696/hello")).flatMap(x => Unmarshal(x.entity).to[String])) { response =>
            complete(response)
          }
        }
      }
    } ~ pathPrefix("hello") {
      get {
        extractRequest { request =>
          debugPrint("/hello", request)
          complete("World")
        }
      }
    }

  Http().newServerAt("0.0.0.0", 9696).bind(route).foreach(addr => println(s"Started server at [$addr]"))
}
