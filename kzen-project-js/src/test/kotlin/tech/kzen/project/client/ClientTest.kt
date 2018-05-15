@file:Suppress("unused")

package tech.kzen.project.client

import tech.kzen.project.common.getAnswerBar
import kotlin.test.Test
import kotlin.test.assertEquals

class ClientTest {
    // JS test names cannot contain illegal characters.

    @Test
    fun the_answer_should_be_correct() {
        assertEquals(42, getAnswerBar())
    }
}
