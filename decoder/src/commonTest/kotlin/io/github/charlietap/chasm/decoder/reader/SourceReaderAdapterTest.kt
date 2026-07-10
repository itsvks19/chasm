package io.github.charlietap.chasm.decoder.reader

import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SourceReaderAdapterTest {

    @Test
    fun `copies source chunks into the requested destination range`() {
        val adapter = SourceReaderAdapter(FakeSourceReader(byteArrayOf(1, 2, 3)))
        val destination = byteArrayOf(0, 0, 0, 0)

        assertEquals(2, adapter.readAtMostTo(destination, 1, 3))
        assertContentEquals(byteArrayOf(0, 1, 2, 0), destination)
        assertEquals(1, adapter.readAtMostTo(destination, 1, 3))
        assertContentEquals(byteArrayOf(0, 3, 2, 0), destination)
        assertEquals(-1, adapter.readAtMostTo(destination, 1, 3))
    }

    @Test
    fun `rejects chunks larger than the requested destination range`() {
        val adapter = SourceReaderAdapter(
            FakeSourceReader(
                bytes = { amount -> ByteArray(amount + 1) },
            ),
        )

        val error = assertFailsWith<IllegalStateException> {
            adapter.readAtMostTo(ByteArray(2), 0, 2)
        }

        assertEquals("SourceReader returned 3 bytes for a 2 byte request", error.message)
    }
}
