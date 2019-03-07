package me.rotemfo.linkchecker

import akka.actor.Props

/**
  * project: link-checker
  * package: me.rotemfo.linkchecker
  * file:    FakeGetter
  * created: 2019-03-07
  * author:  rotem
  */
object FakeGetter {
  def apply(url: String, depth: Int): Props = Props(new Getter(url, depth) {
    override def webClient: WebClient = MockWebClient
  })
}
