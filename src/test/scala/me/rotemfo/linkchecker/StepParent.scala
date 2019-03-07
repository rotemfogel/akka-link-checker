package me.rotemfo.linkchecker

import akka.actor.{Actor, ActorRef, Props}

/**
  * project: link-checker
  * package: me.rotemfo.linkchecker
  * file:    StepParent
  * created: 2019-03-07
  * author:  rotem
  */
class StepParent(child: Props, probe: ActorRef) extends Actor {
  context.actorOf(child, "child")

  override def receive: Receive = {
    case msg => probe.tell(msg, sender)
  }
}
