package main.kotlin

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class ElementXML(
    val name: String = "",
    val text: String = ""
)

@Target(AnnotationTarget.PROPERTY)
annotation class AttributeXML(
    val name: String
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class XmlDelist()

@Target(AnnotationTarget.PROPERTY)
annotation class XmlString(
    val attribute: KClass<out ChangeAttribute>
)

@Target(AnnotationTarget.CLASS)
annotation class XMLadapter(
    val adapter: KClass<out ChangeXML>
)

@Target(AnnotationTarget.PROPERTY)
annotation class ExcludeXML

@ElementXML ("componente")
class componenteavaliacao(
    @AttributeXML("nome")
    val nome: String,
    @AttributeXML("valor")
    @XmlString(AddPercentage::class)
    val peso: Int
)

@ElementXML ("fuc")
@XMLadapter(FUCAdapter::class)
class fuc(
    @AttributeXML("codigo")
    val codigo: String,
    @ElementXML("nome")
    val nome: String,
    @ElementXML("ects")
    val ects: Double,
    @ExcludeXML
    val observacoes: String,
    @ElementXML("avaliacao")
    @XmlDelist
    val avaliacao: List<componenteavaliacao>
)

interface ChangeAttribute{
    fun changeValue(value: Any): String
}

class AddPercentage: ChangeAttribute {
    override fun changeValue(value: Any): String {
        return "$value%"
    }
}

interface ChangeXML{
    fun changeElement(element: XMLElement): XMLElement
}

class FUCAdapter: ChangeXML {
    override fun changeElement(element: XMLElement): XMLElement {
        return element
    }
}

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

    if (xmlElement::class.hasAnnotation<XMLadapter>()) {
        val changeAttributeClass = xmlElement::class.findAnnotation<XMLadapter>()!!.adapter
        return changeAttributeClass.createInstance().changeElement(xmlElement)
    }

    return xmlElement
}


val KClass<*>.classFields: List<KProperty<*>>
    get() {
        return primaryConstructor!!.parameters.map { p ->
            declaredMemberProperties.find { it.name == p.name }!!
        }
    }

fun main(){
    val c = componenteavaliacao("Quizzes", 20)
    val f = fuc("M4310", "Programação Avançada", 6.0, "la la...",
        listOf(
            componenteavaliacao("Quizzes", 20),
            componenteavaliacao("Projeto", 80)
        )
    )

    val fucXml: XMLElement = translate(f)
    println(fucXml.toText())

}
