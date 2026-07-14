package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun InstructionScope(
    context: ModuleValidationContext,
    instruction: Instruction,
    block: (ModuleValidationContext) -> Result<Unit, ModuleValidatorError>,
): Result<Unit, ModuleValidatorError> {
    context.instructionContext.instruction = instruction
    val result = block(context)
    context.instructionContext.instruction = null
    return result
}
