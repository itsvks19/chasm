package io.github.charlietap.chasm.decoder.decoder.section.index

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun TagIndexDecoder(
    context: ModuleDecoderContext,
): Result<Index.TagIndex, WasmDecodeError> = IndexDecoder(context, Index::TagIndex)
