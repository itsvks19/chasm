package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.ComponentDecoder
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Component
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.fixture.config.componentConfig
import io.github.charlietap.chasm.stream.SourceReader
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import io.github.charlietap.chasm.fixture.ast.component.component as componentFixture

class ComponentTest {

    @Test
    fun `passes bytes and config to the component decoder`() {
        val bytes = byteArrayOf(0x01, 0x02)
        val config = componentConfig()
        val decoded = componentFixture()
        val decoder = ComponentDecoder<ByteArray> { actualConfig, actualBytes ->
            assertEquals(config, actualConfig)
            assertContentEquals(bytes, actualBytes)
            Ok(decoded)
        }

        val actual = assertIs<ChasmResult.Success<Component>>(
            component(bytes, config, decoder),
        ).result

        assertEquals(config, actual.config)
        assertEquals(decoded, actual.component)
    }

    @Test
    fun `passes a source reader and config to the component decoder`() {
        val source = FakeSourceReader()
        val config = componentConfig()
        val decoded = componentFixture()
        val decoder = ComponentDecoder<SourceReader> { actualConfig, actualSource ->
            assertEquals(config, actualConfig)
            assertSame(source, actualSource)
            Ok(decoded)
        }

        val actual = assertIs<ChasmResult.Success<Component>>(
            component(source, config, decoder),
        ).result

        assertEquals(config, actual.config)
        assertEquals(decoded, actual.component)
    }
}
