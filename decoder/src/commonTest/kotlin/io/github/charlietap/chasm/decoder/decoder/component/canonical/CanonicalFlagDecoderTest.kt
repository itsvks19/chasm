package io.github.charlietap.chasm.decoder.decoder.component.canonical

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.error.ComponentCanonicalDecodeError
import io.github.charlietap.chasm.decoder.fixture.assertWasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class CanonicalFlagDecoderTest {

    @Test
    fun `decodes async flags`() {
        val cases = listOf(
            0x00 to Ok(false),
            0x01 to Ok(true),
            0x02 to Err(ComponentCanonicalDecodeError.InvalidAsyncFlag(0x02u)),
        )
        val decoder: ComponentDecoder<Boolean> = ::AsyncFlagDecoder

        cases.forEach { (encoded, result) ->
            val context = componentDecoderContext(BinaryReader(byteArrayOf(encoded.toByte())))
            val actual = decoder(context)
            val expected = result

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `decodes cancellable flags`() {
        val cases = listOf(
            0x00 to Ok(false),
            0x01 to Ok(true),
            0x02 to Err(ComponentCanonicalDecodeError.InvalidCancellableFlag(0x02u)),
        )
        val decoder: ComponentDecoder<Boolean> = ::CancellableFlagDecoder

        cases.forEach { (encoded, result) ->
            val context = componentDecoderContext(BinaryReader(byteArrayOf(encoded.toByte())))
            val actual = decoder(context)
            val expected = result

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `decodes shared flags`() {
        val cases = listOf(
            0x00 to Ok(false),
            0x01 to Ok(true),
            0x02 to Err(ComponentCanonicalDecodeError.InvalidSharedFlag(0x02u)),
        )
        val decoder: ComponentDecoder<Boolean> = ::SharedFlagDecoder

        cases.forEach { (encoded, result) ->
            val context = componentDecoderContext(BinaryReader(byteArrayOf(encoded.toByte())))
            val actual = decoder(context)
            val expected = result

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `propagates reader errors from each flag decoder`() {
        val error = ioError()

        val asyncContext = componentDecoderContext(IOErrorWasmFileReader(error))
        assertWasmDecodeError(error) {
            AsyncFlagDecoder(asyncContext)
        }

        val cancellableContext = componentDecoderContext(IOErrorWasmFileReader(error))
        assertWasmDecodeError(error) {
            CancellableFlagDecoder(cancellableContext)
        }

        val sharedContext = componentDecoderContext(IOErrorWasmFileReader(error))
        assertWasmDecodeError(error) {
            SharedFlagDecoder(sharedContext)
        }
    }
}
