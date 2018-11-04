package info.informationsea.jsonview.values

case class JsonStringValue(value: String, path: Option[JsonPath]) extends JsonValue {
  override def stringValue: String = value
}
