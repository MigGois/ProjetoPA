package main.kotlin

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class ElementXML(
    val name: String,
    val text: String = ""
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class AttributeXML(
    val name: String
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class ExcludeXML

@ElementXML ("componente")
class componenteavaliacao(
    @AttributeXML("nome")
    val nome: String,
    @AttributeXML("peso")
    val peso: Int)

@ElementXML ("fuc")
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
    val avaliacao: List<componenteavaliacao>
)

//OPERADOR ELVIS

fun translate(obj: Any): XMLElement {
    val xmlElement = XMLElement(obj::class.findAnnotation<ElementXML>()?.name ?: obj::class.simpleName!!, obj::class.findAnnotation<ElementXML>()?.text ?: "")

    obj::class.classFields.forEach{ prop ->
        if(prop.hasAnnotation<ExcludeXML>()){
        }else if(prop.hasAnnotation<AttributeXML>()) {
            val propName = prop.findAnnotation<AttributeXML>()?.name ?: prop.name
            val propValue = prop.getter.call(obj)?.toString() ?: "null"
            xmlElement.addAttribute(propName, propValue)
        }else if(prop.hasAnnotation<ElementXML>()){
            if(prop.getter.call(obj) is List<*>){
                val element1 = XMLElement(prop.findAnnotation<ElementXML>()?.name ?: prop.name,"", xmlElement)
                (prop.getter.call(obj) as List<*>).forEach { element ->
                    if (element != null) {
                        element1.addElement(translate(element))
                    }
                }
            }else{
                XMLElement(prop.findAnnotation<ElementXML>()?.name ?: prop.name, prop.getter.call(obj).toString() ?: "", xmlElement)
            }
        }
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
    val sql: XMLElement = translate(f)
    println(sql.toText())

}













