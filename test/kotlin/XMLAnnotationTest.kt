package test.kotlin

import main.kotlin.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class FUCAdapter: ChangeXML {
    override fun changeElement(element: XMLElement): XMLElement {

        val list = element.getChildren()[2].getChildren()

        list.forEach {
            val att = it.getAttributes()["nome"]
            it.removeAttribute("nome")
            it.addAttribute("nome", att!!)
        }

        return element
    }
}


class AddPercentage: ChangeAttribute {
    override fun changeValue(value: Any): String {
        return "$value%"
    }
}

@ElementXML ("componente")
class componenteavaliacao(
    @AttributeXML("nome")
    val nome: String,
    @AttributeXML("valor")
    @XmlString(AddPercentage::class)
    val peso: Int
)

@ElementXML ("fuc")
@XMLadapter(FUCAdapter::class)
class fuc(
    @AttributeXML("codigoteste")
    val codigo: String,
    @ElementXML("nometeste")
    val nome: String,
    @ElementXML("ectsteste")
    val ects: Double,
    @ExcludeXML
    val observacoes: String,
    @ElementXML("avaliacaoteste")
    val avaliacao: List<componenteavaliacao>
)

@ElementXML ("fucdelist")
@XMLadapter(FUCAdapter::class)
class fucDelist(
    @AttributeXML("codigo")
    val codigo: String,
    @ElementXML("nometeste")
    val nome: String,
    @ElementXML("ectsteste")
    val ects: Double,
    @ExcludeXML
    val observacoes: String,
    @ElementXML("avaliacaoteste")
    @XmlDelist
    val avaliacao: List<componenteavaliacao>
)

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


    val fd = fucDelist(
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
        assertEquals("100%", xmlElement.getAttributes()["valor"])
    }

    @Test
    fun fucXMLTranslationTest() {

        val xmlElement = translate(f)

        assertEquals("fuc", xmlElement.getElementName())
        assertEquals("M4310", xmlElement.getAttributes()["codigoteste"])

        val childElements = xmlElement.getChildren()
        val childAvaliacao = xmlElement.getChildren()[2]

        assertEquals(3, childElements.size)
        assertEquals("nometeste", childElements[0].getElementName())

        assertEquals("Quizzes", childAvaliacao.getChildren()[0].getAttributes()["nome"])
        assertEquals("20%", childAvaliacao.getChildren()[0].getAttributes()["valor"])

        assertEquals("Projeto", childAvaliacao.getChildren()[1].getAttributes()["nome"])
        assertEquals("80%", childAvaliacao.getChildren()[1].getAttributes()["valor"])
    }

    @Test
    fun exclusionTest() {

        val xmlElement = translate(f)
        assertNull(xmlElement.getAttributes()["observacoes"])
    }

    @Test
    fun unListTest() {

        val xmlElement = translate(fd)
        val childElements = xmlElement.getChildren()

        assertEquals(4, childElements.size)
        assertEquals("nometeste", childElements[0].getElementName())
        assertEquals("ectsteste", childElements[1].getElementName())
    }

    @Test
    fun adapterTest() {
        val xmlElement = translate(f)

        val expectedXml = "<fuc codigoteste=\"M4310\">\n" +
                "\t<nometeste>Programação Avançada</nometeste>\n" +
                "\t<ectsteste>6.0</ectsteste>\n" +
                "\t<avaliacaoteste>\n" +
                "\t\t<componente valor=\"20%\" nome=\"Quizzes\"/>\n" +
                "\t\t<componente valor=\"80%\" nome=\"Projeto\"/>\n" +
                "\t</avaliacaoteste>\n" +
                "</fuc>\n"

        assertEquals(expectedXml, xmlElement.textToFile())
    }

    //DSL

    @Test
    fun testDSl(){

        val dir = directory("artists") {
            directory("beatles") {
                directory("help") {
                }
            }
        }

        val help : XMLElement = dir / "beatles"

        assertEquals(help.textToFile().trim(), "<beatles>\n" +
                "\t<help/>\n" +
                "</beatles>")

    }
}
