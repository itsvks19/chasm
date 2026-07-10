package io.github.charlietap.chasm.decoder.decoder

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal typealias Decoder<T> = (ModuleDecoderContext) -> Result<T, WasmDecodeError>
