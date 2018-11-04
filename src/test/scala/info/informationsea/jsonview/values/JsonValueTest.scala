package info.informationsea.jsonview.values

import com.fasterxml.jackson.core.JsonFactory
import info.informationsea.jsonview.values
import org.scalatest.FlatSpec

class JsonValueTest extends FlatSpec {
  "simple1.json" should "be parsed correctly" in {
    val factory = new JsonFactory()
    val parser = factory.createParser(getClass.getResourceAsStream("/json/simple1.json"))
    val result = JsonValue.load(parser)

    val expected = JsonMapValue(Seq(
      ("foo", JsonStringValue("bar", Some(values.JsonMapPath("foo")))),
      ("bar", JsonIntValue(1, Some(JsonMapPath("bar")))),
    ), None)

    assert(expected === result)
  }

  "simple2.json" should "be parsed correctly" in {
    val factory = new JsonFactory()
    val parser = factory.createParser(getClass.getResourceAsStream("/json/simple2.json"))
    val result = JsonValue.load(parser)

    val expected = JsonArrayValue(IndexedSeq(
      JsonStringValue("foo", Some(JsonArrayPath(0))),
      JsonStringValue("bar", Some(JsonArrayPath(1))),
      JsonIntValue(1, Some(JsonArrayPath(2))),
      JsonFloatValue(1.2f, Some(JsonArrayPath(3)))
    ), None)

    assert(expected === result)
  }

  "false.json" should "be parsed correctly" in {
    val factory = new JsonFactory()
    val parser = factory.createParser(getClass.getResourceAsStream("/json/false.json"))
    val result = JsonValue.load(parser)
    val expected = JsonBooleanValue(false, None)
    assert(expected === result)
  }

  "true.json" should "be parsed correctly" in {
    val factory = new JsonFactory()
    val parser = factory.createParser(getClass.getResourceAsStream("/json/true.json"))
    val result = JsonValue.load(parser)
    val expected = JsonBooleanValue(true, None)
    assert(expected === result)
  }

  "float.json" should "be parsed correctly" in {
    val factory = new JsonFactory()
    val parser = factory.createParser(getClass.getResourceAsStream("/json/float.json"))
    val result = JsonValue.load(parser)
    val expected = JsonFloatValue(1.234f, None)
    assert(expected === result)
  }

  "int.json" should "be parsed correctly" in {
    val factory = new JsonFactory()
    val parser = factory.createParser(getClass.getResourceAsStream("/json/int.json"))
    val result = JsonValue.load(parser)
    val expected = JsonIntValue(123, None)
    assert(expected === result)
  }

  "string.json" should "be parsed correctly" in {
    val factory = new JsonFactory()
    val parser = factory.createParser(getClass.getResourceAsStream("/json/string.json"))
    val result = JsonValue.load(parser)
    val expected = JsonStringValue("string_value", None)
    assert(expected === result)
  }

  "complex.json" should "be parsed correctly" in {
    val factory = new JsonFactory()
    val parser = factory.createParser(getClass.getResourceAsStream("/json/complex.json"))
    val result = JsonValue.load(parser)
    val expected = JsonMapValue(Seq(
      ("complex", JsonMapValue(Seq(
        ("foo", JsonArrayValue(IndexedSeq(JsonIntValue(1, Some(JsonArrayPath(0))),
          JsonIntValue(2, Some(JsonArrayPath(1))), JsonIntValue(3, Some(JsonArrayPath(2))),
          JsonIntValue(4, Some(JsonArrayPath(3)))), Some(JsonMapPath("foo")))),
        ("bar", JsonStringValue("foo", Some(JsonMapPath("bar"))))
      ), Some(JsonMapPath("complex")))),
      ("foo", JsonMapValue(Seq(
        ("key1", JsonMapValue(Seq(
          ("key2", JsonMapValue(Seq(
            ("foo", JsonArrayValue(IndexedSeq(JsonIntValue(5, Some(JsonArrayPath(0))),
              JsonIntValue(6, Some(JsonArrayPath(1))),
              JsonIntValue(7, Some(JsonArrayPath(2)))), Some(JsonMapPath("foo"))))
          ), Some(JsonMapPath("key2"))))
        ), Some(JsonMapPath("key1"))))
      ), Some(JsonMapPath("foo")))),
      ("bar", JsonArrayValue(IndexedSeq(
        JsonMapValue(Seq(("key3", JsonStringValue("value3", Some(JsonMapPath("key3"))))), Some(JsonArrayPath(0))),
        JsonMapValue(Seq(("key4", JsonStringValue("value4", Some(JsonMapPath("key4")))),
          ("array", JsonArrayValue(IndexedSeq(
            JsonIntValue(1, Some(JsonArrayPath(0))),
            JsonStringValue("hoge", Some(JsonArrayPath(1))),
            JsonMapValue(Seq(("inner", JsonIntValue(2, Some(JsonMapPath("inner"))))),
              Some(JsonArrayPath(2)))), Some(JsonMapPath("array"))))),
          Some(JsonArrayPath(1))),
        JsonIntValue(1, Some(JsonArrayPath(2)))
      ), Some(JsonMapPath("bar")))),
      ("ID", JsonStringValue("id", Some(JsonMapPath("ID"))))
    ), None)
    assert(expected === result)
  }
}
