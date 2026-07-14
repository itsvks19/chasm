package io.github.charlietap.chasm.decoder.decoder.vector

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.error.ComponentDecodeError
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUIntReader
import kotlin.test.Test
import kotlin.test.assertEquals

class ReaderVectorDecoderTest {

    @Test
    fun `rejects component vectors before allocating beyond the implementation limit`() {
        val size = MAX_COMPONENT_VECTOR_SIZE + 1u
        val reader = FakeUIntReader { Ok(size) }
        val context = componentDecoderContext(reader)
        val elementDecoder: ComponentDecoder<NameValue> = { Ok(NameValue("value")) }

        val actual = ReaderVectorDecoder(context, elementDecoder)
        val expected = Err(ComponentDecodeError.VectorTooLarge(size))

        assertEquals(expected, actual)
    }
}
