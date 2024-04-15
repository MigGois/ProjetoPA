package test.kotlin

import main.kotlin.XMLDocument
import main.kotlin.XMLElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File


internal const val red = "\u001b[31m"
internal const val green = "\u001b[32m"
internal const val brightred = "\u001b[38;5;210m"
internal const val reset = "\u001b[0m"

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
    fun accessChildEntitiesTest() {
        val parent = XMLElement("parent")
        val child1 = XMLElement("child1")
        val child2 = XMLElement("child2")
        parent.addElement(child1)
        parent.addElement(child2)
        val childrenNames = parent.getChildrens()
        assertEquals(listOf("child1", "child2"), childrenNames)
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
    fun visitElementsTest() {
        val parent = XMLElement("parent")
        val child1 = XMLElement("child1")
        val child2 = XMLElement("child2")
        parent.addElement(child1)
        parent.addElement(child2)

        val visitedElements = mutableListOf<String>()
        val visitor: (XMLElement) -> Boolean = { element ->
            visitedElements.add(element.getElementName())
            true
        }
        parent.accept(visitor)
        assertEquals(listOf("parent", "child1", "child2"), visitedElements)
    }

    @Test
    fun addAttributesGloballyTest() {
        val document = XMLDocument()
        val parent = XMLElement("parent")
        val child = XMLElement("child")
        parent.addElement(child)
        document.addRoot(parent)
        val addAttributesGlobally: (XMLElement) -> Boolean = { element ->
            if (element.getElementName() == "child") {
                element.addAttribute("globalAttribute", "value")
            }
            true
        }
        document.accept(addAttributesGlobally)
        assertEquals("value", child.getAttributes()["globalAttribute"])
    }

    @Test
    fun renameEntitiesGloballyTest() {
        val document = XMLDocument()
        val parent = XMLElement("oldName")
        val child = XMLElement("child")
        parent.addElement(child)
        document.addRoot(parent)
        val renameEntitiesGlobally: (XMLElement) -> Boolean = { element ->
            if (element.getElementName() == "oldName") {
                element.name = "newName"
            }
            true
        }
        document.accept(renameEntitiesGlobally)
        assertEquals("newName", parent.getElementName())
    }

    @Test
    fun removeEntitiesGloballyTest1() {
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
        assertEquals(true, removedElement == null && removedElement1 == null)
    }
}

