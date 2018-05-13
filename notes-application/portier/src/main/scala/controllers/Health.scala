package controllers

import java.time.{Duration, Instant}

object Health {
  private val startTime = {
    println("start")

    Instant.now
  }

  def get: String = {
    s"""{
      "name": "portier-akka",
      "status": "OK"
      "uptime": ${Duration.between(startTime, Instant.now)}
    }"""
  }

  def init(): Unit = Unit
}
