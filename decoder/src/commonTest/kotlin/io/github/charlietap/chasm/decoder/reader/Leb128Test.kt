package io.github.charlietap.chasm.decoder.reader

import io.github.charlietap.chasm.decoder.error.ReaderDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeException
import io.github.charlietap.chasm.stream.SourceReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class Leb128Test {

    @Test
    fun `decodes unsigned integers from contiguous buffers`() {
        assertDirect(byteArrayOf(0x05), 3, 5u.toUShort(), WasmBinaryReader::ushort)
        assertDirect(byteArrayOf(0x80.toByte(), 0x01), 3, 128u.toUShort(), WasmBinaryReader::ushort)
        assertDirect(byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0x03), 3, UShort.MAX_VALUE, WasmBinaryReader::ushort)

        assertDirect(byteArrayOf(0x05), 5, 5u, WasmBinaryReader::uint)
        assertDirect(byteArrayOf(0x80.toByte(), 0x01), 5, 128u, WasmBinaryReader::uint)
        assertDirect(byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x01), 5, 16_384u, WasmBinaryReader::uint)
        assertDirect(
            byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x01),
            5,
            2_097_152u,
            WasmBinaryReader::uint,
        )
        assertDirect(
            byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x0F),
            5,
            UInt.MAX_VALUE,
            WasmBinaryReader::uint,
        )

        assertDirect(byteArrayOf(0x09), 10, 9uL, WasmBinaryReader::ulong)
        (1..8).forEach { continuationBytes ->
            val encoded = ByteArray(continuationBytes + 1) { index ->
                if (index == continuationBytes) 0x01.toByte() else 0x80.toByte()
            }
            assertDirect(encoded, 10, 1uL shl (continuationBytes * 7), WasmBinaryReader::ulong)
        }
        assertDirect(
            byteArrayOf(
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0x01,
            ),
            10,
            ULong.MAX_VALUE,
            WasmBinaryReader::ulong,
        )
    }

    @Test
    fun `decodes signed integers from contiguous buffers`() {
        assertDirect(byteArrayOf(0x05), 3, 5.toShort(), WasmBinaryReader::short)
        assertDirect(byteArrayOf(0x80.toByte(), 0x01), 3, 128.toShort(), WasmBinaryReader::short)
        assertDirect(byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x7E), 3, Short.MIN_VALUE, WasmBinaryReader::short)

        assertDirect(byteArrayOf(0x05), 5, 5, WasmBinaryReader::int)
        assertDirect(byteArrayOf(0x80.toByte(), 0x01), 5, 128, WasmBinaryReader::int)
        assertDirect(byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x01), 5, 16_384, WasmBinaryReader::int)
        assertDirect(
            byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x01),
            5,
            2_097_152,
            WasmBinaryReader::int,
        )
        assertDirect(
            byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x78),
            5,
            Int.MIN_VALUE,
            WasmBinaryReader::int,
        )

        assertDirect(byteArrayOf(0x05), 5, 5L, WasmBinaryReader::s33)
        assertDirect(byteArrayOf(0x80.toByte(), 0x01), 5, 128L, WasmBinaryReader::s33)
        assertDirect(byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x01), 5, 16_384L, WasmBinaryReader::s33)
        assertDirect(
            byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x01),
            5,
            2_097_152L,
            WasmBinaryReader::s33,
        )
        assertDirect(
            byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x70),
            5,
            -4_294_967_296L,
            WasmBinaryReader::s33,
        )

        assertDirect(byteArrayOf(0x09), 10, 9L, WasmBinaryReader::long)
        (1..8).forEach { continuationBytes ->
            val encoded = ByteArray(continuationBytes + 1) { index ->
                if (index == continuationBytes) 0x01.toByte() else 0x80.toByte()
            }
            assertDirect(encoded, 10, 1L shl (continuationBytes * 7), WasmBinaryReader::long)
        }
        assertDirect(
            byteArrayOf(
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x7F,
            ),
            10,
            Long.MIN_VALUE,
            WasmBinaryReader::long,
        )
    }

    @Test
    fun `decodes multibyte integers from bounded buffers`() {
        assertBoundary(byteArrayOf(0x80.toByte(), 0x01), 128.toShort(), WasmBinaryReader::short)
        assertBoundary(byteArrayOf(0x80.toByte(), 0x01), 128u.toUShort(), WasmBinaryReader::ushort)
        assertBoundary(byteArrayOf(0x80.toByte(), 0x01), 128, WasmBinaryReader::int)
        assertBoundary(byteArrayOf(0x80.toByte(), 0x01), 128u, WasmBinaryReader::uint)
        assertBoundary(byteArrayOf(0x80.toByte(), 0x01), 128L, WasmBinaryReader::s33)
        assertBoundary(byteArrayOf(0x80.toByte(), 0x01), 128L, WasmBinaryReader::long)
        assertBoundary(byteArrayOf(0x80.toByte(), 0x01), 128uL, WasmBinaryReader::ulong)
    }

    @Test
    fun `rejects invalid terminal bytes from contiguous buffers`() {
        assertInvalid(byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x02), Short.SIZE_BITS, WasmBinaryReader::short)
        assertInvalid(byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x04), UShort.SIZE_BITS, WasmBinaryReader::ushort)
        assertInvalid(
            byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x08),
            Int.SIZE_BITS,
            WasmBinaryReader::int,
        )
        assertInvalid(
            byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x10),
            UInt.SIZE_BITS,
            WasmBinaryReader::uint,
        )
        assertInvalid(
            byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x20),
            33,
            WasmBinaryReader::s33,
        )
        assertInvalid(
            ByteArray(10) { index -> if (index == 9) 0x01.toByte() else 0x80.toByte() },
            Long.SIZE_BITS,
            WasmBinaryReader::long,
        )
        assertInvalid(
            ByteArray(10) { index -> if (index == 9) 0x02.toByte() else 0x80.toByte() },
            ULong.SIZE_BITS,
            WasmBinaryReader::ulong,
        )
    }

    @Test
    fun `decodes each integer type across source refills`() {
        assertStreaming(byteArrayOf(0xFF.toByte(), 0x7E), (-129).toShort(), WasmBinaryReader::short)
        assertStreaming(byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0x03), UShort.MAX_VALUE, WasmBinaryReader::ushort)
        assertStreaming(
            byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x78),
            Int.MIN_VALUE,
            WasmBinaryReader::int,
        )
        assertStreaming(
            byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x0F),
            UInt.MAX_VALUE,
            WasmBinaryReader::uint,
        )
        assertStreaming(
            byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x0F),
            UInt.MAX_VALUE.toLong(),
            WasmBinaryReader::s33,
        )
        assertStreaming(
            byteArrayOf(
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x80.toByte(),
                0x7F,
            ),
            Long.MIN_VALUE,
            WasmBinaryReader::long,
        )
        assertStreaming(
            byteArrayOf(
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0x01,
            ),
            ULong.MAX_VALUE,
            WasmBinaryReader::ulong,
        )
    }

    @Test
    fun `rejects invalid terminal bytes across source refills`() {
        assertStreamingInvalid(
            byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x10),
            UInt.SIZE_BITS,
            WasmBinaryReader::uint,
        )
        assertStreamingInvalid(
            ByteArray(10) { index -> if (index == 9) 0x02.toByte() else 0x80.toByte() },
            ULong.SIZE_BITS,
            WasmBinaryReader::ulong,
        )
    }

    private fun <T> assertDirect(
        encoded: ByteArray,
        width: Int,
        expected: T,
        decode: WasmBinaryReader.() -> T,
    ) {
        val reader = reader(encoded.copyOf(width))

        assertEquals(expected, reader.decode())
        assertEquals(encoded.size.toUInt(), reader.position())
    }

    private fun <T> assertBoundary(
        encoded: ByteArray,
        expected: T,
        decode: WasmBinaryReader.() -> T,
    ) {
        val reader = reader(encoded)

        assertEquals(expected, reader.decode())
        assertEquals(encoded.size.toUInt(), reader.position())
    }

    private fun <T> assertInvalid(
        encoded: ByteArray,
        bitWidth: Int,
        decode: WasmBinaryReader.() -> T,
    ) {
        val error = assertFailsWith<WasmDecodeException> {
            reader(encoded).decode()
        }

        val invalidEncoding = assertIs<ReaderDecodeError.InvalidIntegerEncoding>(error.error)
        assertEquals(bitWidth, invalidEncoding.bitWidth)
    }

    private fun <T> assertStreaming(
        encoded: ByteArray,
        expected: T,
        decode: WasmBinaryReader.() -> T,
    ) {
        val reader = streamingReader(encoded)

        assertEquals(expected, reader.decode())
        assertEquals(encoded.size.toUInt(), reader.position())
    }

    private fun <T> assertStreamingInvalid(
        encoded: ByteArray,
        bitWidth: Int,
        decode: WasmBinaryReader.() -> T,
    ) {
        val error = assertFailsWith<WasmDecodeException> {
            streamingReader(encoded).decode()
        }

        assertEquals(ReaderDecodeError.InvalidIntegerEncoding(bitWidth), error.error)
    }

    private fun reader(bytes: ByteArray): WasmBinaryReader = BinaryReader(bytes)

    private fun streamingReader(bytes: ByteArray): WasmBinaryReader =
        BinaryReader(singleByteSource(bytes))

    private fun singleByteSource(
        bytes: ByteArray,
        startPosition: Int = 0,
    ): SourceReader = object : SourceReader {
        private var position = startPosition

        override fun byte(): Byte {
            if (exhausted()) throw NoSuchElementException("No more elements")
            return bytes[position++]
        }

        override fun bytes(amount: Int): ByteArray = when {
            amount == 0 -> byteArrayOf()
            exhausted() -> byteArrayOf()
            else -> byteArrayOf(byte())
        }

        override fun exhausted(): Boolean = position >= bytes.size

        override fun peek(): SourceReader = singleByteSource(bytes, position)
    }
}
