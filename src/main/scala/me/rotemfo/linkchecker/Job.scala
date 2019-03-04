package me.rotemfo.linkchecker

import akka.actor.ActorRef

/**
  * project: scala-playground
  * package: me.rotemfo.linkchecker
  * file:    Job
  * created: 2019-03-04
  * author:  rotem
  */
case class Job(client: ActorRef, url: String)

