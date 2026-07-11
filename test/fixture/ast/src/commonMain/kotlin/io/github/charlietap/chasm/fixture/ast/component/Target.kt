package io.github.charlietap.chasm.fixture.ast.component

import io.github.charlietap.chasm.ast.component.CoreExportTarget
import io.github.charlietap.chasm.ast.component.ExportTarget
import io.github.charlietap.chasm.ast.component.Index
import io.github.charlietap.chasm.fixture.ast.module.functionIndex
import io.github.charlietap.chasm.fixture.ast.module.globalIndex
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex
import io.github.charlietap.chasm.fixture.ast.module.tableIndex
import io.github.charlietap.chasm.fixture.ast.module.tagIndex
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.ast.module.Index as ModuleIndex

fun exportTarget(): ExportTarget = moduleExportTarget()

fun moduleExportTarget(
    index: Index.ComponentModuleIndex = componentModuleIndex(),
) = ExportTarget.Module(
    index = index,
)

fun functionExportTarget(
    index: Index.ComponentFunctionIndex = componentFunctionIndex(),
) = ExportTarget.Function(
    index = index,
)

fun valueExportTarget(
    index: Index.ComponentValueIndex = componentValueIndex(),
) = ExportTarget.Value(
    index = index,
)

fun typeExportTarget(
    index: Index.ComponentTypeIndex = componentTypeIndex(),
) = ExportTarget.Type(
    index = index,
)

fun componentExportTarget(
    index: Index.ComponentIndex = componentIndex(),
) = ExportTarget.Component(
    index = index,
)

fun instanceExportTarget(
    index: Index.ComponentInstanceIndex = componentInstanceIndex(),
) = ExportTarget.Instance(
    index = index,
)

fun coreExportTarget(): CoreExportTarget = functionCoreExportTarget()

fun functionCoreExportTarget(
    index: ModuleIndex.FunctionIndex = functionIndex(),
) = CoreExportTarget.Function(
    index = index,
)

fun tableCoreExportTarget(
    index: ModuleIndex.TableIndex = tableIndex(),
) = CoreExportTarget.Table(
    index = index,
)

fun memoryCoreExportTarget(
    index: ModuleIndex.MemoryIndex = memoryIndex(),
) = CoreExportTarget.Memory(
    index = index,
)

fun globalCoreExportTarget(
    index: ModuleIndex.GlobalIndex = globalIndex(),
) = CoreExportTarget.Global(
    index = index,
)

fun tagCoreExportTarget(
    index: ModuleIndex.TagIndex = tagIndex(),
) = CoreExportTarget.Tag(
    index = index,
)

fun typeCoreExportTarget(
    index: ModuleIndex.TypeIndex = typeIndex(),
) = CoreExportTarget.Type(
    index = index,
)

fun moduleCoreExportTarget(
    index: Index.ComponentModuleIndex = componentModuleIndex(),
) = CoreExportTarget.Module(
    index = index,
)

fun instanceCoreExportTarget(
    index: Index.ComponentModuleInstanceIndex = componentModuleInstanceIndex(),
) = CoreExportTarget.Instance(
    index = index,
)
