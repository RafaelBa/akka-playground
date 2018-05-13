package objects

import spray.json.DefaultJsonProtocol._

case class HealthStatus(
  name: String,
  status: String,
  uptime: String
)

object HealthStatus { implicit val format = jsonFormat3(HealthStatus.apply) }
