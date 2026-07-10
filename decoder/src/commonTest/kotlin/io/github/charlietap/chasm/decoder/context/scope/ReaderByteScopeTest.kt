@file:OptIn(UnsafeResultValueAccess::class)

package io.github.charlietap.chasm.decoder.context.scope

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.annotation.UnsafeResultValueAccess
import io.github.charlietap.chasm.decoder.error.ReaderDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class ReaderByteScopeTest {

    @Test
    fun `scope accepts exact consumption and restores the parent reader`() {
        val reader = BinaryReader(byteArrayOf(1, 2, 3))
        val context = decoderContext(reader)

        val actual = ReaderByteScope(context, 2u) { scoped ->
            Ok(scoped.reader.bytes(2))
        }

        assertContentEquals(byteArrayOf(1, 2), actual.value)
        assertSame(reader, context.reader)
        assertEquals(3.toByte(), reader.byte())
    }

    @Test
    fun `scope rejects under consumption and restores the parent reader`() {
        val reader = BinaryReader(byteArrayOf(1, 2, 3))
        val context = decoderContext(reader)

        val actual = ReaderByteScope(context, 2u) { scoped ->
            Ok(scoped.reader.byte())
        }

        assertEquals(Err(ReaderDecodeError.SizeMismatch(2u, 1u)), actual)
        assertSame(reader, context.reader)
    }

    @Test
    fun `scope propagates decoder error and restores the parent reader`() {
        val reader = BinaryReader(byteArrayOf(1, 2))
        val context = decoderContext(reader)
        val error = WasmDecodeError.IOError(IllegalStateException())

        val actual = ReaderByteScope(context, 1u) { Err(error) }

        assertEquals(Err(error), actual)
        assertSame(reader, context.reader)
    }

    @Test
    fun `nested scopes consume within the parent bound`() {
        val reader = BinaryReader(byteArrayOf(1, 2, 3, 4))
        val context = decoderContext(reader)

        val actual = ReaderByteScope(context, 3u) { outer ->
            outer.reader.byte()
            ReaderByteScope(outer, 2u) { inner -> Ok(inner.reader.bytes(2)) }
        }

        assertContentEquals(byteArrayOf(2, 3), actual.value)
        assertSame(reader, context.reader)
        assertEquals(4.toByte(), reader.byte())
    }

    @Test
    fun `child scope cannot exceed parent bound`() {
        val reader = BinaryReader(byteArrayOf(1, 2))
        val context = decoderContext(reader)

        assertFailsWith<NoSuchElementException> {
            ReaderByteScope(context, 1u) { outer ->
                ReaderByteScope(outer, 2u) { Ok(Unit) }
            }
        }
    }
}
