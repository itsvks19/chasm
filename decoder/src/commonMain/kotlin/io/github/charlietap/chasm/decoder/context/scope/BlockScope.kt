package io.github.charlietap.chasm.decoder.context.scope

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.context.BlockContextImpl
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun BlockScope(
    context: ModuleDecoderContext,
    blockEndOpcode: UByte,
): Result<ModuleDecoderContext, WasmDecodeError> = context
    .copy(
        blockContext = BlockContextImpl(
            blockEndOpcode = blockEndOpcode,
        ),
    ).let(::Ok)
