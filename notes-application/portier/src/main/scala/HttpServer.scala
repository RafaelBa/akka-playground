import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import controllers.Health
import server.Inits

import scala.io.StdIn

object HttpServer extends App {
  implicit val system = ActorSystem("portier-akka-werbserver")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val host = "localhost"
  val port = 9000

  Inits.run()

  val bindingFuture = Http().bindAndHandle(server.Routes.getRoutes, host, port)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

  StdIn.readLine() // let it run until user presses return

  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
