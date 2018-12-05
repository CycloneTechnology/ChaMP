package com.cyclone.wsman.impl.model

import com.cyclone.util.XmlUtils.{attributeValue, childElements, prettyPrint}
import com.cyclone.wsman.impl.Namespace
import com.cyclone.wsman.impl.xml.EprXML
import com.cyclone.wsman.command.{WSManInstance, WSManPropertyValue}
import com.typesafe.scalalogging.LazyLogging

import scala.Option.option2Iterable
import scala.collection.immutable.HashMap
import scala.collection.{Iterable, Seq}
import scala.xml.NodeSeq.seqToNodeSeq
import scala.xml._

private[wsman] object ManagedInstance extends LazyLogging {

  def fromResourceURI(resourceURI: String, prefix: String = "p"): ManagedInstance =
    apply(documentElement(resourceURI, prefix))

  def apply(elementName: String, namespace: String, prefix: String = "p"): ManagedInstance =
    apply(documentElement(elementName, namespace, prefix))

  def apply(elem: Elem): ManagedInstance = {
    val mi = new ManagedInstance(elem)

    logger.debug(s"Created instance $elem")

    mi
  }

  private def documentElement(resourceURI: String, prefix: String): Elem = {
    val pos = resourceURI.lastIndexOf('/')

    if (pos > 0) {
      documentElement(resourceURI.substring(pos + 1), resourceURI, prefix)
    } else {
      <xml/>.copy(label = resourceURI, prefix = prefix)
    }
  }

  private def documentElement(elementName: String, namespace: String, prefix: String) =
    <xml/>.copy(
      prefix = prefix,
      label = elementName,
      scope = NamespaceBinding(prefix, namespace, TopScope)
    )
}

/**
  * A representation of a WS-Management instance.
  *
  * Holds its data in XML format (typically in the form received in the SOAP response).
  *
  * Note: ManagedInstance instances are immutable. All 'update' methods return new instances.
  */
