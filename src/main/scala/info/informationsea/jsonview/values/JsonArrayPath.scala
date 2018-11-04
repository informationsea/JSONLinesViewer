package info.informationsea.jsonview.values

case class JsonArrayPath(index: Int) extends JsonPath {
  override def pathAsPython: String = "[" + index + "]"

  override def pathAsString: String = Integer.toString(index)
}
