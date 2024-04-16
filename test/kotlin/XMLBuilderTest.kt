package test.kotlin

import main.kotlin.XMLDocument
import main.kotlin.XMLElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class XMLLibraryTest {

    @Test
    fun addXMLElementTest() {
        val document = XMLDocument()
        val element = XMLElement("plano")
        document.addRoot(element)
        val addedElement = document.xPath("plano").firstOrNull()
        assertEquals(true, addedElement != null)
    }

    @Test
    fun removeXMLElementTest() {
        val document = XMLDocument()
        val element = XMLElement("plano")
        val element1 = XMLElement("plano1")
        val element2 = XMLElement("plano2")
        document.addRoot(element)
        element.addElement(element1)
        element1.addElement(element2)
        document.removeElement("plano2")
        val removedElement = document.xPath("plano2").firstOrNull()
        assertEquals(true, removedElement == null)
    }

    @Test
    fun addAttributeTest() {
        val element = XMLElement("element")
        element.addAttribute("attribute1", "value1")
        assertEquals("value1", element.getAttributes()["attribute1"])
    }

    @Test
    fun removeAttributeTest() {
        val element = XMLElement("element")
        element.addAttribute("attribute1", "value1")
        element.removeAttribute("attribute1")
        assertEquals(null, element.getAttributes()["attribute1"])
    }

    @Test
    fun updateAttributeTest() {
        val element = XMLElement("element")
        element.addAttribute("attribute1", "value1")
        element.updateAttribute("attribute1", "newValue")
        assertEquals("newValue", element.getAttributes()["attribute1"])
    }

    @Test
    fun accessParentEntityTest() {
        val parent = XMLElement("parent")
        val child = XMLElement("child")
        parent.addElement(child)
        assertEquals("parent", child.getParents()?.getElementName())
    }

    @Test
    fun accessChildEntityTest() {
        val parent = XMLElement("parent")
        val child1 = XMLElement("child1")
        val child2 = XMLElement("child2")
        parent.addElement(child1)
        parent.addElement(child2)
        assertEquals(listOf("child1","child2"), parent.getChildren())
    }

    @Test
    fun prettyPrintToStringTest() {
        val parent = XMLElement("parent")
        val child1 = XMLElement("child1", "content1")
        val child2 = XMLElement("child2", "content2")
        parent.addElement(child1)
        parent.addElement(child2)
        val prettyPrintedXML = parent.toText().trimIndent()
        val expectedPrettyPrintedXML = "" +
                "\u001B[38;5;210m<\u001B[31mparent\u001B[0m\u001B[38;5;210m>\u001B[0m\n" +
                "\t\u001B[38;5;210m<\u001B[31mchild1\u001B[0m\u001B[38;5;210m>\u001B[0mcontent1\u001B[38;5;210m</\u001B[31mchild1\u001B[38;5;210m>\u001B[0m\n" +
                "\t\u001B[38;5;210m<\u001B[31mchild2\u001B[0m\u001B[38;5;210m>\u001B[0mcontent2\u001B[38;5;210m</\u001B[31mchild2\u001B[38;5;210m>\u001B[0m\n" +
                "\u001B[38;5;210m</\u001B[31mparent\u001B[38;5;210m>\u001B[0m"

        assertEquals(expectedPrettyPrintedXML, prettyPrintedXML)
    }

    @Test
    fun writeToFileTest() {
        val parent = XMLElement("parent")
        val child1 = XMLElement("child1", "content1")
        val child2 = XMLElement("child2", "content2")
        parent.addElement(child1)
        parent.addElement(child2)
        val document = XMLDocument()
        document.addRoot(parent)
        val fileName = "test.xml"
        document.generateXMLFile(fileName)
        val file = File(fileName)
        assertTrue(file.exists())
        val fileContent = file.readText().trimIndent()

        val expectedFileContent = "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<parent>\n" +
                "\t<child1>content1</child1>\n" +
                "\t<child2>content2</child2>\n" +
                "</parent>"

        assertEquals(expectedFileContent, fileContent)
        file.delete()
    }

    @Test
    fun addAttributesGloballyTest() {
        val document = XMLDocument()
        val root = XMLElement("plano")
        document.addRoot(root)
        document.addAttribute("plano", "peso", "10%")
        assertEquals("10%", root.getAttributes().getValue("peso"))
    }

    @Test
    fun renameEntitiesGloballyTest() {
        val document = XMLDocument()
        val root = XMLElement("plano")
        document.addRoot(root)
        document.renameXMLElements("plano", "teste")
        assertEquals("teste", root.getElementName())
    }

    @Test
    fun renameAttributesGloballyTest() {
        val document = XMLDocument()
        val root = XMLElement("plano")
        document.addRoot(root)
        document.addAttribute("plano", "peso", "10%")
        document.renameAttributes("plano" ,"peso", "test")
        assertTrue(root.getAttributes().containsKey("test"))
    }

    @Test
    fun removeEntitiesGloballyTest() {
        val document = XMLDocument()
        val parent = XMLElement("parent")
        val parent1 = XMLElement("parent1")
        val child = XMLElement("child")
        val plano = XMLElement("plano")
        document.addRoot(plano)
        plano.addElement(parent)
        parent.addElement(child)
        plano.addElement(parent1)
        document.removeElement("parent")
        val removedElement = document.xPath("parent").firstOrNull()
        val removedElement1 = document.xPath("child").firstOrNull()
        assertTrue(removedElement == null && removedElement1 == null)
    }

    @Test
    fun removeAttributesGloballyTest() {
        val document = XMLDocument()
        val root = XMLElement("plano")
        document.addRoot(root)
        document.addAttribute("plano", "peso", "10%")
        document.removeAttributes("plano", "peso")
        assertFalse(root.getAttributes().containsKey("peso"))
    }

}

