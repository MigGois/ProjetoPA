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
        assertNotNull(element)
    }

    @Test
    fun removeXMLElementTest() {
        val document = XMLDocument()
        val element = XMLElement("plano")
        val element1 = XMLElement("planonovo", parent = element)
        document.addRoot(element)
        element.addElement(element1)
        element.removeElement("plano1")
        assertFalse(element.getChildren().contains("plano1"))
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
        val child1 = XMLElement("childum")
        val child2 = XMLElement("childdois")
        parent.addElement(child1)
        parent.addElement(child2)
        assertEquals(listOf("childum","childdois"), parent.getChildren())
    }

    @Test
    fun prettyPrintToStringTest() {
        val parent = XMLElement("parent")
        val child1 = XMLElement("childum", "content1")
        val child2 = XMLElement("childdois", "content2")
        parent.addElement(child1)
        parent.addElement(child2)
        val prettyPrintedXML = parent.toText().trimIndent()
        val expectedPrettyPrintedXML = "" +
                "\u001B[38;5;210m<\u001B[31mparent\u001B[0m\u001B[38;5;210m>\u001B[0m\n" +
                "\t\u001B[38;5;210m<\u001B[31mchildum\u001B[0m\u001B[38;5;210m>\u001B[0mcontent1\u001B[38;5;210m</\u001B[31mchildum\u001B[38;5;210m>\u001B[0m\n" +
                "\t\u001B[38;5;210m<\u001B[31mchilddois\u001B[0m\u001B[38;5;210m>\u001B[0mcontent2\u001B[38;5;210m</\u001B[31mchilddois\u001B[38;5;210m>\u001B[0m\n" +
                "\u001B[38;5;210m</\u001B[31mparent\u001B[38;5;210m>\u001B[0m"

        assertEquals(expectedPrettyPrintedXML, prettyPrintedXML)
    }

    @Test
    fun writeToFileTest() {
        val parent = XMLElement("parent")
        val child1 = XMLElement("childum", "content1")
        val child2 = XMLElement("childdois", "content2")
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
                "\t<childum>content1</childum>\n" +
                "\t<childdois>content2</childdois>\n" +
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
        val parent1 = XMLElement("parentum")
        val child = XMLElement("child")
        val plano = XMLElement("plano")
        document.addRoot(plano)
        plano.addElement(parent)
        parent.addElement(child)
        plano.addElement(parent1)
        document.removeElement("parent")
        assertFalse(plano.getChildren().contains("parent"))
        assertFalse(plano.getChildren().contains("child"))
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

    @Test
    fun xPathTest() {
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


        val path = document.xPath("avaliacao/componente")


        assertEquals(listOf(componente1, componente2, componente3), path)
    }

}

