package me.rotemfo.linkchecker

import akka.actor.{Actor, ActorLogging, ActorRef, Props, ReceiveTimeout}
import akka.event.LoggingReceive

import scala.concurrent.duration._

/**
  * project: scala-playground
  * package: me.rotemfo.linkchecker
  * file:    Controller
  * created: 2019-03-03
  * author:  rotem
  */
object Controller {
  case class Check(url: String, depth: Int)
  case class Result(links: Set[String])
  case object Done
  case object Failed
}

class Controller extends Actor with ActorLogging {

  context.setReceiveTimeout(10.seconds)

  var cache = Set.empty[String]
  var children = Set.empty[ActorRef]

  override def receive: Receive = LoggingReceive {
    case Controller.Check(url, depth) =>
      log.debug("Controller::receive ==> [{},{}]", url, depth)
      if (!cache(url) && depth > 0)
        children += context.actorOf(Props(new Getter(url, depth - 1)))
      cache += url
    case Getter.Done =>
      children -= sender
      if (children.isEmpty) {
        log.debug("Controller::receive ==> {}", cache.mkString(","))
        context.parent ! Controller.Result(cache)
      }
    case ReceiveTimeout => children.foreach(_ ! Getter.Abort)
  }
}
