package io.github.charlietap.chasm.fixture.ast.component

import io.github.charlietap.chasm.ast.component.AliasDefinition
import io.github.charlietap.chasm.ast.component.ComponentDeclaration
import io.github.charlietap.chasm.ast.component.CoreTypeDefinition
import io.github.charlietap.chasm.ast.component.ExternalType
import io.github.charlietap.chasm.ast.component.FunctionType
import io.github.charlietap.chasm.ast.component.Index
import io.github.charlietap.chasm.ast.component.InstanceDeclaration
import io.github.charlietap.chasm.ast.component.KeyType
import io.github.charlietap.chasm.ast.component.LabeledValueType
import io.github.charlietap.chasm.ast.component.NameAttributes
import io.github.charlietap.chasm.ast.component.TypeDefinition
import io.github.charlietap.chasm.ast.component.ValueType
import io.github.charlietap.chasm.ast.component.VariantCase
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.ast.module.Index as ModuleIndex

fun componentValueType(): ValueType = boolComponentValueType()

fun boolComponentValueType() = ValueType.Bool

fun s8ComponentValueType() = ValueType.S8

fun u8ComponentValueType() = ValueType.U8

fun s16ComponentValueType() = ValueType.S16

fun u16ComponentValueType() = ValueType.U16

fun s32ComponentValueType() = ValueType.S32

fun u32ComponentValueType() = ValueType.U32

fun s64ComponentValueType() = ValueType.S64

fun u64ComponentValueType() = ValueType.U64

fun f32ComponentValueType() = ValueType.F32

fun f64ComponentValueType() = ValueType.F64

fun charComponentValueType() = ValueType.Char

fun stringComponentValueType() = ValueType.String

fun errorContextComponentValueType() = ValueType.ErrorContext

fun typeIndexComponentValueType(
    index: Index.ComponentTypeIndex = componentTypeIndex(),
) = ValueType.TypeIndex(
    index = index,
)

fun recordComponentValueType(
    fields: List<LabeledValueType> = [],
) = ValueType.Record(
    fields = fields,
)

fun variantComponentValueType(
    cases: List<VariantCase> = [],
) = ValueType.Variant(
    cases = cases,
)

fun listComponentValueType(
    element: ValueType = componentValueType(),
) = ValueType.List(
    element = element,
)

fun fixedLengthListComponentValueType(
    element: ValueType = componentValueType(),
    length: UInt = 0u,
) = ValueType.FixedLengthList(
    element = element,
    length = length,
)

fun mapComponentValueType(
    key: KeyType = componentKeyType(),
    value: ValueType = componentValueType(),
) = ValueType.Map(
    key = key,
    value = value,
)

fun tupleComponentValueType(
    elements: List<ValueType> = [],
) = ValueType.Tuple(
    elements = elements,
)

fun flagsComponentValueType(
    labels: List<NameValue> = [],
) = ValueType.Flags(
    labels = labels,
)

fun enumComponentValueType(
    labels: List<NameValue> = [],
) = ValueType.Enum(
    labels = labels,
)

fun optionComponentValueType(
    value: ValueType = componentValueType(),
) = ValueType.Option(
    value = value,
)

fun resultComponentValueType(
    ok: ValueType? = null,
    error: ValueType? = null,
) = ValueType.Result(
    ok = ok,
    error = error,
)

fun ownComponentValueType(
    resource: Index.ComponentTypeIndex = componentTypeIndex(),
) = ValueType.Own(
    resource = resource,
)

fun borrowComponentValueType(
    resource: Index.ComponentTypeIndex = componentTypeIndex(),
) = ValueType.Borrow(
    resource = resource,
)

fun streamComponentValueType(
    element: ValueType? = null,
) = ValueType.Stream(
    element = element,
)

fun futureComponentValueType(
    value: ValueType? = null,
) = ValueType.Future(
    value = value,
)

fun labeledComponentValueType(
    label: NameValue = nameValue(),
    type: ValueType = componentValueType(),
) = LabeledValueType(
    label = label,
    type = type,
)

fun componentVariantCase(
    label: NameValue = nameValue(),
    type: ValueType? = null,
) = VariantCase(
    label = label,
    type = type,
)

fun componentKeyType(): KeyType = boolComponentKeyType()

fun boolComponentKeyType() = KeyType.Bool

fun s8ComponentKeyType() = KeyType.S8

fun u8ComponentKeyType() = KeyType.U8

fun s16ComponentKeyType() = KeyType.S16

fun u16ComponentKeyType() = KeyType.U16

fun s32ComponentKeyType() = KeyType.S32

fun u32ComponentKeyType() = KeyType.U32

fun s64ComponentKeyType() = KeyType.S64

fun u64ComponentKeyType() = KeyType.U64

fun charComponentKeyType() = KeyType.Char

fun stringComponentKeyType() = KeyType.String

fun typeDefinition(): TypeDefinition = valueTypeDefinition()

fun valueTypeDefinition(
    type: ValueType = componentValueType(),
) = TypeDefinition.Value(
    type = type,
)

fun functionTypeDefinition(
    type: FunctionType = componentFunctionType(),
) = TypeDefinition.Function(
    type = type,
)

fun componentTypeDefinition(
    declarations: List<ComponentDeclaration> = [],
) = TypeDefinition.Component(
    declarations = declarations,
)

fun instanceTypeDefinition(
    declarations: List<InstanceDeclaration> = [],
) = TypeDefinition.Instance(
    declarations = declarations,
)

fun resourceTypeDefinition(
    representation: ValueType = componentValueType(),
    destructor: ModuleIndex.FunctionIndex? = null,
) = TypeDefinition.Resource(
    representation = representation,
    destructor = destructor,
)

fun componentFunctionType(
    params: List<LabeledValueType> = [],
    result: ValueType? = null,
    async: Boolean = false,
) = FunctionType(
    params = params,
    result = result,
    async = async,
)

fun componentDeclaration(): ComponentDeclaration = coreTypeComponentDeclaration()

fun coreTypeComponentDeclaration(
    type: CoreTypeDefinition = coreTypeDefinition(),
) = ComponentDeclaration.CoreType(
    type = type,
)

fun typeComponentDeclaration(
    type: TypeDefinition = typeDefinition(),
) = ComponentDeclaration.Type(
    type = type,
)

fun aliasComponentDeclaration(
    alias: AliasDefinition = aliasDefinition(),
) = ComponentDeclaration.Alias(
    alias = alias,
)

fun exportComponentDeclaration(
    name: NameAttributes = nameAttributes(),
    type: ExternalType = externalType(),
) = ComponentDeclaration.Export(
    name = name,
    type = type,
)

fun importComponentDeclaration(
    name: NameAttributes = nameAttributes(),
    type: ExternalType = externalType(),
) = ComponentDeclaration.Import(
    name = name,
    type = type,
)

fun instanceDeclaration(): InstanceDeclaration = coreTypeInstanceDeclaration()

fun coreTypeInstanceDeclaration(
    type: CoreTypeDefinition = coreTypeDefinition(),
) = InstanceDeclaration.CoreType(
    type = type,
)

fun typeInstanceDeclaration(
    type: TypeDefinition = typeDefinition(),
) = InstanceDeclaration.Type(
    type = type,
)

fun aliasInstanceDeclaration(
    alias: AliasDefinition = aliasDefinition(),
) = InstanceDeclaration.Alias(
    alias = alias,
)

fun exportInstanceDeclaration(
    name: NameAttributes = nameAttributes(),
    type: ExternalType = externalType(),
) = InstanceDeclaration.Export(
    name = name,
    type = type,
)
