@file:JvmName("DataIndexDecoderKt")

package io.github.charlietap.chasm.decoder.decoder.section.index

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import kotlin.jvm.JvmName

internal fun DataIndexDecoder(
    context: ModuleDecoderContext,
): Result<Index.DataIndex, WasmDecodeError> = IndexDecoder(context, Index::DataIndex)
