package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.CoreTypeValidator
import io.github.charlietap.chasm.validator.context.CoreTypeValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ValueTypeValidator(
    context: CoreTypeValidationContext,
    type: ValueType,
): Result<Unit, ModuleValidatorError> =
    ValueTypeValidator(
        context = context,
        type = type,
        referenceTypeValidator = ::ReferenceTypeValidator,
    )

internal inline fun ValueTypeValidator(
    context: CoreTypeValidationContext,
    type: ValueType,
    crossinline referenceTypeValidator: CoreTypeValidator<ReferenceType>,
): Result<Unit, ModuleValidatorError> = binding {
    when (type) {
        is ValueType.Bottom,
        is ValueType.Number,
        is ValueType.Vector,
        -> Unit
        is ValueType.Reference -> referenceTypeValidator(context, type.referenceType).bind()
    }
}
