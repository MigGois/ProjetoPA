package main.kotlin

import java.io.File

internal const val red = "\u001b[31m"
internal const val green = "\u001b[32m"
internal const val brightred = "\u001b[38;5;210m"
internal const val reset = "\u001b[0m"

class XMLElement(var name: String, var text: String? = null, var parent: XMLElement? = null) {
    private val children = mutableListOf<XMLElement>()
    private val attributes = mutableMapOf<String, String>()

    init {
        parent?.children?.add(this)
    }

    fun getElementName(): String{
        return name
    }

    // Obter entidades child
    fun getChildrens(): List<String> {
        return children.map { it.name }
    }

    fun getParents(): XMLElement? {
        return parent
    }

    fun addChild(element: XMLElement){
        element.parent?.children?.remove(element)
        children.add(element)
        element.parent = this
    }

    fun removeChild(name: String, attributes: Map<String, String>) {
        val iterator = children.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (element.name == name && element.attributes.entries.containsAll(attributes.entries)) {
                element.parent = null
                iterator.remove()
            }
        }
    }


    //Adicionar atributos numa categoria
    fun addAttribute(key: String, value: String) {
        attributes[key] = value
    }

    //Alterar atributos
    fun updateAttribute(key: String, value: String) {
        if (attributes.containsKey(key)) {
            attributes[key] = value
        } else {
            println("Atributo '$key' não existe.")
        }
    }

    fun renameAttribute(key: String, newkey: String) {
        if (attributes.containsKey(key)) {
            attributes[newkey] = attributes.remove(key)!!
        } else {
            println("Atributo '$key' não existe.")
        }
    }

    //Remover atributos
    fun removeAttribute(key: String) {
        if (attributes.containsKey(key)) {
            attributes.remove(key)
        } else {
            println("Atributo '$key' não existe.")
        }
    }

    val depth: Int
        get() {
            return if(parent == null){
                0
            }else{
                1 + parent!!.depth
            }
        }

    fun toText(): String {
        var indent = "\t".repeat(depth)
        val sb = StringBuilder()
        sb.append("$indent$brightred<$red$name$reset")
        for ((key, value) in attributes) {
            sb.append(" $green$key$brightred=\"$reset$value$brightred\"")
        }
        if (children.isEmpty() && text == null) {
            sb.append("$brightred/>\n")
        } else {
            if (text != null) {
                sb.append("$brightred>$reset$text")
                indent = "\t".repeat(0)
            }else{
                sb.append("$brightred>$reset\n")
            }
            for (child in children) {
                sb.append(child.toText())
            }
            sb.append("$indent$brightred</$red$name$brightred>$reset\n")
        }
        return sb.toString()
    }

    fun textToFile(): String{

            var indent = "\t".repeat(depth)
            val sb = StringBuilder()
            sb.append("$indent<$name")
            for ((key, value) in attributes) {
                sb.append(" $key=\"$value\"")
            }
            if (children.isEmpty() && text == null) {
                sb.append("/>\n")
            } else {
                if (text != null) {
                    sb.append(">$text")
                    indent = "\t".repeat(0)
                } else {
                    sb.append(">\n")
                }
                for (child in children) {
                    sb.append(child.textToFile())
                }
                sb.append("$indent</$name>\n")
            }
        return sb.toString()
    }

    fun accept(visitor: (XMLElement) -> Boolean){
        if(visitor(this)){
            children.forEach {
                it.accept (visitor)
            }
        }
    }
}


class XMLDocument {
    private var element: XMLElement? = null

    fun addElement(element: XMLElement) {
        this.element = element
    }

    fun generateXMLConsole(): String {

        val sb = StringBuilder()
        sb.append("$brightred<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append(element?.toText())

        return sb.toString()
    }

    fun generateXMLFile(name: String){

        try {
            val file = File(name)
            val sb = StringBuilder()
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            sb.append(element?.textToFile())

                if(file.isFile){
                    println("O ficheiro com '$name' já existe")
                }else{
                    println("O ficheiro '$name' foi criado")
                }

                file.writeText(sb.toString())
        }catch(e: Exception) {
            println("Ocorreu um problema na criação do ficheiro")
        }
    }

    fun accept(visitor: (XMLElement) -> Boolean){
        element!!.accept(visitor)
    }

    fun renameXMLElements(oldname: String, newname: String) {
        this.accept { if (it.name == oldname) it.name = newname; true }
    }

    fun renameAttributes(elementname:String, oldname: String, newname: String){
        this.accept { if(it.name == elementname) it.renameAttribute(oldname, newname); true}
    }

    fun xPath(name: String): List<XMLElement>{
        val list = mutableListOf<XMLElement>()
        this.accept { if(it.name == name) list.add(it); true }
        return list
    }

}

interface Visitor{
    fun visit(xml: XMLElement):Boolean
}

fun createExampleXML(){
    val document = XMLDocument()

    val plano = XMLElement("plano")
    document.addElement(plano)

    val curso = XMLElement("curso", "Mestrado em Engenharia Informática", plano)

    val fuc1 = XMLElement("fuc", parent = plano)
    fuc1.addAttribute("codigo", "M4310")

    val nome1 = XMLElement("nome", "Programação Avançada", fuc1)
    val ects1 = XMLElement("ects", "6.0", fuc1)
    val avaliacao1 = XMLElement("avaliacao", parent =  fuc1)

    val componente1 = XMLElement("componente", parent = avaliacao1)
    componente1.addAttribute("nome", "Quizzes")
    componente1.addAttribute("peso", "20%")

    val componente2 = XMLElement("componente", parent = avaliacao1)
    componente2.addAttribute("nome", "Projeto")
    componente2.addAttribute("peso", "80%")

    val fuc2 = XMLElement("fuc", parent = plano)
    fuc2.addAttribute("codigo", "03782")

    val nome2 = XMLElement("nome", "Dissertação", fuc2)
    val ects2 = XMLElement("ects", "42.0", fuc2)
    val avaliacao2 = XMLElement("avaliacao", parent = fuc2)

    val componente3 = XMLElement("componente", parent = avaliacao2)
    componente3.addAttribute("nome", "Dissertação")
    componente3.addAttribute("peso", "60%")

    val componente4 = XMLElement("componente", parent = avaliacao2)
    componente4.addAttribute("nome", "Apresentação")
    componente4.addAttribute("peso", "20%")

    val componente5 = XMLElement("componente", parent = avaliacao2)
    componente5.addAttribute("nome", "Discussão")
    componente5.addAttribute("peso", "20%")

    document.renameXMLElements("componente", "bacano")
    document.renameAttributes("bacano","nome", "apelido")

    println(document.generateXMLConsole().trimIndent())
    document.generateXMLFile("teste.xml")
    println("Childrens:" + avaliacao2.getChildrens())

}

fun main() {
    val exampleXML = createExampleXML()
    println(exampleXML)
}