package io.github.charlietap.chasm.fixture.ast.component

import io.github.charlietap.chasm.ast.component.ExternalType
import io.github.charlietap.chasm.ast.component.Index
import io.github.charlietap.chasm.ast.component.TypeBound
import io.github.charlietap.chasm.ast.component.ValueBound
import io.github.charlietap.chasm.ast.component.ValueType
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.ast.module.Index as ModuleIndex

fun externalType(): ExternalType = coreModuleExternalType()

fun coreModuleExternalType(
    typeIndex: ModuleIndex.TypeIndex = typeIndex(),
) = ExternalType.CoreModule(
    typeIndex = typeIndex,
)

fun functionExternalType(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = ExternalType.Function(
    typeIndex = typeIndex,
)

fun valueExternalType(
    bound: ValueBound = valueBound(),
) = ExternalType.Value(
    bound = bound,
)

fun typeExternalType(
    bound: TypeBound = typeBound(),
) = ExternalType.Type(
    bound = bound,
)

fun componentExternalType(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = ExternalType.Component(
    typeIndex = typeIndex,
)

fun instanceExternalType(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = ExternalType.Instance(
    typeIndex = typeIndex,
)

fun valueBound(): ValueBound = equalsValueBound()

fun equalsValueBound(
    valueIndex: Index.ComponentValueIndex = componentValueIndex(),
) = ValueBound.Equals(
    valueIndex = valueIndex,
)

fun typeValueBound(
    type: ValueType = componentValueType(),
) = ValueBound.Type(
    type = type,
)

fun typeBound(): TypeBound = equalsTypeBound()

fun equalsTypeBound(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = TypeBound.Equals(
    typeIndex = typeIndex,
)

fun subResourceTypeBound() = TypeBound.SubResource
