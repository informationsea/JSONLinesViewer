package info.informationsea.jsonview.values

import java.lang

case class JsonBooleanValue(value: Boolean, path: Option[JsonPath]) extends JsonValue {
  override def stringValue: String = lang.Boolean.toString(value)
}
