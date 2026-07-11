package io.github.charlietap.chasm.fixture.ast.component

import io.github.charlietap.chasm.ast.component.CoreExternalType
import io.github.charlietap.chasm.ast.component.CoreModuleDeclaration
import io.github.charlietap.chasm.ast.component.CoreTypeDefinition
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.fixture.type.attribute
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.ast.module.Index as ModuleIndex

fun coreTypeDefinition(): CoreTypeDefinition = definedTypeCoreTypeDefinition()

fun definedTypeCoreTypeDefinition(
    type: RecursiveType = recursiveType(),
) = CoreTypeDefinition.DefinedType(
    type = type,
)

fun moduleTypeCoreTypeDefinition(
    declarations: List<CoreModuleDeclaration> = [],
) = CoreTypeDefinition.ModuleType(
    declarations = declarations,
)

fun coreModuleDeclaration(): CoreModuleDeclaration = typeCoreModuleDeclaration()

fun typeCoreModuleDeclaration(
    type: CoreTypeDefinition = definedTypeCoreTypeDefinition(),
) = CoreModuleDeclaration.Type(
    type = type,
)

fun importCoreModuleDeclaration(
    moduleName: NameValue = nameValue(),
    entityName: NameValue = nameValue(),
    descriptor: CoreExternalType = functionCoreExternalType(),
) = CoreModuleDeclaration.Import(
    moduleName = moduleName,
    entityName = entityName,
    descriptor = descriptor,
)

fun exportCoreModuleDeclaration(
    name: NameValue = nameValue(),
    descriptor: CoreExternalType = functionCoreExternalType(),
) = CoreModuleDeclaration.Export(
    name = name,
    descriptor = descriptor,
)

fun outerAliasCoreModuleDeclaration(
    count: UInt = 0u,
    typeIndex: ModuleIndex.TypeIndex = typeIndex(),
) = CoreModuleDeclaration.OuterAlias(
    count = count,
    typeIndex = typeIndex,
)

fun coreExternalType(): CoreExternalType = functionCoreExternalType()

fun functionCoreExternalType(
    typeIndex: ModuleIndex.TypeIndex = typeIndex(),
) = CoreExternalType.Function(typeIndex)

fun tableCoreExternalType(
    type: TableType = tableType(),
) = CoreExternalType.Table(type)

fun memoryCoreExternalType(
    type: MemoryType = memoryType(),
) = CoreExternalType.Memory(type)

fun globalCoreExternalType(
    type: GlobalType = globalType(),
) = CoreExternalType.Global(type)

fun tagCoreExternalType(
    attribute: TagType.Attribute = attribute(),
    typeIndex: ModuleIndex.TypeIndex = typeIndex(),
) = CoreExternalType.Tag(
    attribute = attribute,
    typeIndex = typeIndex,
)
