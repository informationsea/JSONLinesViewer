package info.informationsea.jsonview.values

case class JsonNullValue(path: Option[JsonPath]) extends JsonValue {
  override def stringValue: String = "null"
}
