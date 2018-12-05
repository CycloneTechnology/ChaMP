package com.cyclone.wsman

case class ResourceUri(uri: String) extends AnyVal {
  private def appendSlash: String =
    if (uri.endsWith("/")) uri else uri + "/"

  def applyRelative(className: String): ResourceUri =
    if (className.startsWith("http:"))
      ResourceUri(className)
    else
      ResourceUri(appendSlash + className)

  def allClassesUriFrom: ResourceUri =
    if (uri.endsWith("/*"))
      ResourceUri(uri)
    else
      ResourceUri(appendSlash + "*")
}

object ResourceUri {

  // See https://msdn.microsoft.com/en-us/library/ee896656.aspx ...
  val defaultBase: ResourceUri = ResourceUri(
    "http://schemas.microsoft.com/wbem/wsman/1/wmi/root/cimv2/"
  )

}
