package io.github.charlietap.chasm.ast.component

import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.ast.module.Import.Descriptor as ModuleExternalDescriptor
import io.github.charlietap.chasm.ast.module.Index.TypeIndex as ModuleTypeIndex

sealed interface CoreTypeDefinition {

    data class DefinedType(val type: RecursiveType) : CoreTypeDefinition

    data class ModuleType(val declarations: List<CoreModuleDeclaration>) : CoreTypeDefinition
}

sealed interface CoreModuleDeclaration {

    data class Type(val type: RecursiveType) : CoreModuleDeclaration

    data class Import(
        val moduleName: NameValue,
        val entityName: NameValue,
        val descriptor: ModuleExternalDescriptor,
    ) : CoreModuleDeclaration

    data class Export(
        val name: NameValue,
        val descriptor: ModuleExternalDescriptor,
    ) : CoreModuleDeclaration

    data class OuterAlias(
        val count: UInt,
        val typeIndex: ModuleTypeIndex,
    ) : CoreModuleDeclaration
}
