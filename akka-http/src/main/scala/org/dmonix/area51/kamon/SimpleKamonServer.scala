package org.dmonix.area51.kamon

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest}
import akka.http.scaladsl.server.Directives.{complete, get, pathPrefix, _}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import kamon.Kamon


/**
  * A very simple akka-http based server
  * @author Peter Nerg
  */
object SimpleKamonServer extends App with LazyLogging {

  Kamon.init(kamonConfig)

  private implicit val system = ActorSystem("simple-kamon-server")
  private implicit val materializer = ActorMaterializer()
  private implicit val executionContext = system.dispatcher

  private def traceID = Kamon.currentSpan().trace.id.string
  
  private lazy val route: Route =
    pathPrefix("api" / "execute" / Remaining) { order =>
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Hello World!"))
      }
     }
  
  Http()
    .bindAndHandle(route, "0.0.0.0", 0)
    .map(_.localAddress)
    .flatMap{addr =>
      val port = addr.getPort
      logger.info(s"Started on port $port")
      Http().singleRequest(HttpRequest(uri = s"http://localhost:$port/api/execute/do-it"))
    }.foreach{ rsp => 
      logger.info(rsp.status.toString())
  }
}
