package me.rotemfo.linkchecker

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.LoggingReceive
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
/**
  * project: link-checker
  * package: me.rotemfo.linkchecker
  * file:    ReceptionistSpec
  * created: 2019-03-07
  * author:  rotem
  */
class ReceptionistSpec extends TestKit(ActorSystem("ReceptionistSpec")) with WordSpecLike with BeforeAndAfterAll {
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import ReceptionistSpec._

  private final val url = "http://www.google.com/"

  "A Receptionist" must {

    "reply with a result" in {
      val receptionist = system.actorOf(StepParent(fakeReceptionist, testActor), "reply")
      receptionist ! Receptionist.Get(url)
      expectMsg(Receptionist.Result(url, Set(url)))
    }

    "reject request flood" in {
      val receptionist = system.actorOf(StepParent(fakeReceptionist, testActor), "reject")
      for (i <- 1 to 5) receptionist ! Receptionist.Get(s"$url$i")
      for (i <- 1 to 4) expectMsg(Receptionist.Result(s"$url$i", Set(s"$url$i")))
      expectMsg(Receptionist.Failed(s"${url}4"))
    }
  }
}


object ReceptionistSpec {

  private class FakeController extends Actor with ActorLogging {
    import context.dispatcher
    override def receive: Receive = LoggingReceive {
      case Controller.Check(url, depth) =>
        log.debug("FakeController::receive ==> [{},{}]", url, depth)
        context.system.scheduler.scheduleOnce(500.millis, sender, Controller.Result(Set(url)))
    }
  }

  def fakeReceptionist: Props = {
    Props(new Receptionist {
      // override controller with fake one
      override protected def controllerProps: Props = Props[FakeController]
    })
  }
}