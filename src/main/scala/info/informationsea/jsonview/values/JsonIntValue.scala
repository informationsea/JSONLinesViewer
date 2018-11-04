package info.informationsea.jsonview.values

case class JsonIntValue(value: Int, path: Option[JsonPath]) extends JsonValue {
  override def stringValue: String = Integer.toString(value)
}
