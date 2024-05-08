package main.kotlin

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class DbName(
    val id: String
)

@DbName ("plano")
class componenteavaliacao(val nome: String, val peso: Int)


class fuc(
    val codigo: String,
    val nome: String,
    val ects: Double,
    val observacoes: String,
    val avaliacao: List<componenteavaliacao>
)

fun translate(obj: Any) : XMLElement =
    XMLElement(obj::class.findAnnotation<DbName>()?.id ?: obj::class.simpleName!!
        , obj::class.dataClassFields[0].toString()
    )


val KClass<*>.dataClassFields: List<KProperty<*>>
    get() {
        require(isData) { "instance must be data class" }
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
    val sql: XMLElement = translate(c)
    println(sql.toText())

}













