package me.rotemfo.linkchecker

import akka.actor.{Actor, ActorLogging, ActorRef, Props, ReceiveTimeout}

import scala.concurrent.duration._

/**
  * project: scala-playground
  * package: me.rotemfo.linkchecker
  * file:    Main
  * created: 2019-03-04
  * author:  rotem
  */
class Main extends Actor with ActorLogging {
  val receptionist: ActorRef = context.actorOf(Props[Receptionist], "receptionist")
  context.setReceiveTimeout(10.seconds)

  receptionist ! Receptionist.Get("http://www.google.com")

  override def receive: Receive = {
    case Receptionist.Result(url, links) =>
      log.info(s"\n'$url' result:\n[${links.toSeq.mkString("],[")}]")
    case Receptionist.Failed(url) =>
      log.error(s"error fetching url '$url'")
    case ReceiveTimeout =>
      context.stop(self)
  }

  override def postStop(): Unit = {
    WebClient.shutdown()
  }
}
