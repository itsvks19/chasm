package io.github.charlietap.chasm.decoder.context.scope

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal inline fun <T> BlockScope(
    context: ModuleDecoderContext,
    blockEndOpcode: UByte,
    crossinline decoder: Decoder<T>,
): Result<T, WasmDecodeError> {
    val previousBlockEndOpcode = context.blockEndOpcode
    context.blockEndOpcode = blockEndOpcode

    return try {
        decoder(context)
    } finally {
        context.blockEndOpcode = previousBlockEndOpcode
    }
}
