package org.dmonix.area51.kamon

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest}
import akka.http.scaladsl.server.Directives.{complete, get, pathPrefix, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import kamon.Kamon

import scala.util.{Failure, Random, Success}

/**
  * A very simple akka-http based server
  * @author Peter Nerg
  */
object SimpleKamonServer extends App with LazyLogging {

  Kamon.init(kamonConfig)

  private val rand = new Random()

  private implicit val system = ActorSystem("simple-kamon-server")
  private implicit val materializer = ActorMaterializer()
  private implicit val executionContext = system.dispatcher

  
  private def traceID = Kamon.currentSpan().trace.id.string
  
  private def pause:Unit = Thread.sleep(rand.nextInt(300).longValue)
  
  private lazy val route: Route =
    pathPrefix("api" / "execute" / Remaining) { order =>
      get {
        Kamon.runWithSpan(Kamon.clientSpanBuilder("read.user.data", "database").tagMetrics("method", "read").start(), true) {
          //simulates some work
          pause
        }

        Kamon.runWithSpan(Kamon.clientSpanBuilder("update.cache", "in-memory").start(), true) {
          //simulates some work
          pause
        }
        
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
   pathPrefix("health") {
     get {
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
      logger.info(s"Started on port ${addr.getPort}")
    })
}
