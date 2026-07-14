package io.github.charlietap.chasm.validator.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.get
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.stack.peekNth
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.BottomType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.VectorType
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.matching.ValueTypeMatcher
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError

internal inline fun ModuleValidationContext.pop(): Result<ValueType, ModuleValidatorError> = binding {

    val label = labels.peekOrNull() ?: Label.DEFAULT

    if (operands.depth() == label.operandsDepth && !label.unreachable) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    if (operands.depth() == label.operandsDepth && label.unreachable) {
        ValueType.Bottom(BottomType)
    } else {
        operands
            .popOrNull()
            .toResultOr {
                TypeValidatorError.TypeMismatch
            }.bind()
    }
}

internal inline fun ModuleValidationContext.pop(
    expected: ValueType,
    crossinline typeMatcher: TypeMatcher<ValueType> = ::ValueTypeMatcher,
) = binding {
    val actual = pop().bind()
    if (!typeMatcher(actual, expected, this@pop)) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }
    actual
}

internal inline fun ModuleValidationContext.push(
    valueType: ValueType,
) = operands.push(valueType)

internal inline fun ModuleValidationContext.popReference(): Result<ReferenceType, ModuleValidatorError> {
    val t = pop().get() ?: return Err(TypeValidatorError.TypeMismatch)

    return when (t) {
        is ValueType.Reference -> Ok(t.referenceType)
        is ValueType.Bottom -> Ok(ReferenceType.Ref(AbstractHeapType.Bottom(BottomType)))
        is ValueType.Number,
        is ValueType.Vector,
        -> Err(TypeValidatorError.TypeMismatch)
    }
}

internal inline fun ModuleValidationContext.popI32(): Result<ValueType, ModuleValidatorError> = pop(ValueType.Number(NumberType.I32))

internal inline fun ModuleValidationContext.popI64(): Result<ValueType, ModuleValidatorError> = pop(ValueType.Number(NumberType.I64))

internal inline fun ModuleValidationContext.popF32(): Result<ValueType, ModuleValidatorError> = pop(ValueType.Number(NumberType.F32))

internal inline fun ModuleValidationContext.popF64(): Result<ValueType, ModuleValidatorError> = pop(ValueType.Number(NumberType.F64))

internal inline fun ModuleValidationContext.pushI32() = operands.push(ValueType.Number(NumberType.I32))

internal inline fun ModuleValidationContext.pushI64() = operands.push(ValueType.Number(NumberType.I64))

internal inline fun ModuleValidationContext.pushF32() = operands.push(ValueType.Number(NumberType.F32))

internal inline fun ModuleValidationContext.pushF64() = operands.push(ValueType.Number(NumberType.F64))

internal inline fun ModuleValidationContext.popV128(): Result<ValueType, ModuleValidatorError> = pop(ValueType.Vector(VectorType.V128))

internal inline fun ModuleValidationContext.pushV128() = operands.push(ValueType.Vector(VectorType.V128))

internal inline fun ModuleValidationContext.pushRef(
    heapType: HeapType,
) = operands.push(ValueType.Reference(ReferenceType.Ref(heapType)))

internal inline fun ModuleValidationContext.pushRefNull(
    heapType: HeapType,
) = operands.push(ValueType.Reference(ReferenceType.RefNull(heapType)))

internal inline fun ModuleValidationContext.popRef(): Result<ReferenceType.Ref, ModuleValidatorError> = popReference().flatMap { reference ->
    if (reference is ReferenceType.Ref) {
        Ok(reference)
    } else {
        Err(TypeValidatorError.TypeMismatch)
    }
}

internal inline fun ModuleValidationContext.popRefNull(): Result<ReferenceType.RefNull, ModuleValidatorError> = popReference().flatMap { reference ->
    if (reference is ReferenceType.RefNull) {
        Ok(reference)
    } else {
        Err(TypeValidatorError.TypeMismatch)
    }
}

internal inline fun ModuleValidationContext.popMemoryAddress(
    index: Index.MemoryIndex,
): Result<ValueType, ModuleValidatorError> = binding {
    val memory = memoryType(index).bind()
    when (memory.addressType) {
        AddressType.I32 -> popI32().bind()
        AddressType.I64 -> popI64().bind()
    }
}

internal inline fun ModuleValidationContext.pushMemoryAddress(
    index: Index.MemoryIndex,
): Result<Unit, ModuleValidatorError> = binding {
    val memory = memoryType(index).bind()
    when (memory.addressType) {
        AddressType.I32 -> pushI32()
        AddressType.I64 -> pushI64()
    }
}

internal inline fun ModuleValidationContext.popTableAddress(
    index: Index.TableIndex,
): Result<ValueType, ModuleValidatorError> = binding {
    val memory = tableType(index).bind()
    when (memory.addressType) {
        AddressType.I32 -> popI32().bind()
        AddressType.I64 -> popI64().bind()
    }
}

internal inline fun ModuleValidationContext.pushTableAddress(
    index: Index.TableIndex,
): Result<Unit, ModuleValidatorError> = binding {
    val memory = tableType(index).bind()
    when (memory.addressType) {
        AddressType.I32 -> pushI32()
        AddressType.I64 -> pushI64()
    }
}

internal inline fun ModuleValidationContext.peekValues(
    expected: List<ValueType>,
    crossinline typeMatcher: TypeMatcher<ValueType> = ::ValueTypeMatcher,
): Result<List<ValueType>, ModuleValidatorError> = binding {
    expected.asReversed().mapIndexed { index, valueType ->
        val actual = operands
            .peekNth(index)
            .mapError {
                TypeValidatorError.TypeMismatch
            }.bind()

        if (!typeMatcher(actual, valueType, this@peekValues)) {
            Err(TypeValidatorError.TypeMismatch).bind<Unit>()
        }
        actual
    }
}

internal inline fun ModuleValidationContext.popValues(
    expected: List<ValueType>,
): Result<List<ValueType>, ModuleValidatorError> = binding {
    expected.asReversed().map { valueType ->
        pop(valueType).bind()
    }
}

internal inline fun ModuleValidationContext.pushValues(
    values: List<ValueType>,
) = values.forEach { valueType ->
    operands.push(valueType)
}

internal inline fun ModuleValidationContext.popAndReplaceValues(
    expected: List<ValueType>,
): Result<Unit, ModuleValidatorError> = binding {
    expected.asReversed().forEach { valueType ->
        pop(valueType).bind()
    }
    expected.forEach { valueType ->
        operands.push(valueType)
    }
}

internal inline fun ModuleValidationContext.unreachable() = binding {
    val label = labels.peek().bind()
    while (operands.depth() > label.operandsDepth) {
        pop().bind()
    }
    label.unreachable = true
}
