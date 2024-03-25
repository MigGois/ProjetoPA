package test.kotlin

import main.kotlin.createExampleXML
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal const val red = "\u001b[31m"
internal const val green = "\u001b[32m"
internal const val brightred = "\u001b[38;5;210m"
internal const val reset = "\u001b[0m"

class XMLLibraryTest {


    @Test
    fun XMLTest() {
        val generatedXML = createExampleXML()

        val expectedXML = """
            [38;5;210m<?xml version="1.0" encoding="UTF-8"?>
            [38;5;210m<[31mplano[0m[38;5;210m>[0m
                [38;5;210m<[31mcurso[0m[38;5;210m>[0mMestrado em Engenharia Informática[38;5;210m</[31mcurso[38;5;210m>[0m
                [38;5;210m<[31mfuc[0m [32mcodigo[38;5;210m="[0mM4310[38;5;210m"[38;5;210m>[0m
                    [38;5;210m<[31mnome[0m[38;5;210m>[0mProgramação Avançada[38;5;210m</[31mnome[38;5;210m>[0m
                    [38;5;210m<[31mects[0m[38;5;210m>[0m6.0[38;5;210m</[31mects[38;5;210m>[0m
                    [38;5;210m<[31mavaliacao[0m[38;5;210m>[0m
                        [38;5;210m<[31mcomponente[0m [32mnome[38;5;210m="[0mQuizzes[38;5;210m" [32mpeso[38;5;210m="[0m20%[38;5;210m"[38;5;210m/>
                        [38;5;210m<[31mcomponente[0m [32mnome[38;5;210m="[0mProjeto[38;5;210m" [32mpeso[38;5;210m="[0m80%[38;5;210m"[38;5;210m/>
                    [38;5;210m</[31mavaliacao[38;5;210m>[0m
                [38;5;210m</[31mfuc[38;5;210m>[0m
                [38;5;210m<[31mfuc[0m [32mcodigo[38;5;210m="[0m03782[38;5;210m"[38;5;210m>[0m
                    [38;5;210m<[31mnome[0m[38;5;210m>[0mDissertação[38;5;210m</[31mnome[38;5;210m>[0m
                    [38;5;210m<[31mects[0m[38;5;210m>[0m42.0[38;5;210m</[31mects[38;5;210m>[0m
                    [38;5;210m<[31mavaliacao[0m[38;5;210m>[0m
                        [38;5;210m<[31mcomponente[0m [32mnome[38;5;210m="[0mDissertação[38;5;210m" [32mpeso[38;5;210m="[0m60%[38;5;210m"[38;5;210m/>
                        [38;5;210m<[31mcomponente[0m [32mnome[38;5;210m="[0mApresentação[38;5;210m" [32mpeso[38;5;210m="[0m20%[38;5;210m"[38;5;210m/>
                        [38;5;210m<[31mcomponente[0m [32mnome[38;5;210m="[0mDiscussão[38;5;210m" [32mpeso[38;5;210m="[0m20%[38;5;210m"[38;5;210m/>
                    [38;5;210m</[31mavaliacao[38;5;210m>[0m
                [38;5;210m</[31mfuc[38;5;210m>[0m
            [38;5;210m</[31mplano[38;5;210m>[0m
        """.trimIndent().replace("    ", "\t")

        assertEquals(expectedXML, generatedXML)
    }

}

