package me.rotemfo.linkchecker

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

/**
  * project: link-checker
  * package: me.rotemfo.linkchecker
  * file:    GetterSpec
  * created: 2019-03-07
  * author:  rotem
  */
class GetterSpec extends TestKit(ActorSystem("GetterSpec")) with WordSpecLike with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import GetterSpec._
  import MockWebClient._

  "A Getter" must {
    "return the right body" in {
      system.actorOf(StepParent(fakeGetter(firstLink, 2), testActor), "rightBody")
      for (link <- links(firstLink))
        expectMsg(Controller.Check(link, 2))
      expectMsg(Getter.Done)
    }

    "finish properly in case of errors" in {
      system.actorOf(StepParent(fakeGetter("unknown", 2), testActor), "wrongLink")
      expectMsg(Getter.Done)
    }
  }
}

object GetterSpec {
  def fakeGetter(url: String, depth: Int): Props = Props(new Getter(url, depth) {
    override def webClient: WebClient = MockWebClient
  })
}
