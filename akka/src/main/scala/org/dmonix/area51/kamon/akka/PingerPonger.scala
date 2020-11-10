package org.dmonix.area51.kamon.akka

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Timers}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import kamon.Kamon

/**
  * Test to see what metrics a plain akka actor system renders
  * Simple Pinger/Ponger.
  * Pinger
  *   periodically sends message Ping to 'Ponger' actor
  *   creates temporary actor 'PongReceiver' to receive the Pong response
  * Ponger 
  *   Immediately responds with Pong message
  * PongReceiver
  *   Receives Pong message, forwards to 'Pinger' and terminates itself
  * 
  * 
  * @author Peter Nerg
  */
object PingerPonger extends App with LazyLogging {

  private val config = ConfigFactory.empty()
    .withFallback(ConfigFactory.defaultOverrides())
    .withFallback(ConfigFactory.defaultApplication())
    .withFallback(ConfigFactory.defaultReference())
    .resolve()

  Kamon.init(config)
  
  private implicit val system = ActorSystem("area51-kamon-akka", config)
  val ponger = system.actorOf(Props(new Ponger()), "Ponger")
  val pinger = system.actorOf(Props(new Pinger(ponger)), "Pinger")
  
  logger.info("Started Pinger/Ponger")
}

private object Messages {
  object Ping
  object Pong
}

private class Pinger(ponger:ActorRef) extends Actor with Timers with LazyLogging {
  import scala.concurrent.duration.DurationInt
  import Messages._
  private object Timer
  private object TimerKey
  override def preStart(): Unit = schedulePing()
  override def receive: Receive = {
    case Timer => 
      val receiver = context.actorOf(Props(new PongReceiver(self)))
      ponger.tell(Ping, receiver)
    case Pong => 
      logger.info("Got Pong")
      schedulePing()
  }
  
  private def schedulePing():Unit = timers.startSingleTimer(TimerKey, Timer, 5.seconds)
}

private class PongReceiver(pinger:ActorRef) extends Actor {
  import Messages._
  override def receive: Receive = {
    case Pong =>
      pinger ! Pong
      context.stop(self)
  }
}

private class Ponger extends Actor {
  import Messages._
  override def receive: Receive = {
    case Ping => 
      sender ! Pong
  } 
}