private[wsman] class ManagedInstance private (private[impl] val root: Elem) {

  /**
    * @return the external (API) equivalent instance
    */
  def external: WSManInstance = {

    lazy val toExternalPV: PropertyValue => WSManPropertyValue = {
      case StringPropertyValue(v)      => WSManPropertyValue.ForString(v.toString)
      case ListPropertyValue(vs)       => WSManPropertyValue.ForArray(vs.map(toExternalPV): _*)
      case InstancePropertyValue(inst) => WSManPropertyValue.ForInstance(inst.external)
      case ReferencePropertyValue(ref) => WSManPropertyValue.ForReference(ref.getResourceURI)
    }

    lazy val toExternal: ((String, PropertyValue)) => (String, WSManPropertyValue) = {
      case (name, pv) => (name, toExternalPV(pv))
    }

    WSManInstance(allPropertyNamesAndValues.map(toExternal): _*)
  }

  def propertyNames: List[String] = {
    childElts.map(_.label).toList.distinct
  }

  private def childElts: NodeSeq = childElements(root)

  private def elementsWithName(name: String): Seq[Elem] =
    childElts.filter(_.label == name).map(_.asInstanceOf[Elem])

  def getResourceURI: String = root.namespace

  private def copyRootWithChildren(children: Seq[Node]) = root.copy(child = children)

  private def getElementPropertyMetaData(elt: Elem): Option[PropertyMetaData[PropertyValue]] = {
    import PropertyValueConverterRegistry._

    // Extract type name from explicit xsi:type or else from child element's name
    // (MS seems inconsistent in which it supplies!)

    // ^^TODO why?? - can we force it to do it properly? Is it something we're doing wrong?
    def typeName(parent: Elem, child: Elem): Option[String] =
      attributeValue(elt, Namespace.XMLSCHEMA_INSTANCE, "type")
        .orElse(Option(child.label))

    // If the element has a child it may be a managed instance, reference or other complex type.
    // If not just extract the text.
    childElements(elt).headOption match {
      case Some(child) =>
        if (elt \ "Address" != NodeSeq.Empty && elt \ "ReferenceParameters" != NodeSeq.Empty)
          Some(ManagedReferencePropertyMetaData(elt))
        else
          (for (t         <- typeName(elt, child);
                converter <- converterFor(t)) yield ConverterPropertyMetaData(elt, converter))
            .orElse(Some(ManagedInstancePropertyMetaData(elt)))
      case None =>
        Some(ConverterPropertyMetaData(elt, { e =>
          StringPropertyValue(e.text)
        }))
    }
  }

  private def metaDatasForTopLevelProperty(
    name: String
  ): Iterable[PropertyMetaData[PropertyValue]] = {
    for (e   <- elementsWithName(name);
         opt <- getElementPropertyMetaData(e)) yield opt
  }

  private def assertValidPropertyName(name: String): Unit =
    require(!name.contains('.'), "Names of created properties cannot contain dots: " + name)

  /**
    * Gets the value of a property.
    */
  def getPropertyValue(name: String): Option[PropertyValue] = {
    metaDatasForTopLevelProperty(name).toList match {
      case Nil        => None
      case List(meta) => Some(meta.evaluate)
      case metas: List[PropertyMetaData[PropertyValue]] =>
        Some(ListPropertyValue(metas.map(_.evaluate)))
    }
  }

  /**
    * Gets all top-level property names and values.
    *
    * Properties of embedded instances are not returned: that would require additional calls
    * to this method for those instances.
    *
    * @param predicate to filter the names and values returned
    * @return property names and values as tuples
    */
  def propertyNamesAndValues(
    predicate: (String, PropertyValue) => Boolean
  ): Seq[(String, PropertyValue)] = {
    for {
      name  <- propertyNames
      value <- getPropertyValue(name) if predicate(name, value)
    } yield (name, value)
  }

  def allPropertyNamesAndValues: Seq[(String, PropertyValue)] =
    propertyNamesAndValues((_, _) => true)

  /**
    * Immutable update of a named property.
    *
    * @return the updated instance
    */
  def withProperty(name: String, value: PropertyValue): ManagedInstance =
    removeProperty(name).addProperty(name, value)

  def withProperty(name: String, value: String): ManagedInstance =
    withProperty(name, StringPropertyValue(value))

  private def addProperty(name: String, value: PropertyValue): ManagedInstance = {
    assertValidPropertyName(name)

    // @formatter:off
    value match {
      case StringPropertyValue(v) =>
        ManagedInstance(copyRootWithChildren(
          childElts ++ <xml>{ v.toString }</xml>.copy(prefix = root.prefix,
            label = name,
            scope = root.scope)))

      case InstancePropertyValue(inst) =>
        ManagedInstance(copyRootWithChildren(
          childElts ++ <xml>{ inst.root.child }</xml>.copy(prefix = null,
            label = name)))

      case ReferencePropertyValue(ref) =>
        ManagedInstance(copyRootWithChildren(
          childElts ++ <xml>{ ref.root.child }</xml>.copy(prefix = root.prefix,
            label = name,
            scope = root.scope)))

      case ListPropertyValue(values) => values match {
        case Nil     => this
        case v :: vs => addProperty(name, v).addProperty(name, ListPropertyValue(vs))
      }
    }
    // @formatter:on
  }

  private def removeProperty(name: String) =
    ManagedInstance(copyRootWithChildren(childElts.filter(_.label != name)))

  // FIXME return this if no property with name present ^^ (i.e. remove is no-op)

  override def toString: String = prettyPrint(root)

  /**
    * Registry for looking up property meta data by type
    *
    * @author Jeremy.Stone
    */
  object PropertyValueConverterRegistry {
    private val map = HashMap[String, Elem => PropertyValue]() +
    ("cim:cimDateTime" -> CIMDateTimePropertyConverter) +
    ("Datetime"        -> CIMDateTimePropertyConverter)

    def converterFor(typeName: String): Option[Elem => PropertyValue] = map.get(typeName)
  }

  /**
    * Property meta data
    *
    * @author Jeremy.Stone
    */
  sealed abstract class PropertyMetaData[+T <: PropertyValue](element: Elem) {
    def evaluate: T

    def name: String = element.label
  }

  case class ConverterPropertyMetaData(element: Elem, converter: Elem => PropertyValue)
      extends PropertyMetaData[PropertyValue](element) {
    def evaluate: PropertyValue = converter(element)
  }

  case class ManagedInstancePropertyMetaData(element: Elem) extends PropertyMetaData[InstancePropertyValue](element) {
    def evaluate = InstancePropertyValue(ManagedInstance(element))
  }

  case class ManagedReferencePropertyMetaData(element: Elem) extends PropertyMetaData[ReferencePropertyValue](element) {

    def evaluate: ReferencePropertyValue = {
      val address = element \ "Address"
      val refData = element \ "ReferenceParameters"
      ReferencePropertyValue(ManagedReference(EprXML.forAddressAndRef(address, refData)))
    }
  }

  object CIMDateTimePropertyConverter extends (Elem => PropertyValue) {
    def apply(propertyElement: Elem) = StringPropertyValue(childElements(propertyElement).head.text)
  }

}
