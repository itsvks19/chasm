package io.github.charlietap.chasm.decoder.reader

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeException
import kotlin.test.fail

internal fun FakeWasmBinaryReader(
    fakeByteReader: (() -> Result<Byte, WasmDecodeError>)? = null,
    fakeUByteReader: (() -> Result<UByte, WasmDecodeError>)? = null,
    fakeBytesReader: ((Int) -> Result<ByteArray, WasmDecodeError>)? = null,
    fakeUBytesReader: ((UInt) -> Result<UByteArray, WasmDecodeError>)? = null,
    fakeIntReader: (() -> Result<Int, WasmDecodeError>)? = null,
    fakeUIntReader: (() -> Result<UInt, WasmDecodeError>)? = null,
    fakeShortReader: (() -> Result<Short, WasmDecodeError>)? = null,
    fakeUShortReader: (() -> Result<UShort, WasmDecodeError>)? = null,
    fakeS33Reader: (() -> Result<Long, WasmDecodeError>)? = null,
    fakeLongReader: (() -> Result<Long, WasmDecodeError>)? = null,
    fakeULongReader: (() -> Result<ULong, WasmDecodeError>)? = null,
    fakeFloatReader: (() -> Result<Float, WasmDecodeError>)? = null,
    fakeDoubleReader: (() -> Result<Double, WasmDecodeError>)? = null,
    fakeExhaustedReader: (() -> Result<Boolean, WasmDecodeError>)? = null,
    fakePositionReader: (() -> UInt)? = null,
    fakePeekUByteReader: (() -> Result<UByte, WasmDecodeError>)? = null,
    fakePeekUIntReader: (() -> Result<UInt, WasmDecodeError>)? = null,
    fakePeekReader: (() -> WasmBinaryReader)? = null,
    message: String? = null,
): WasmBinaryReader = object : WasmBinaryReader {

    val fail: () -> Nothing = {
        fail(message ?: "binary reader should never be called in this scenario")
    }

    override fun byte(): Byte = (fakeByteReader ?: fail)().orThrow()

    override fun ubyte(): UByte = (fakeUByteReader ?: fail)().orThrow()

    override fun bytes(amount: Int): ByteArray = fakeBytesReader?.let {
        fakeBytesReader(amount)
    }?.orThrow() ?: fail()

    override fun ubytes(amount: UInt): UByteArray = fakeUBytesReader?.let {
        fakeUBytesReader(amount)
    }?.orThrow() ?: fail()

    override fun int(): Int = (fakeIntReader ?: fail)().orThrow()

    override fun uint(): UInt = (fakeUIntReader ?: fail)().orThrow()

    override fun short(): Short = (fakeShortReader ?: fail)().orThrow()

    override fun ushort(): UShort = (fakeUShortReader ?: fail)().orThrow()

    override fun s33(): Long = (fakeS33Reader ?: fail)().orThrow()

    override fun long(): Long = (fakeLongReader ?: fail)().orThrow()

    override fun ulong(): ULong = (fakeULongReader ?: fail)().orThrow()

    override fun float(): Float = (fakeFloatReader ?: fail)().orThrow()

    override fun double(): Double = (fakeDoubleReader ?: fail)().orThrow()

    override fun exhausted(): Boolean = (fakeExhaustedReader ?: fail)().orThrow()

    override fun position(): UInt = fakePositionReader?.invoke() ?: fail()

    override fun peekUByte(): UByte = when {
        fakePeekUByteReader != null -> fakePeekUByteReader().orThrow()
        fakePeekReader != null -> fakePeekReader().ubyte()
        else -> fail()
    }

    override fun peekUInt(): UInt = when {
        fakePeekUIntReader != null -> fakePeekUIntReader().orThrow()
        fakePeekReader != null -> fakePeekReader().uint()
        else -> fail()
    }
}

private fun <T> Result<T, WasmDecodeError>.orThrow(): T = fold(
    success = { value -> value },
    failure = { error -> throw WasmDecodeException(error) },
)

internal fun FakeByteReader(
    byte: () -> Result<Byte, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeByteReader = byte)

internal fun FakeUByteReader(
    byte: () -> Result<UByte, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeUByteReader = byte)

internal fun FakeIntReader(
    int: () -> Result<Int, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeIntReader = int)

internal fun FakeUIntReader(
    uint: () -> Result<UInt, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeUIntReader = uint)

internal fun FakeShortReader(
    short: () -> Result<Short, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeShortReader = short)

internal fun FakeUShortReader(
    ushort: () -> Result<UShort, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeUShortReader = ushort)

internal fun FakeS33Reader(
    s33: () -> Result<Long, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeS33Reader = s33)

internal fun FakeLongReader(
    long: () -> Result<Long, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeLongReader = long)

internal fun FakeULongReader(
    ulong: () -> Result<ULong, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeULongReader = ulong)

internal fun FakeFloatReader(
    float: () -> Result<Float, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeFloatReader = float)

internal fun FakeDoubleReader(
    double: () -> Result<Double, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeDoubleReader = double)

internal fun FakeUBytesReader(
    bytes: (UInt) -> Result<UByteArray, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeUBytesReader = bytes)

internal fun FakeExhaustedReader(
    exhausted: () -> Result<Boolean, WasmDecodeError>,
): WasmBinaryReader = FakeWasmBinaryReader(fakeExhaustedReader = exhausted)

internal fun FakePositionReader(
    position: () -> UInt,
): WasmBinaryReader = FakeWasmBinaryReader(fakePositionReader = position)
