package server

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, get, path}
import controllers.Health

object Routes extends SprayJsonSupport {
  import objects.HealthStatus._

  def getRoutes = {
      path("health") {
        get {
          complete(Health.get)
        }
      }
  }
}
