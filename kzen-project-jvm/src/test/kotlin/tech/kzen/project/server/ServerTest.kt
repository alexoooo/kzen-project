package tech.kzen.project.server

import tech.kzen.project.common.getAnswerBar
import org.junit.Assert.assertEquals
import kotlin.test.Test


class ServerTest {
    @Test
    fun `simple test`() {
        assertEquals(42, getAnswerBar())
    }
}
