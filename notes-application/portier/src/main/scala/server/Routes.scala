package server

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path}
import controllers.Health

object Routes {
  def getRoutes = {
      path("health") {
        get {
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, Health.get))
        }
      }
  }
}
