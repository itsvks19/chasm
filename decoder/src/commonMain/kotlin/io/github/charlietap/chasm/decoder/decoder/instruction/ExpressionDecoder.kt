package io.github.charlietap.chasm.decoder.decoder.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.context.scope.BlockScope
import io.github.charlietap.chasm.decoder.context.scope.ScopedDecoder
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun ExpressionDecoder(
    context: ModuleDecoderContext,
): Result<Expression, WasmDecodeError> =
    ExpressionDecoder(
        context,
        ::BlockScope,
        ::InstructionBlockDecoder,
    )

internal inline fun ExpressionDecoder(
    context: ModuleDecoderContext,
    crossinline scope: ScopedDecoder<UByte, List<Instruction>>,
    crossinline instructionBlockDecoder: Decoder<List<Instruction>>,
): Result<Expression, WasmDecodeError> = binding {

    val instructions = scope(context, END) { scopedContext ->
        instructionBlockDecoder(scopedContext)
    }.bind()

    Expression(instructions)
}
