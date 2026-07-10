package io.github.charlietap.chasm.decoder.context.scope

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.context.VectorContextImpl
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun VectorScope(
    context: ModuleDecoderContext,
    index: Int,
): Result<ModuleDecoderContext, WasmDecodeError> = binding {
    context.copy(
        vectorContext = VectorContextImpl(
            index = index,
        ),
    )
}
