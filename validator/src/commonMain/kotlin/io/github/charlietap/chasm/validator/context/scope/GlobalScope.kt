package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun GlobalScope(
    context: ModuleValidationContext,
    global: Global,
    block: (ModuleValidationContext) -> Result<Unit, ModuleValidatorError>,
): Result<Unit, ModuleValidatorError> {
    val previousGlobals = context.globals.toList()
    val previousResultType = context.expressionResultType
    context.globals.clear()
    context.globals.addAll(previousGlobals.take(global.idx.idx.toInt()))
    context.expressionResultType = ResultType(listOf(global.type.valueType))

    val result = block(context)
    context.globals.clear()
    context.globals.addAll(previousGlobals)
    context.expressionResultType = previousResultType
    return result
}
