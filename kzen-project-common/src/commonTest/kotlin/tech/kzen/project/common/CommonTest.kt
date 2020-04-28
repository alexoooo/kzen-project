package tech.kzen.project.common

import kotlin.test.Test
import kotlin.test.assertEquals


class CommonTest {
    @Test
    fun should_return_the_one_and_only_answer() {
        assertEquals(42, getAnswerBar())
    }
}
