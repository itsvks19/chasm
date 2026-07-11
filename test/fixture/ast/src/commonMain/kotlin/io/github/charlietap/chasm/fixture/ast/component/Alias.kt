package io.github.charlietap.chasm.fixture.ast.component

import io.github.charlietap.chasm.ast.component.AliasDefinition
import io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget
import io.github.charlietap.chasm.ast.component.Index
import io.github.charlietap.chasm.ast.component.InstanceExportAliasTarget
import io.github.charlietap.chasm.ast.component.OuterAliasTarget
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.ast.module.Index as ModuleIndex

fun aliasDefinition(): AliasDefinition = instanceExportAliasDefinition()

fun instanceExportAliasDefinition(
    target: InstanceExportAliasTarget = instanceExportAliasTarget(),
) = AliasDefinition.InstanceExport(
    target = target,
)

fun coreInstanceExportAliasDefinition(
    target: CoreInstanceExportAliasTarget = coreInstanceExportAliasTarget(),
) = AliasDefinition.CoreInstanceExport(
    target = target,
)

fun outerAliasDefinition(
    target: OuterAliasTarget = outerAliasTarget(),
) = AliasDefinition.Outer(
    target = target,
)

fun instanceExportAliasTarget(): InstanceExportAliasTarget = moduleInstanceExportAliasTarget()

fun moduleInstanceExportAliasTarget(
    instance: Index.ComponentInstanceIndex = componentInstanceIndex(),
    name: NameValue = nameValue(),
) = InstanceExportAliasTarget.Module(
    instance = instance,
    name = name,
)

fun functionInstanceExportAliasTarget(
    instance: Index.ComponentInstanceIndex = componentInstanceIndex(),
    name: NameValue = nameValue(),
) = InstanceExportAliasTarget.Function(
    instance = instance,
    name = name,
)

fun valueInstanceExportAliasTarget(
    instance: Index.ComponentInstanceIndex = componentInstanceIndex(),
    name: NameValue = nameValue(),
) = InstanceExportAliasTarget.Value(
    instance = instance,
    name = name,
)

fun typeInstanceExportAliasTarget(
    instance: Index.ComponentInstanceIndex = componentInstanceIndex(),
    name: NameValue = nameValue(),
) = InstanceExportAliasTarget.Type(
    instance = instance,
    name = name,
)

fun componentInstanceExportAliasTarget(
    instance: Index.ComponentInstanceIndex = componentInstanceIndex(),
    name: NameValue = nameValue(),
) = InstanceExportAliasTarget.Component(
    instance = instance,
    name = name,
)

fun instanceInstanceExportAliasTarget(
    instance: Index.ComponentInstanceIndex = componentInstanceIndex(),
    name: NameValue = nameValue(),
) = InstanceExportAliasTarget.Instance(
    instance = instance,
    name = name,
)

fun coreInstanceExportAliasTarget(): CoreInstanceExportAliasTarget = functionCoreInstanceExportAliasTarget()

fun functionCoreInstanceExportAliasTarget(
    instance: Index.ComponentModuleInstanceIndex = componentModuleInstanceIndex(),
    name: NameValue = nameValue(),
) = CoreInstanceExportAliasTarget.Function(
    instance = instance,
    name = name,
)

fun tableCoreInstanceExportAliasTarget(
    instance: Index.ComponentModuleInstanceIndex = componentModuleInstanceIndex(),
    name: NameValue = nameValue(),
) = CoreInstanceExportAliasTarget.Table(
    instance = instance,
    name = name,
)

fun memoryCoreInstanceExportAliasTarget(
    instance: Index.ComponentModuleInstanceIndex = componentModuleInstanceIndex(),
    name: NameValue = nameValue(),
) = CoreInstanceExportAliasTarget.Memory(
    instance = instance,
    name = name,
)

fun globalCoreInstanceExportAliasTarget(
    instance: Index.ComponentModuleInstanceIndex = componentModuleInstanceIndex(),
    name: NameValue = nameValue(),
) = CoreInstanceExportAliasTarget.Global(
    instance = instance,
    name = name,
)

fun tagCoreInstanceExportAliasTarget(
    instance: Index.ComponentModuleInstanceIndex = componentModuleInstanceIndex(),
    name: NameValue = nameValue(),
) = CoreInstanceExportAliasTarget.Tag(
    instance = instance,
    name = name,
)

fun typeCoreInstanceExportAliasTarget(
    instance: Index.ComponentModuleInstanceIndex = componentModuleInstanceIndex(),
    name: NameValue = nameValue(),
) = CoreInstanceExportAliasTarget.Type(
    instance = instance,
    name = name,
)

fun moduleCoreInstanceExportAliasTarget(
    instance: Index.ComponentModuleInstanceIndex = componentModuleInstanceIndex(),
    name: NameValue = nameValue(),
) = CoreInstanceExportAliasTarget.Module(
    instance = instance,
    name = name,
)

fun instanceCoreInstanceExportAliasTarget(
    instance: Index.ComponentModuleInstanceIndex = componentModuleInstanceIndex(),
    name: NameValue = nameValue(),
) = CoreInstanceExportAliasTarget.Instance(
    instance = instance,
    name = name,
)

fun outerAliasTarget(): OuterAliasTarget = moduleOuterAliasTarget()

fun moduleOuterAliasTarget(
    count: UInt = 0u,
    index: Index.ComponentModuleIndex = componentModuleIndex(),
) = OuterAliasTarget.Module(
    count = count,
    index = index,
)

fun coreTypeOuterAliasTarget(
    count: UInt = 0u,
    index: ModuleIndex.TypeIndex = typeIndex(),
) = OuterAliasTarget.CoreType(
    count = count,
    index = index,
)

fun typeOuterAliasTarget(
    count: UInt = 0u,
    index: Index.ComponentTypeIndex = componentTypeIndex(),
) = OuterAliasTarget.Type(
    count = count,
    index = index,
)

fun componentOuterAliasTarget(
    count: UInt = 0u,
    index: Index.ComponentIndex = componentIndex(),
) = OuterAliasTarget.Component(
    count = count,
    index = index,
)
