package me.rotemfo.linkchecker

import akka.actor.{Actor, ActorLogging, Status}
import akka.event.LoggingReceive
import akka.pattern.pipe
import org.jsoup.Jsoup

import scala.concurrent.ExecutionContextExecutor
/**
  * project: scala-playground
  * package: me.rotemfo.linkchecker
  * file:    Getter
  * created: 2019-03-03
  * author:  rotem
  */
object Getter {
  case object Abort
  case object Done
}

class Getter(url: String, depth: Int) extends Actor with ActorLogging {

  implicit val executor: ExecutionContextExecutor = context.dispatcher

  WebClient.get(url).pipeTo(self)

  import scala.collection.JavaConverters._

  private def findLinks(body: String): Iterator[String] = {
    val document = Jsoup.parse(body)
    val links = document.select("a[href]")
    for {
      link <- links.iterator.asScala
    } yield link.absUrl("href")
  }

  private def stop(): Unit = {
    context.parent ! Getter.Done
    context.stop(self)
  }

  override def receive: Receive = LoggingReceive {
    case body: String =>
      for (link <- findLinks(body))
        context.parent ! Controller.Check(link, depth)
      stop()
    case Getter.Abort => stop()
    case _: Status.Failure => stop()
  }
}
