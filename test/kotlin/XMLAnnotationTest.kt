package test.kotlin

import main.kotlin.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AnnotationTranslationTest {

    val f = fuc(
        "M4310",
        "Programação Avançada",
        6.0,
        "la la...",
        listOf(
            componenteavaliacao("Quizzes", 20),
            componenteavaliacao("Projeto", 80)
        )
    )

    @Test
    fun componentXMLTranslationTest() {
        val componente = componenteavaliacao("nome1", 100)
        val xmlElement = translate(componente)

        assertEquals("componente", xmlElement.getElementName())
        assertEquals("nome1", xmlElement.getAttributes()["nome"])
        assertEquals("100%", xmlElement.getAttributes()["valor"]) // AddPercentage should modify the value to "20%"
    }

    @Test
    fun fucXMLTranslationTest() {

        val xmlElement = translate(f)

        assertEquals("fuc", xmlElement.getElementName())
        assertEquals("M4310", xmlElement.getAttributes()["codigo"])

        val childElements = xmlElement.getChildren()
        assertEquals(2, childElements.size)
        assertEquals("componente", childElements[0].getElementName())
        assertEquals("Quizzes", childElements[0].getAttributes()["nome"])
        assertEquals("20%", childElements[0].getAttributes()["valor"])

        assertEquals("componente", childElements[1].getElementName())
        assertEquals("Projeto", childElements[1].getAttributes()["nome"])
        assertEquals("80%", childElements[1].getAttributes()["valor"])
    }

    @Test
    fun exclusionTest() {

        val xmlElement = translate(f)
        assertNull(xmlElement.getAttributes()["observacoes"])
    }

    @Test
    fun unListTest() {

        val xmlElement = translate(f)
        val childElements = xmlElement.getChildren()

        assertEquals(2, childElements.size)
        assertEquals("componente", childElements[0].getElementName())
        assertEquals("componente", childElements[1].getElementName())
    }

    @Test
    fun adapterTest() {


        val xmlElement = translate(f)
        assertEquals("fuc", xmlElement.getElementName())
    }
}
