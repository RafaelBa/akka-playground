package server

import controllers.HealthController

object Inits {
  def run(): Unit = {
    HealthController.init()
  }
}
