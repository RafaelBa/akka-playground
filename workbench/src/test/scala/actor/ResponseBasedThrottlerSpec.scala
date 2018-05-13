package actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActors, TestKit, TestProbe}
import akka.util.Timeout
import org.specs2.SpecificationLike
import org.specs2.specification.AfterAll
import org.specs2.time.NoTimeConversions
import scala.concurrent.duration.DurationInt

class ResponseBasedThrottlerSpec extends TestKit(ActorSystem("ResponseBasedThrottlerSpec")) with AfterAll with ImplicitSender with SpecificationLike with NoTimeConversions { def is =
s2"""
  Throttler should
    forward the message unchanged             $forward
    slow down message by concurrent calls     $throttle
    respond if maximum of queue is reached    $reachMaxQue
    set concurrent calls and max queue        $setRate
    work the queue in order                   $workQueueInOrder
"""



  val probe = TestProbe()
  val throttler = system.actorOf(ResponseBasedThrottler.props(2, 10, system.actorOf(TestActors.echoActorProps)))

  def forward = {
    implicit val timeout = Timeout(1 second)
    val hi = "hi"

    probe.send(throttler, hi)

    probe.expectMsg(hi)
    success
  }

  def throttle = {
    val hi = "hi"
    val successMessage = "success message!"
    val messages = Seq(
      hi, hi, hi, hi, successMessage
    )

    messages foreach { probe.send(throttler, _) }

    probe.expectMsg(hi)
    probe.expectMsg(hi)

    // FIXME this does not work as expected, needs to be fixed
    probe.within(1 second, 2 seconds){
      probe.expectMsg(hi)
      probe.expectMsg(hi)
    }

//    probe.within(2 second, 3 seconds){
//      probe.expectMsg(hi)
//      probe.expectMsg(hi)
//    }

    probe.within(3 second, 4 seconds){
      probe.expectMsg(successMessage)
    }


    success
  }

  def reachMaxQue = {
    pending
  }

  def setRate = {
    pending
  }

  def workQueueInOrder = {
    pending
  }

  def afterAll = {
    TestKit.shutdownActorSystem(system)
  }
}
