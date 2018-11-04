package info.informationsea.jsonview.values

import com.fasterxml.jackson.core.{JsonParser, JsonToken}

import scala.collection.mutable.ListBuffer

trait JsonValue {
  def stringValue: String
  def path: Option[JsonPath]
}

object JsonValue {
  def load(jsonParser: JsonParser): JsonValue = {
    val nextToken = jsonParser.nextToken()
    nextLoad(jsonParser, nextToken, None)
  }

  private def nextLoad(jsonParser: JsonParser, token: JsonToken, path: Option[JsonPath]): JsonValue = {
    token match {
      case JsonToken.VALUE_NUMBER_INT => JsonIntValue(jsonParser.getIntValue, path)
      case JsonToken.VALUE_NUMBER_FLOAT => JsonFloatValue(jsonParser.getFloatValue, path)
      case JsonToken.VALUE_TRUE => JsonBooleanValue(true, path)
      case JsonToken.VALUE_FALSE => JsonBooleanValue(false, path)
      case JsonToken.VALUE_STRING => JsonStringValue(jsonParser.getValueAsString, path)
      case JsonToken.VALUE_NULL => JsonNullValue(path)
      case JsonToken.START_ARRAY =>
        val list: ListBuffer[JsonValue] = ListBuffer()
        var nextToken = jsonParser.nextToken()
        var i = 0
        while (nextToken != JsonToken.END_ARRAY) {
          list += nextLoad(jsonParser, nextToken, Some(JsonArrayPath(i)))
          nextToken = jsonParser.nextToken()
          i += 1
        }
        JsonArrayValue(list.toIndexedSeq, path)
      case JsonToken.START_OBJECT =>
        val list: ListBuffer[(String, JsonValue)] = ListBuffer()
        var nextToken = jsonParser.nextToken()
        while (nextToken != JsonToken.END_OBJECT) {
          if (nextToken != JsonToken.FIELD_NAME)
            throw new Exception("Invalid json token " + nextToken)
          val fieldName = jsonParser.currentName()
          nextToken = jsonParser.nextToken()
          list += ((fieldName, nextLoad(jsonParser, nextToken, Some(JsonMapPath(fieldName)))))
          nextToken = jsonParser.nextToken()
        }
        JsonMapValue(list, path)
      case _ => JsonNullValue(path)
    }
  }
}