package server

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Directives._
import controllers.{HealthController, NotesController}
import objects.notes.NoteRequest

object Routes extends SprayJsonSupport {
  import objects.HealthStatus._

  def getRoutes = {
      path("health") {
        get { complete(HealthController.get) }
      } ~
      pathPrefix("notes") {
        put { entity(as[NoteRequest]) { note => NotesController.putNote(note) } }
      }
  }
}
