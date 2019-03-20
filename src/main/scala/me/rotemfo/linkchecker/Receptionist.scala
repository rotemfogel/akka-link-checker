package me.rotemfo.linkchecker

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive

/**
  * project: scala-playground
  * package: me.rotemfo.linkchecker
  * file:    Receptionist
  * created: 2019-03-03
  * author:  rotem
  */
object Receptionist {
  case class Get(url: String)
  case class Result(url: String, links: Set[String])
  case class Failed(url: String)
}

class Receptionist extends Actor with ActorLogging {

  private val reqNo = new AtomicInteger(0)
  protected val queueSize: Int = 3

  private def waiting: Receive = LoggingReceive {
    case Receptionist.Get(url) =>
      context.become(runNext(Vector(Job(sender, url))))
  }

  private def enqueueJob(queue: Vector[Job], job: Job): Receive = LoggingReceive {
    if (queue.size > queueSize) {
      sender ! Receptionist.Failed(job.url)
      running(queue)
    } else running(queue :+ job)
  }

  private def running(queue: Vector[Job]): Receive = LoggingReceive {
    case Controller.Result(links) =>
      val job = queue.head
      job.client ! Receptionist.Result(job.url, links)
      context.stop(sender)
      reqNo.decrementAndGet()
      context.become(runNext(queue.tail))
    case Receptionist.Get(url) =>
      log.debug("Receptionist::running ==> {}", url)
      context.become(enqueueJob(queue, Job(sender, url)))
  }

  private def runNext(queue: Vector[Job]): Receive = LoggingReceive {
    if (queue.isEmpty) waiting
    else {
      log.debug("Receptionist::runNext ==> {}", queue.head.url)
      reqNo.incrementAndGet()
      val controller = context.actorOf(controllerProps, s"c${reqNo.get}")
      controller ! Controller.Check(queue.head.url, 2)
      running(queue)
    }
  }

  protected def controllerProps: Props = Props[Controller]
  override def receive: Receive = waiting

}

