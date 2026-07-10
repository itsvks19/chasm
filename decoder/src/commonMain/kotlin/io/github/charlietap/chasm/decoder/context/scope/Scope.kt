package io.github.charlietap.chasm.decoder.context.scope

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal typealias Scope<T> = (ModuleDecoderContext, T) -> Result<ModuleDecoderContext, WasmDecodeError>
