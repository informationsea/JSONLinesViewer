package info.informationsea.jsonview.values

case class JsonMapPath(key: String) extends JsonPath {
  override def pathAsPython: String = "[\"" + key + "\"]"

  override def pathAsString: String = key
}
