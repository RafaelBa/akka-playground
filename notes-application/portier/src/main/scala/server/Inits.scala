package server

import controllers.Health

object Inits {
  def run(): Unit = {
    Health.init()
  }
}
