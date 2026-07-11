package io.github.charlietap.chasm.fixture.ast.component

import io.github.charlietap.chasm.ast.component.Alias
import io.github.charlietap.chasm.ast.component.AliasDefinition
import io.github.charlietap.chasm.ast.component.Canon
import io.github.charlietap.chasm.ast.component.CanonicalDefinition
import io.github.charlietap.chasm.ast.component.Component
import io.github.charlietap.chasm.ast.component.ComponentValue
import io.github.charlietap.chasm.ast.component.CoreInstance
import io.github.charlietap.chasm.ast.component.CoreInstanceDefinition
import io.github.charlietap.chasm.ast.component.CoreModule
import io.github.charlietap.chasm.ast.component.CoreType
import io.github.charlietap.chasm.ast.component.CoreTypeDefinition
import io.github.charlietap.chasm.ast.component.Custom
import io.github.charlietap.chasm.ast.component.Definition
import io.github.charlietap.chasm.ast.component.Export
import io.github.charlietap.chasm.ast.component.ExportTarget
import io.github.charlietap.chasm.ast.component.ExternalType
import io.github.charlietap.chasm.ast.component.Import
import io.github.charlietap.chasm.ast.component.Instance
import io.github.charlietap.chasm.ast.component.InstanceDefinition
import io.github.charlietap.chasm.ast.component.NameAttributes
import io.github.charlietap.chasm.ast.component.NestedComponent
import io.github.charlietap.chasm.ast.component.Start
import io.github.charlietap.chasm.ast.component.StartDefinition
import io.github.charlietap.chasm.ast.component.Type
import io.github.charlietap.chasm.ast.component.TypeDefinition
import io.github.charlietap.chasm.ast.component.Value
import io.github.charlietap.chasm.ast.component.Version
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.fixture.ast.module.module

fun component(
    version: Version = componentVersion(),
    definitions: List<Definition> = emptyList(),
    customs: List<Custom> = emptyList(),
) = Component(
    version = version,
    definitions = definitions,
    customs = customs,
)

fun componentDefinition(): Definition = coreModuleComponentDefinition()

fun coreModuleComponentDefinition(
    module: Module = module(),
) = CoreModule(
    module = module,
)

fun coreInstanceComponentDefinition(
    instance: CoreInstanceDefinition = coreInstanceDefinition(),
) = CoreInstance(
    instance = instance,
)

fun coreTypeComponentDefinition(
    type: CoreTypeDefinition = coreTypeDefinition(),
) = CoreType(
    type = type,
)

fun nestedComponentComponentDefinition(
    component: Component = component(),
) = NestedComponent(
    component = component,
)

fun instanceComponentDefinition(
    instance: InstanceDefinition = instanceDefinition(),
) = Instance(
    instance = instance,
)

fun aliasComponentDefinition(
    alias: AliasDefinition = aliasDefinition(),
) = Alias(
    alias = alias,
)

fun typeComponentDefinition(
    type: TypeDefinition = typeDefinition(),
) = Type(
    type = type,
)

fun canonComponentDefinition(
    canon: CanonicalDefinition = canonicalDefinition(),
) = Canon(
    canon = canon,
)

fun startComponentDefinition(
    start: StartDefinition = startDefinition(),
) = Start(
    start = start,
)

fun importComponentDefinition(
    name: NameAttributes = nameAttributes(),
    type: ExternalType = externalType(),
) = Import(
    name = name,
    type = type,
)

fun exportComponentDefinition(
    name: NameAttributes = nameAttributes(),
    target: ExportTarget = exportTarget(),
    type: ExternalType? = null,
) = Export(
    name = name,
    target = target,
    type = type,
)

fun valueComponentDefinition(
    value: ComponentValue = componentValue(),
) = Value(
    value = value,
)
