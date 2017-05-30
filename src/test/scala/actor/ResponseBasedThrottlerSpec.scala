package actor

import akka.testkit.TestActors
import akka.pattern.ask
import akka.util.Timeout
import org.specs2.Specification
import org.specs2.time.NoTimeConversions
import utils.AkkaTestkitSpecs2Support
import scala.concurrent.duration.DurationInt

class ResponseBasedThrottlerSpec extends Specification with NoTimeConversions { def is =
s2"""
  Throttler should
    forward the message unchanged       ${akkaTest.forward}
"""


  private lazy val akkaTest = new AkkaTestkitSpecs2Support {
    val actor = system.actorOf(ResponseBasedThrottler.props(2, 2, system.actorOf(TestActors.echoActorProps)))

    def forward = {
      implicit val timeout = Timeout(1 second)
      val hi = "hi"

      actor ? hi
      expectMsg(hi)
      success
    }
  }
}
