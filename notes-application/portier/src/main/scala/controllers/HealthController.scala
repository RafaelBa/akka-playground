package controllers

import java.time.{Duration, Instant}

import objects.HealthStatus

object Health {
  private val startTime = Instant.now

  def get: HealthStatus = {
    HealthStatus(
      name = "portier-akka",
      status = "OK",
      uptime = Duration.between(startTime, Instant.now).toString
    )
  }

  def init(): Unit = Unit
}
