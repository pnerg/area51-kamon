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

import scala.concurrent.Future
import scala.util.Random

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

  //dummy counters just for testing
  private val reqCounter = Kamon.counter("execute.requests")
  private val businessCounter = reqCounter.withTag("type", "business")
  private val nonbusinessCounter = reqCounter.withTag("type", "non-business")
  
  private def traceID = Kamon.currentSpan().trace.id.string
  
  private def randomPause:Unit = Thread.sleep(rand.nextInt(300).longValue)
  
  private def simulateAsyncJob(operationName:String, component:String, method:String):Future[Unit] = {
    Future {
      simulateSyncJob(operationName, component, method)
    }       
  }

  private def simulateSyncJob(operationName:String, component:String, method:String):Unit = {
      Kamon.runWithSpan(Kamon.clientSpanBuilder(operationName, component).tagMetrics("method", method).start(), true) {
        //simulates some work
        randomPause
      }
  }

  private lazy val route: Route =
    pathPrefix("api" / "execute" / Remaining) { order =>
      get {
        
        //parallel jobs
        val job1 = simulateAsyncJob("read.user.data", "cassandra", "read")
        val job2 = simulateAsyncJob("read.product.data", "mariadb", "read")
        
        //execute the two parallel jobs and finish with a sync job
        val res = for {
          _ <- job1
          _ <- job2  
        } yield {
          simulateSyncJob("create.product.purchase.order", "kafka", "write")
          businessCounter.increment()
          s"""
               |{
               | "x-trace-token":"${ContextUtil.xTraceTokenValueOrUndefined}",
               | "trace-id":"${traceID}"
               |}
           """.stripMargin
        }
        
        onSuccess(res){ response =>
          complete(HttpEntity(ContentTypes.`application/json`, response))
        }
        
      }
     } ~
   pathPrefix("health") {
     get {
       nonbusinessCounter.increment()
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
        businessCounter.increment()
        r
      }
      onSuccess(f) { response =>
        complete(HttpEntity(ContentTypes.`application/json`, response))
      }
    }
  }


  Http()
    .bindAndHandle(route, "0.0.0.0", 9696)
    .map(_.localAddress)
    .flatMap{addr =>
      val port = addr.getPort
      logger.info(s"Started on port $port")
      Http().singleRequest(HttpRequest(uri = s"http://localhost:$port/api/execute/do-it"))
    }.foreach{ rsp =>
    logger.info(rsp.status.toString())
  }
}
