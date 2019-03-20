package me.rotemfo.linkchecker

import java.util.concurrent.Executor

import scala.concurrent.Future

/**
  * project: link-checker
  * package: me.rotemfo.linkchecker
  * file:    MockWebClient
  * created: 2019-03-07
  * author:  rotem
  */
object MockWebClient extends WebClient {
  private final val url = "http://rkuhn.info"
  final val firstLink = s"$url/1"
  final val secondLink = s"$url/2"

  private val bodies = Map(
    firstLink ->
      s"""
         |<html>
         |   <head><title>Page 1</title></head>
         |   <body>
         |     <h1>A Link</a>
         |     <a href="$secondLink"
         |   </body>
         |</html>
    """.stripMargin,
    secondLink ->
      s"""
         |<html>
         |   <head><title>Page 1</title></head>
         |</html>
    """
  )

  final val links = Map(firstLink -> Seq(secondLink))

  override def get(url: String)(implicit executor: Executor): Future[String] = {
    bodies.get(url) match {
      case None => Future.failed(BadStatus(404))
      case Some(body) => Future.successful(body)
    }
  }

  override def shutdown(): Unit = {}
}
