package main.kotlin

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

/**
 * Annotation used to mark a class or property for XML serialization, specifying [XMLElement] properties.
 * @param name The name of the [XMLElement].
 * @param text The text content of the [XMLElement].
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class ElementXML(
    val name: String = "",
    val text: String = ""
)

/**
 * Annotation used to mark a property for XML serialization as an attribute.
 * @param name The name of the XML attribute.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class AttributeXML(
    val name: String
)

/**
 * Annotation used to mark a class or property for XML serialization as a list of elements.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class XmlDelist()

/**
 * Annotation used to mark a property for XML serialization as a string and specify a custom attribute transformation.
 * @param attribute The class implementing ChangeAttribute interface for attribute transformation.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class XmlString(
    val attribute: KClass<out ChangeAttribute>
)

/**
 * Annotation used to specify a custom [XMLadapter] for transforming XML elements.
 * @param adapter The class implementing ChangeXML interface for element transformation.
 */
@Target(AnnotationTarget.CLASS)
annotation class XMLadapter(
    val adapter: KClass<out ChangeXML>
)

/**
 * Annotation used to exclude a property from XML serialization.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class ExcludeXML

/**
 * Interface defining a method to change the value of an attribute.
 */
interface ChangeAttribute{
    fun changeValue(value: Any): String
}

/**
 * Interface defining a method to change the structure of an [XMLElement].
 */
interface ChangeXML{
    fun changeElement(element: XMLElement): XMLElement
}

/**
 * Function to translate an object to an [XMLElement].
 * @param obj The object to be translated.
 * @return The [XMLElement] representing the object.
 */
fun translate(obj: Any): XMLElement {

val xmlElement: XMLElement = if(obj::class.findAnnotation<ElementXML>()?.name.isNullOrEmpty()) {
    XMLElement(obj::class.simpleName!!, obj::class.findAnnotation<ElementXML>()?.text ?: "")
} else {
    XMLElement(obj::class.findAnnotation<ElementXML>()?.name ?: obj::class.simpleName!!, obj::class.findAnnotation<ElementXML>()?.text ?: "")
}

    obj::class.classFields.forEach{ prop ->
        if(prop.hasAnnotation<ExcludeXML>()){
            //do nothing
        }
        else if(prop.hasAnnotation<AttributeXML>()) {
            val propName = prop.findAnnotation<AttributeXML>()?.name ?: prop.name
            val propValue = prop.getter.call(obj)?.toString() ?: "null"
            if(prop.hasAnnotation<XmlString>()) {
                val changeAttributeClass = prop.findAnnotation<XmlString>()?.attribute
                val changeAttributeInstance = changeAttributeClass?.createInstance()
                val modifiedValue = changeAttributeInstance?.changeValue(propValue) ?: propValue
                xmlElement.addAttribute(propName, modifiedValue)
            }else {
                xmlElement.addAttribute(propName, propValue)
            }
        }else if(prop.hasAnnotation<ElementXML>()){
            if(prop.getter.call(obj) is List<*>){
                if(prop.hasAnnotation<XmlDelist>()){
                    (prop.getter.call(obj) as List<*>).forEach { element ->
                        if (element != null) {
                            xmlElement.addElement(translate(element))
                        }
                    }
                } else {
                    val element1 = XMLElement(prop.findAnnotation<ElementXML>()?.name ?: prop.name,"", xmlElement)
                    (prop.getter.call(obj) as List<*>).forEach { element ->
                        if (element != null) {
                            element1.addElement(translate(element))
                        }
                    }
                }
            } else {
                XMLElement(prop.findAnnotation<ElementXML>()?.name ?: prop.name, prop.getter.call(obj).toString(), xmlElement)
            }
        }
    }

    if (obj::class.hasAnnotation<XMLadapter>()) {
        val changeAttributeClass = obj::class.findAnnotation<XMLadapter>()!!.adapter
        return changeAttributeClass.createInstance().changeElement(xmlElement)

    } else {

        return xmlElement
    }
}

/**
 * Extension property to retrieve all declared fields (properties) of a Kotlin class.
 */
val KClass<*>.classFields: List<KProperty<*>>
    get() {
        return primaryConstructor!!.parameters.map { p ->
            declaredMemberProperties.find { it.name == p.name }!!
        }
    }
