package io.github.charlietap.chasm.decoder.context

import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.config.componentConfig
import kotlin.test.Test
import kotlin.test.assertSame

class ComponentDecoderContextTest {

    @Test
    fun `creates the module context with the component module config`() {
        val config = componentConfig()
        val context = componentDecoderContext(config = config)

        val actual = context.moduleContext.config

        val expected = config.moduleConfig
        assertSame(expected, actual)
    }

    @Test
    fun `delegates reader mutation to the reusable module context`() {
        val context = componentDecoderContext()
        val replacement = FakeWasmBinaryReader()

        context.reader = replacement
        val actual = context.moduleContext.reader

        val expected = replacement
        assertSame(expected, actual)
    }
}
