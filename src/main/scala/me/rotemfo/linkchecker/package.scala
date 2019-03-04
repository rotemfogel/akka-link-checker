package me.rotemfo

/**
  * project: scala-playground
  * package: me.rotemfo.linkchecker
  * file:    package
  * created: 2019-03-03
  * author:  rotem
  */
package object linkchecker {

  final case class BadStatus(private val statusCode: Int = 500,
                             private val cause: Throwable = None.orNull)
    extends Exception(s"an http error $statusCode has occurred", cause)


}
