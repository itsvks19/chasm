package io.github.charlietap.chasm.fixture.ast.component

import io.github.charlietap.chasm.ast.component.ComponentNameSubsection
import io.github.charlietap.chasm.ast.component.Custom
import io.github.charlietap.chasm.ast.component.NameData
import io.github.charlietap.chasm.ast.component.NameSort
import io.github.charlietap.chasm.ast.component.NameSubsection
import io.github.charlietap.chasm.ast.component.SortNameSubsection
import io.github.charlietap.chasm.ast.component.Uninterpreted
import io.github.charlietap.chasm.ast.name.NameMap
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.fixture.ast.name.nameMap
import io.github.charlietap.chasm.fixture.ast.value.nameValue

fun componentCustom(): Custom = uninterpretedComponentCustom()

fun uninterpretedComponentCustom(
    name: NameValue = nameValue(),
    data: UByteArray = ubyteArrayOf(),
) = Uninterpreted(
    name = name,
    data = data,
)

fun nameSubsection(): NameSubsection = componentNameSubsection()

fun componentNameSubsection(
    name: NameValue = nameValue(),
) = ComponentNameSubsection(
    name = name,
)

fun sortNameSubsection(
    sort: NameSort = nameSort(),
    nameMap: NameMap = nameMap(),
) = SortNameSubsection(
    sort = sort,
    nameMap = nameMap,
)

fun nameSort(): NameSort = coreFunctionNameSort()

fun coreFunctionNameSort() = NameSort.CoreFunction

fun coreTableNameSort() = NameSort.CoreTable

fun coreMemoryNameSort() = NameSort.CoreMemory

fun coreGlobalNameSort() = NameSort.CoreGlobal

fun coreTagNameSort() = NameSort.CoreTag

fun coreTypeNameSort() = NameSort.CoreType

fun coreModuleNameSort() = NameSort.CoreModule

fun coreInstanceNameSort() = NameSort.CoreInstance

fun functionNameSort() = NameSort.Function

fun valueNameSort() = NameSort.Value

fun typeNameSort() = NameSort.Type

fun componentNameSort() = NameSort.Component

fun instanceNameSort() = NameSort.Instance

fun nameDataComponentCustom(
    subsections: List<NameSubsection> = emptyList(),
) = NameData(
    subsections = subsections,
)
