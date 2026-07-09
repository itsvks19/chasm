package io.github.charlietap.chasm.ast.component

import io.github.charlietap.chasm.ast.component.Index.ComponentTypeIndex
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.ast.module.Index.FunctionIndex as ModuleFunctionIndex

sealed interface ValueType {

    data object Bool : ValueType

    data object S8 : ValueType

    data object U8 : ValueType

    data object S16 : ValueType

    data object U16 : ValueType

    data object S32 : ValueType

    data object U32 : ValueType

    data object S64 : ValueType

    data object U64 : ValueType

    data object F32 : ValueType

    data object F64 : ValueType

    data object Char : ValueType

    data object String : ValueType

    data object ErrorContext : ValueType

    data class TypeIndex(val index: ComponentTypeIndex) : ValueType

    data class Record(val fields: kotlin.collections.List<LabeledValueType>) : ValueType

    data class Variant(val cases: kotlin.collections.List<VariantCase>) : ValueType

    data class List(val element: ValueType) : ValueType

    data class FixedLengthList(val element: ValueType, val length: UInt) : ValueType

    data class Map(val key: KeyType, val value: ValueType) : ValueType

    data class Tuple(val elements: kotlin.collections.List<ValueType>) : ValueType

    data class Flags(val labels: kotlin.collections.List<NameValue>) : ValueType

    data class Enum(val labels: kotlin.collections.List<NameValue>) : ValueType

    data class Option(val value: ValueType) : ValueType

    data class Result(val ok: ValueType?, val error: ValueType?) : ValueType

    data class Own(val resource: ComponentTypeIndex) : ValueType

    data class Borrow(val resource: ComponentTypeIndex) : ValueType

    data class Stream(val element: ValueType?) : ValueType

    data class Future(val value: ValueType?) : ValueType
}

data class LabeledValueType(
    val label: NameValue,
    val type: ValueType,
)

data class VariantCase(
    val label: NameValue,
    val type: ValueType?,
)

sealed interface KeyType {

    data object Bool : KeyType

    data object S8 : KeyType

    data object U8 : KeyType

    data object S16 : KeyType

    data object U16 : KeyType

    data object S32 : KeyType

    data object U32 : KeyType

    data object S64 : KeyType

    data object U64 : KeyType

    data object Char : KeyType

    data object String : KeyType
}

sealed interface TypeDefinition {

    data class Value(val type: ValueType) : TypeDefinition

    data class Function(val type: FunctionType) : TypeDefinition

    data class Component(val declarations: List<ComponentDeclaration>) : TypeDefinition

    data class Instance(val declarations: List<InstanceDeclaration>) : TypeDefinition

    data class Resource(
        val representation: ValueType,
        val destructor: ModuleFunctionIndex?,
    ) : TypeDefinition
}

data class FunctionType(
    val params: List<LabeledValueType>,
    val result: ValueType?,
    val async: Boolean = false,
)

// Decode keeps the grammar broad; validation rejects private resource types in
// component and instance type declarations.
sealed interface ComponentDeclaration {

    data class CoreType(val type: CoreTypeDefinition) : ComponentDeclaration

    data class Type(val type: TypeDefinition) : ComponentDeclaration

    data class Alias(val alias: AliasDefinition) : ComponentDeclaration

    data class Export(
        val name: NameAttributes,
        val type: ExternalType,
    ) : ComponentDeclaration

    data class Import(
        val name: NameAttributes,
        val type: ExternalType,
    ) : ComponentDeclaration
}

sealed interface InstanceDeclaration {

    data class CoreType(val type: CoreTypeDefinition) : InstanceDeclaration

    data class Type(val type: TypeDefinition) : InstanceDeclaration

    data class Alias(val alias: AliasDefinition) : InstanceDeclaration

    data class Export(
        val name: NameAttributes,
        val type: ExternalType,
    ) : InstanceDeclaration
}
