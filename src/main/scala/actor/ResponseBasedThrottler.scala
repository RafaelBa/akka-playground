package actor

import actor.ResponseBasedThrottler.{Active, Data, SetBoundries, State}
import akka.actor.{Actor, ActorLogging, ActorRef, FSM, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import scala.collection.immutable.Queue
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

case class Message()

object ResponseBasedThrottler {
  def props(maxCalls: Int, maxQueue: Int, actor: ActorRef) = Props(new ResponseBasedThrottler(maxCalls, maxQueue, actor))

  sealed trait State
  case object Active extends State

  final case class Message(message: Any, sender: ActorRef)

  final case class Data(
    maxCalls: Int,
    maxQueue: Int,
    queue: Seq[Message]
  )

  final case class SetBoundries(maxCalls: Int, maxQueue: Int)
}

class ResponseBasedThrottler(maxCalls: Int, maxQueue: Int, actor: ActorRef) extends Actor with FSM[State, Data] {
  implicit val timeout = Timeout(2 seconds)

  startWith(Active, Data(maxCalls, maxQueue, Seq.empty))

  when(Active) {
    case Event(SetBoundries(newMaxCalls, newMaxQueue), data) ⇒
      stay using data.copy(newMaxCalls, newMaxQueue)

    case Event(msg, data) ⇒
      Thread.sleep(1000)      // FIXME no, we don't want to sleep
      val res = actor ? msg
      pipe(res) to sender
      stay using data


      // needs rework: the sending a message to the actual actor. But this step has to be split up into sending the thing and receving. response.onComplete(x => x.fold(self ! Error)(value => self ! Success(value))
    /*case Event(any, messages) if messages.size < maxCalls ⇒
      val res = (actor ? any)

      pipe(res) to sender

      res map {_ =>
        stay using messages.drop(1)
      }*/
  }

}

// This helps a lot in order to understand what I have to do

/*
object TimerBasedThrottler {
  case object Tick

  // States of the FSM: A `TimerBasedThrottler` is in state `Active` iff the timer is running.
  sealed trait State
  case object Idle extends State
  case object Active extends State

  // Messages, as we queue them to be sent later
  final case class Message(message: Any, sender: ActorRef)

  // The data of the FSM
  final case class Data(
    target:                Option[ActorRef],
    callsLeftInThisPeriod: Int,
    queue:                 Queue[Message])
}

class TimerBasedThrottler extends Actor with FSM[State, Data] {
  import TimerBasedThrottler._
  import FSM.`→`

  startWith(Idle, Data(None, rate.numberOfCalls, Queue()))

  // Idle: no messages, or target not set
  when(Idle) {
    // Queuing
    case Event(msg, d @ Data(None, _, queue)) ⇒
      stay using d.copy(queue = queue.enqueue(Message(msg, context.sender())))
    case Event(msg, d @ Data(Some(_), _, Seq())) ⇒
      goto(TActive) using deliverMessages(d.copy(queue = Queue(Message(msg, context.sender()))))
    // Note: The case Event(msg, t @ Data(Some(_), _, _, Seq(_*))) should never happen here.
  }

  when(TActive) {
    // Tick after a `SetTarget(None)`: take the additional permits and go to `Idle`
    case Event(Tick, d @ Data(None, _, _)) ⇒
      goto(Idle) using d.copy(callsLeftInThisPeriod = rate.numberOfCalls)

    // Period ends and we have no more messages: take the additional permits and go to `Idle`
    case Event(Tick, d @ Data(_, _, Seq())) ⇒
      goto(Idle) using d.copy(callsLeftInThisPeriod = rate.numberOfCalls)

    // Period ends and we get more occasions to send messages
    case Event(Tick, d @ Data(_, _, _)) ⇒
      stay using deliverMessages(d.copy(callsLeftInThisPeriod = rate.numberOfCalls))

    // Queue a message (when we cannot send messages in the current period anymore)
    case Event(msg, d @ Data(_, 0, queue)) ⇒
      stay using d.copy(queue = queue.enqueue(Message(msg, context.sender())))

    // Queue a message (when we can send some more messages in the current period)
    case Event(msg, d @ Data(_, _, queue)) ⇒
      stay using deliverMessages(d.copy(queue = queue.enqueue(Message(msg, context.sender()))))
  }

  onTransition {
    case Idle → Active ⇒ startTimer(rate)
    case Active → Idle ⇒ stopTimer()
  }

  initialize()

  /**
    * Send as many messages as we can (while respecting the rate) to the target and
    * return the state data (with the queue containing the remaining ones).
    */
  private def deliverMessages(data: Data): Data = {
    val queue = data.queue
    val nrOfMsgToSend = scala.math.min(queue.length, data.callsLeftInThisPeriod)

    queue.take(nrOfMsgToSend).foreach(x ⇒ data.target.get.tell(x.message, x.sender))

    data.copy(queue = queue.drop(nrOfMsgToSend), callsLeftInThisPeriod = data.callsLeftInThisPeriod - nrOfMsgToSend)
  }
}
*/