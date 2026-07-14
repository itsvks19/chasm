package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun TableScope(
    context: ModuleValidationContext,
    table: Table,
    block: (ModuleValidationContext) -> Result<Unit, ModuleValidatorError>,
): Result<Unit, ModuleValidatorError> {
    val previousGlobals = context.globals.toList()
    val previousResultType = context.expressionResultType
    context.globals.clear()
    context.globals.addAll(previousGlobals.take(context.importedGlobalCount))
    context.expressionResultType = ResultType(listOf(ValueType.Reference(table.type.referenceType)))

    val result = block(context)
    context.globals.clear()
    context.globals.addAll(previousGlobals)
    context.expressionResultType = previousResultType
    return result
}
