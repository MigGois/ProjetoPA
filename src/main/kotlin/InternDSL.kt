package main.kotlin

fun directory(name: String, build: XMLElement.() -> Unit) =
    XMLElement(name).apply {
        build(this)
    }

fun XMLElement.directory(name: String, build: XMLElement.() -> Unit) =
    XMLElement(name, parent = this).apply {
        build(this)
    }

operator fun XMLElement.div(name: String): XMLElement = getChildren().find { it.name == name } as XMLElement


fun main() {

    val dir = directory("artists") {
        directory("beatles") {
            directory("help") {
            }
        }
    }

    val help : XMLElement = dir

    println(help.toText())
}


