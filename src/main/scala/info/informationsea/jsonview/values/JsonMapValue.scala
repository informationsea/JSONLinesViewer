package info.informationsea.jsonview.values

import scala.collection.mutable

case class JsonMapValue(value: Seq[(String, JsonValue)], path: Option[JsonPath]) extends JsonValue {
  private val keyToIndexTemp: mutable.Map[String, Int] = mutable.Map()

  private var i = 0
  for ((k, _) <- value) {
    keyToIndexTemp(k) = i
    i += 1
  }

  override def stringValue: String = {
    val builder = StringBuilder.newBuilder
    builder ++= "{"
    var isFirst = true
    var tooLong = false
    if (keyToIndexTemp.contains("ID")) {
      isFirst = false
      builder ++= "ID: " + value(keyToIndexTemp("ID"))._2.stringValue
    }

    for ((k, v) <- value) {
      if (tooLong) {
        // do nothing
      } else if (builder.size > 30) {
        builder ++= "..."
        tooLong = true
      } else if (k != "ID") {
        if (isFirst) {
          isFirst = false
        } else {
          builder ++= ", "
        }
        builder ++= k
        builder ++= ": "
        builder ++= v.stringValue
      }
    }

    builder ++= "}"
    builder.mkString
  }
}
