package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.validator.CoreTypeValidator
import io.github.charlietap.chasm.validator.context.CoreTypeValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun RecursiveTypeValidator(
    context: CoreTypeValidationContext,
    type: RecursiveType,
): Result<Unit, ModuleValidatorError> =
    RecursiveTypeValidator(
        context = context,
        type = type,
        subTypeValidator = ::SubTypeValidator,
    )

internal inline fun RecursiveTypeValidator(
    context: CoreTypeValidationContext,
    type: RecursiveType,
    crossinline subTypeValidator: CoreTypeValidator<SubType>,
): Result<Unit, ModuleValidatorError> = binding {
    type.subTypes.forEach { subType ->
        subTypeValidator(context, subType).bind()
    }
}
