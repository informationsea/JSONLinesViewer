package info.informationsea.jsonview.values

case class JsonArrayValue(value: IndexedSeq[JsonValue], path: Option[JsonPath]) extends JsonValue {
  override def stringValue: String = {
    val buffer = StringBuilder.newBuilder
    buffer ++= "["
    var isFirst = true
    var i = 0

    while (buffer.size < 30 && i < value.size) {
      if (isFirst) {
        isFirst = false
      } else {
        buffer ++= ", "
      }
      buffer ++= value(i).stringValue
      i += 1
    }

    if (i != value.size) {
      buffer ++= ", ..."
    }
    buffer ++= "]"

    buffer.mkString
  }
}
