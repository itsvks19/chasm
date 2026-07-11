package io.github.charlietap.chasm.decoder.decoder.component.canonical

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.CanonicalOption
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.error.ComponentCanonicalDecodeError
import io.github.charlietap.chasm.decoder.fixture.assertWasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.ast.component.asyncCanonicalOption
import io.github.charlietap.chasm.fixture.ast.component.callbackCanonicalOption
import io.github.charlietap.chasm.fixture.ast.component.latin1Utf16ComponentStringEncoding
import io.github.charlietap.chasm.fixture.ast.component.memoryCanonicalOption
import io.github.charlietap.chasm.fixture.ast.component.postReturnCanonicalOption
import io.github.charlietap.chasm.fixture.ast.component.reallocCanonicalOption
import io.github.charlietap.chasm.fixture.ast.component.stringEncodingCanonicalOption
import io.github.charlietap.chasm.fixture.ast.component.utf16ComponentStringEncoding
import io.github.charlietap.chasm.fixture.ast.component.utf8ComponentStringEncoding
import io.github.charlietap.chasm.fixture.ast.module.functionIndex
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail
import io.github.charlietap.chasm.ast.module.Index.FunctionIndex as ModuleFunctionIndex
import io.github.charlietap.chasm.ast.module.Index.MemoryIndex as ModuleMemoryIndex

class CanonicalOptionDecoderTest {

    @Test
    fun `decodes canonical options without indices`() {
        val cases = listOf(
            0x00 to stringEncodingCanonicalOption(encoding = utf8ComponentStringEncoding()),
            0x01 to stringEncodingCanonicalOption(encoding = utf16ComponentStringEncoding()),
            0x02 to stringEncodingCanonicalOption(encoding = latin1Utf16ComponentStringEncoding()),
            0x06 to asyncCanonicalOption(),
        )
        val decoder: ComponentDecoder<CanonicalOption> = { context ->
            CanonicalOptionDecoder(
                context = context,
                memoryIndexDecoder = neverMemoryIndexDecoder,
                functionIndexDecoder = neverFunctionIndexDecoder,
            )
        }

        cases.forEach { (opcode, option) ->
            val context = componentDecoderContext(BinaryReader(byteArrayOf(opcode.toByte())))

            val actual = decoder(context)

            val expected = Ok(option)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `decodes a memory option`() {
        val context = componentDecoderContext(BinaryReader(byteArrayOf(0x03)))
        val memory = memoryIndex(index = 7u)
        val memoryIndexDecoder: ComponentDecoder<ModuleMemoryIndex> = { Ok(memory) }

        val actual = CanonicalOptionDecoder(
            context = context,
            memoryIndexDecoder = memoryIndexDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
        )

        val expected = Ok(memoryCanonicalOption(index = memory))
        assertEquals(expected, actual)
    }

    @Test
    fun `decodes canonical function options`() {
        val cases = listOf(
            0x04 to ::reallocCanonicalOption,
            0x05 to ::postReturnCanonicalOption,
            0x07 to ::callbackCanonicalOption,
        )
        val function = functionIndex(index = 4u)
        val functionIndexDecoder: ComponentDecoder<ModuleFunctionIndex> = { Ok(function) }
        val decoder: ComponentDecoder<CanonicalOption> = { context ->
            CanonicalOptionDecoder(
                context = context,
                memoryIndexDecoder = neverMemoryIndexDecoder,
                functionIndexDecoder = functionIndexDecoder,
            )
        }

        cases.forEach { (opcode, option) ->
            val context = componentDecoderContext(BinaryReader(byteArrayOf(opcode.toByte())))

            val actual = decoder(context)

            val expected = Ok(option(function))
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `rejects an unknown canonical option`() {
        val context = componentDecoderContext(BinaryReader(byteArrayOf(0x08)))

        val actual = CanonicalOptionDecoder(
            context = context,
            memoryIndexDecoder = neverMemoryIndexDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
        )

        val expected = Err(ComponentCanonicalDecodeError.UnknownOption(0x08u))
        assertEquals(expected, actual)
    }

    @Test
    fun `propagates a reader error`() {
        val error = ioError()
        val context = componentDecoderContext(IOErrorWasmFileReader(error))

        assertWasmDecodeError(error) {
            CanonicalOptionDecoder(
                context = context,
                memoryIndexDecoder = neverMemoryIndexDecoder,
                functionIndexDecoder = neverFunctionIndexDecoder,
            )
        }
    }

    private companion object {
        val neverMemoryIndexDecoder: ComponentDecoder<ModuleMemoryIndex> = {
            fail("memory index decoder should not be called")
        }
        val neverFunctionIndexDecoder: ComponentDecoder<ModuleFunctionIndex> = {
            fail("function index decoder should not be called")
        }
    }
}
