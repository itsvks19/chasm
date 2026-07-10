package io.github.charlietap.chasm.decoder.context.scope

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.context.NameSectionContextImpl
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun NameScope(
    context: ModuleDecoderContext,
    size: UInt,
): Result<ModuleDecoderContext, WasmDecodeError> = binding {
    context.copy(
        nameSectionContext = NameSectionContextImpl(
            sectionSize = size,
        ),
    )
}
