package main.kotlin

sealed interface Element {
    val name: String
    val parent: DirectoryElement?
}

data class FileElement(
    override val name: String,
    override val parent: DirectoryElement? = null
) : Element {
    init {
        parent?.children?.add(this)
    }
}

data class DirectoryElement(
    override val name: String,
    override val parent: DirectoryElement? = null,
) : Element {
    internal val children: MutableList<Element> = mutableListOf()

    init {
        parent?.children?.add(this)
    }
}

fun directory(name: String, build: XMLElement.() -> Unit) =
    XMLElement(name).apply {
        build(this)
    }

fun XMLElement.directory(name: String, build: XMLElement.() -> Unit) =
    XMLElement(name, parent = this).apply {
        build(this)
    }

fun XMLElement.setName(name: String) = XMLElement(name)

operator fun DirectoryElement.get(name: String) = children.find { it.name == name } as FileElement
operator fun XMLElement.div(name: String): XMLElement = getChildren().find { it == name } as XMLElement


fun main() {
    val artists = DirectoryElement("artists")
    val beatles = DirectoryElement("beatles", artists)
    val help = DirectoryElement("help", beatles)
    val iNeedYou = FileElement("i need you", help)
    val letItBe = DirectoryElement("let it be", beatles)
    val getDown = FileElement("get down", letItBe)
    val twoOfUs = FileElement("two of us", letItBe)
    val dir = directory("artists") {
        directory("beatles") {
            directory("help") {
                file("i need you")
            }
        }
    }

    dir / "beatles" / "help" files { println(it) }
}


