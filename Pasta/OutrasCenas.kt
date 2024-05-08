package main.kotlin

import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.*

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class DbName(
    val id: String
)

@Target(AnnotationTarget.PROPERTY)
annotation class Length(
    val len: Int
)

@Target(AnnotationTarget.PROPERTY)
annotation class PrimaryKey

@Target(AnnotationTarget.PROPERTY)
annotation class Exclude

@DbName("STUDENT")
data class Student(
    @PrimaryKey
    @DbName("numero")
    val number: Int,
    @Length(50)
    @DbName("nome")
    val name: String,
    @DbName("degree")
    val type: StudentType? = null
)

enum class StudentType {
    Bachelor, Master, Doctoral
}

// obtem lista de atributos pela ordem do construtor primario
val KClass<*>.dataClassFields: List<KProperty<*>>
    get() {
        require(isData) { "instance must be data class" }
        return primaryConstructor!!.parameters.map { p ->
            declaredMemberProperties.find { it.name == p.name }!!
        }
    }

// saber se um KClassifier é um enumerado
val KClassifier?.isEnum: Boolean
    get() = this is KClass<*> && this.isSubclassOf(Enum::class)

// obter uma lista de constantes de um tipo enumerado
val KClassifier.getEnumValues: List<Enum<*>>
    get() {
        require(isEnum) { "instance must be enum" }
        this as KClass<out Enum<*>>
        return this.java.enumConstants.toList()
    }

val KClassifier?.asClass: KClass<*>
    get() {
        require(this is KClass<*>) { "instance must be KClass"}
        return this
    }

fun main() {
    val s = Student(90009, "Costa", StudentType.Doctoral)
    val clazz: KClass<*> = s::class
    clazz.dataClassFields.forEach {
        println(it.name + " = " + it.call(s))
    }

    val create = createTable(clazz)
    println(create)
}

fun main2() {
    /*val s = Student(90009, "Costa")
    val clazz: KClass<*> = s::class
    clazz.dataClassFields.forEach {
        println(it.name + " = " + it.call(s))
    }*/

    val s = Student(7, "Cristiano", StudentType.Doctoral)
    val sql: String = insertInto(s)
    println(sql)
}

fun main3() {
    val clazz = Student::class
    print(clazz.hasAnnotation<DbName>())

    val ann = clazz.findAnnotation<DbName>()
    print(ann?.id)

}

fun createTable(clazz: KClass<*>) =
    "CREATE TABLE ${clazz.simpleName}" +
            clazz.dataClassFields
                .joinToString(prefix = "(", postfix = ")") {
                    if (it.hasAnnotation<DbName>())
                        "${it.findAnnotation<DbName>()?.id} ${mapType(it.returnType)}"

                    if (it.hasAnnotation<PrimaryKey>())
                        "${it.name} ${mapType(it.returnType)} PRIMARY KEY"

                    if (it.hasAnnotation<Length>())
                        "${it.name} VARCHAR(${it.findAnnotation<Length>()?.len})"

                    else "${it.name} ${mapType(it.returnType)}"


                }

fun insertInto(obj: Any) =
    "INSERT INTO ${obj::class.simpleName}" +
            obj::class.dataClassFields
                .joinToString(prefix = "(", postfix = ")") {
                    "${it.name}"
                } + " VALUES" +
            obj::class.dataClassFields
                .joinToString(prefix = "(", postfix = ")") {
                    "${it.call(obj)}"
                }


fun mapType(type: KType): String {
    return when (type.classifier) {
        Int::class -> "INT"
        String::class -> "CHAR"
        StudentType::class -> "ENUM(‘Bachelor’, ‘Master’, ‘Doctoral’)"
        else -> TODO()
    } + if(type.isMarkedNullable) "" else " NOT NULL"
}