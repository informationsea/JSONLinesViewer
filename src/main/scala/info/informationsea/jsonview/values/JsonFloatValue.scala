package info.informationsea.jsonview.values

import java.lang

case class JsonFloatValue(value: Float, path: Option[JsonPath]) extends JsonValue {
  override def stringValue: String = lang.Float.toString(value)
}
