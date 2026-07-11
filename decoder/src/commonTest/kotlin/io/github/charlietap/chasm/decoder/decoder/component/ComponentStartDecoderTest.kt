package io.github.charlietap.chasm.decoder.decoder.component

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.Index.ComponentFunctionIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentValueIndex
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.ComponentVectorDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.assertWasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUIntReader
import io.github.charlietap.chasm.fixture.ast.component.componentFunctionIndex
import io.github.charlietap.chasm.fixture.ast.component.componentValueIndex
import io.github.charlietap.chasm.fixture.ast.component.startDefinition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ComponentStartDecoderTest {

    @Test
    fun `decodes a component start definition`() {
        val functionIndex = componentFunctionIndex(2u)
        val args = listOf(componentValueIndex(3u), componentValueIndex(4u))
        val context = componentDecoderContext(FakeUIntReader { Ok(1u) })
        val functionIndexDecoder: ComponentDecoder<ComponentFunctionIndex> = {
            Ok(functionIndex)
        }
        val vectorDecoder: ComponentVectorDecoder<ComponentValueIndex> = { _, _ -> Ok(Vector(args)) }

        val actual = ComponentStartDecoder(
            context = context,
            functionIndexDecoder = functionIndexDecoder,
            valueIndexDecoder = neverValueIndexDecoder,
            vectorDecoder = vectorDecoder,
        )

        val expected = Ok(
            startDefinition(
                functionIndex = functionIndex,
                args = args,
                resultCount = 1u,
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `propagates a result count reader error`() {
        val error = WasmDecodeError.IOError(Exception())
        val context = componentDecoderContext(FakeUIntReader { Err(error) })
        val functionIndex = componentFunctionIndex(2u)
        val functionIndexDecoder: ComponentDecoder<ComponentFunctionIndex> = {
            Ok(functionIndex)
        }
        val vectorDecoder: ComponentVectorDecoder<ComponentValueIndex> = { _, _ -> Ok(Vector(emptyList())) }

        assertWasmDecodeError(error) {
            ComponentStartDecoder(
                context = context,
                functionIndexDecoder = functionIndexDecoder,
                valueIndexDecoder = neverValueIndexDecoder,
                vectorDecoder = vectorDecoder,
            )
        }
    }

    private companion object {
        val neverValueIndexDecoder: ComponentDecoder<ComponentValueIndex> = {
            fail("value index decoder must not be called directly")
        }
    }
}
