package io.github.charlietap.chasm.decoder.reader

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeException

internal fun IOErrorWasmFileReader(err: Result<Nothing, WasmDecodeError.IOError>): WasmBinaryReader = object : WasmBinaryReader {

    override fun byte(): Byte = throwError()

    override fun ubyte(): UByte = throwError()

    override fun bytes(amount: Int): ByteArray = throwError()

    override fun ubytes(amount: UInt): UByteArray = throwError()

    override fun int(): Int = throwError()

    override fun uint(): UInt = throwError()

    override fun short(): Short = throwError()

    override fun ushort(): UShort = throwError()

    override fun s33(): Long = throwError()

    override fun long(): Long = throwError()

    override fun ulong(): ULong = throwError()

    override fun float(): Float = throwError()

    override fun double(): Double = throwError()

    override fun exhausted(): Boolean = throwError()

    override fun position(): UInt = 0u

    override fun peekUByte(): UByte = throwError()

    override fun peekUInt(): UInt = throwError()

    private fun throwError(): Nothing = err.fold(
        success = { value -> value },
        failure = { error -> throw WasmDecodeException(error) },
    )
}
