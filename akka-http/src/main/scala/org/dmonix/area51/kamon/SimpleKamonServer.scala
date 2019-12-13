package org.dmonix.area51.kamon

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest}
import akka.http.scaladsl.server.Directives.{complete, get, pathPrefix, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import kamon.Kamon

import scala.util.{Failure, Success}

object SimpleKamonServer extends App {

  Kamon.init(kamonConfig)

  private implicit val system = ActorSystem("simple-kamon-server")
  private implicit val materializer = ActorMaterializer()
  private implicit val executionContext = system.dispatcher

  private def traceID = Kamon.currentSpan().trace.id.string
  
  private lazy val route: Route =
    pathPrefix("api" / "execute" / Remaining) { order =>
      get {
        println("api/execute -> "+ContextUtil.xTraceTokenValue +" : "+ traceID)
        Kamon.clientSpanBuilder("read.user.data", "database").start().finish()
        Kamon.clientSpanBuilder("update.cache", "in-memory").start().finish()
        
        val response =
          s"""
             |{
             | "x-trace-token":"${ContextUtil.xTraceTokenValueOrUndefined}",
             | "trace-id":"${traceID}"
             |}
           """.stripMargin
        
        complete(HttpEntity(ContentTypes.`application/json`, response))
      }
     } ~
    pathPrefix("api" / "customer" / Segment / Remaining) { (id, order) =>
        get {
          Kamon.clientSpanBuilder("read.user.data", "database").start().finish()
          Kamon.clientSpanBuilder("update.cache", "in-memory").start().finish()
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>HELLO WORLD!</h1>"))
        }
    } ~ 
   pathPrefix("health") {
     get {
       println("health")
       complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "OK!"))
     }
   } ~ 
  pathPrefix("forward") {
    get {
      println("forward -> "+ContextUtil.xTraceTokenValue +" : "+ traceID)
      val f = for {
        response <- Http().singleRequest(HttpRequest(uri = "http://localhost:9696/api/execute/"+System.currentTimeMillis()))
        r <- Unmarshal(response.entity).to[String]
      } yield {
        complete(HttpEntity(ContentTypes.`application/json`, r))
      }
      onComplete(f) {
        case Success(x) => x
        case Failure(ex) =>
          ex.printStackTrace()
          ???
      }

    }
  }
  
  
  Http()
    .bindAndHandle(route, "0.0.0.0", 9696)
    .map(_.localAddress)
    .onComplete({ case Success(addr) => 
      val port = addr.getPort
      println("Started on port " + port)
    })
}
