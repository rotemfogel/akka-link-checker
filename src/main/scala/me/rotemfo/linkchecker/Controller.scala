package me.rotemfo.linkchecker

import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, ReceiveTimeout, SupervisorStrategy, Terminated}
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

import me.rotemfo.linkchecker.Getter.getterProps

class Controller extends Actor with ActorLogging {

  context.setReceiveTimeout(10.seconds)

  var cache = Set.empty[String]

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 5) {
    case _: Exception => SupervisorStrategy.restart
  }

  override def receive: Receive = LoggingReceive {
    case Controller.Check(url, depth) =>
      log.debug("Controller::receive ==> [{},{}]", url, depth)
      if (!cache(url) && depth > 0)
        context.watch(context.actorOf(getterProps(url, depth - 1)))
      cache += url
    case Terminated(_) =>
      if (context.children.isEmpty) {
        log.debug("Controller::receive ==> {}", cache.mkString(","))
        context.parent ! Controller.Result(cache)
      }
    case ReceiveTimeout => context.children.foreach(context.stop)
  }
}
