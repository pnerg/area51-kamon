package org.dmonix.area51.kamon

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import kamon.Kamon

object SimpleKamonClient extends App {

  Kamon.init(kamonConfig)

  private implicit val system = ActorSystem("simple-kamon-client")
  private implicit val materializer = ActorMaterializer()
  private implicit val executionContext = system.dispatcher

  val port = 9696
  (for {
    _ <- Http().singleRequest(HttpRequest(uri = s"http://localhost:$port/api/execute/do-it"))
    //_ <- Http().singleRequest(HttpRequest(uri = s"http://localhost:$port/health"))
    //_ <- Http().singleRequest(HttpRequest(uri = s"http://localhost:$port/api/execute/do-it-again"))
    //_ <- Http().singleRequest(HttpRequest(uri = s"http://localhost:$port/api/customer/1234/books"))
    //_ <- Http().singleRequest(HttpRequest(uri = s"http://localhost:$port/api/customer/4321/pets"))
  } yield {
    ()
  }).onComplete {
  case _ =>
  Thread.sleep(5000)
  println("Done!")
  //system.terminate()
  //materializer.shutdown()
  //System.exit(0)
}
  
}
