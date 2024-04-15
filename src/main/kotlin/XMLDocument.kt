package main.kotlin

import java.io.File

internal const val red = "\u001b[31m"
internal const val green = "\u001b[32m"
internal const val brightred = "\u001b[38;5;210m"
internal const val reset = "\u001b[0m"


/**
* Represents an [XMLelement] with a [name], optional [text] content, and optional [parent] element.
*
* @property name The name of the XML element.
* @property text The text content of the XML element, if any.
* @property parent The parent XML element, if any.
* @constructor Creates an XML element with name, text and parent.
*/

class XMLElement(var name: String, var text: String? = null, var parent: XMLElement? = null) {
    private val children = mutableListOf<XMLElement>()
    private val attributes = mutableMapOf<String, String>()

    init {
        parent?.children?.add(this)
    }

    /**
     * @return the [XMLElement.name].
     */
    fun getElementName(): String{
        return name
    }

    /**
     * @return a [List] of [XMLElement.name] from the children of the element.
     */
    fun getChildrens(): List<String> {
        return children.map { it.name }
    }

    /**
     * @return the [XMLElement.parent].
     */
    fun getParents(): XMLElement? {
        return parent
    }

    /**
     * Adds a [XMLElement] to the [XMLElement.children] list.
     *
     * @param element the [XMLElement] to add to the [XMLElement.children] list.
     */
    fun addElement(element: XMLElement){
        element.parent?.children?.remove(element)
        children.add(element)
        element.parent = this
    }

    /**
     * Removes a [XMLElement.children] from the [XMLElement] by its name and attributes.
     *
     * @param name The name of the [XMLElement.children] to remove.
     * @param attributes The attributes of the [XMLElement.children] to match for removal.
     */
    fun removeElement(name: String, attributes: Map<String, String> = mapOf()) {
        val iterator = children.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
                if (element.name == name && element.attributes.entries.containsAll(attributes.entries)) {
                    element.parent = null
                    iterator.remove()
            }
        }
    }

    /**
     * Adds an attribute to the [XMLElement].
     *
     * @return the [XMLElement.attributes]
     */
    fun getAttributes(): Map<String, String> {
        return attributes
    }

    /**
     * Adds an attribute to the [XMLElement].
     *
     * @param key The key of the [attributes].
     * @param value The value of the [attributes].
     */
    fun addAttribute(key: String, value: String) {
        attributes[key] = value
    }

    /**
     * Updates an [XMLElement.attributes] with the specified key to the given value.
     *
     * @param key The key of the [XMLElement.attributes] to update.
     * @param value The new value for the [XMLElement.attributes].
     */
    fun updateAttribute(key: String, value: String) {
        if (attributes.containsKey(key)) {
            attributes[key] = value
        } else {
            println("Atributo '$key' não existe.")
        }
    }

    /**
     * Renames an [XMLElement.attributes] from the old key to the new key.
     *
     * @param key The current key of the attribute to rename.
     * @param newkey The new key for the attribute.
     */
    fun renameAttribute(key: String, newkey: String) {
        if (attributes.containsKey(key)) {
            attributes[newkey] = attributes.remove(key)!!
        } else {
            println("Atributo '$key' não existe.")
        }
    }

    /**
     * Removes an attribute with the specified key.
     *
     * @param key The key of the attribute to remove.
     */
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

    /**
     * Converts the [XMLElement] and the [XMLElement.children] to a string representation.
     *
     * @return The string representation of the [XMLElement].
     */
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

    /**
     * Converts the [XMLElement] and the [XMLElement.children] to a string representation without colors to write in the file.
     *
     * @return The string representation of the [XMLElement] for file writing.
     */
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

/**
 * @constructor Represents an XML document.
 */
class XMLDocument {
    private var element: XMLElement? = null

    /**
     * Adds an [XMLElement] to be the root for to the [XMLDocument].
     *
     * @param element The [XMLElement] to add to the [XMLDocument].
     */
    fun addRoot(element: XMLElement) {
        this.element = element
    }

    /**
     * Removes an [XMLElement] in the [XMLDocument].
     *
     * @param element The [XMLElement] to remove to the [XMLDocument].
     */
    fun removeElement(name: String) {
        if(element?.name == name) element = null
        this.accept { it.removeElement(name) ; true }
    }

    /**
     * Generates XML content as a string.
     *
     * @return The XML content as a string.
     */
    fun generateXML(): String {

        val sb = StringBuilder()
        sb.append("$brightred<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append(element?.toText())

        return sb.toString()
    }

    /**
     * Generates a file with the specified name.
     *
     * @param name The name of the file to generate.
     */
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

    /**
     * Adds an attribute to the [XMLElement].
     *
     * @param name is the name to add in the [XMLElement.attributes] in the [XMLElement].
     * @param attributevalue The name of the attribute in the [XMLElement.attributes] of the [XMLElement].
     */
    fun addAttribute(name: String, attributename: String, attributevalue: String) {
        this.accept { if (it.name == name) it.addAttribute(attributename, attributevalue); true }
    }

    /**
     * Renames the [XMLElement.name] of the [XMLElement].
     *
     * @param oldname The name of the element you want to change.
     * @param newname The new name you want to be changed to.
     */
    fun renameXMLElements(oldname: String, newname: String) {
        this.accept { if (it.name == oldname) it.name = newname; true }
    }

    /**
     * Renames the attribute on the element that matches the elementname.
     *
     * @param elementname Identify the name of the element you will change the attribute.
     * @param oldname The name of the attribute you will change.
     * @param newname The name of what the attribute will be changed into.
     */
    fun renameAttributes(elementname:String, oldname: String, newname: String){
        this.accept { if(it.name == elementname) it.renameAttribute(oldname, newname); true}
    }

    /**
     * Removes the attribute of the specified entity
     *
     * @param entityname Identify the name of the element you will remove the attribute.
     * @param attributename The name of the attribute you will remove.
     */
    fun removeAttributes(entityname:String, attributename: String){
        this.accept { if(it.name == entityname) it.removeAttribute(attributename); true}
    }

    fun xPath(name: String): List<XMLElement>{
        val list = mutableListOf<XMLElement>()
        this.accept { if(it.name == name) list.add(it); true }
        return list
    }

}

/**
 * Creates an example XML document.
 */
fun createExampleXML(): String {
    val document = XMLDocument()

    val plano = XMLElement("plano")
    document.addRoot(plano)

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

    println(document.generateXML().trimIndent())
    document.generateXMLFile("teste.xml")
    println("Childrens:" + avaliacao2.getChildrens())

    //document.renameXMLElements("componente", "bacano")
    //document.renameAttributes("bacano","nome", "apelido")

    return document.generateXML().trimIndent()


}

fun main() {
    val exampleXML = createExampleXML()
    println(exampleXML)
}