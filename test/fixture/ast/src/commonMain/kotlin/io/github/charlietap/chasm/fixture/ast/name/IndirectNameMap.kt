package io.github.charlietap.chasm.fixture.ast.name

import io.github.charlietap.chasm.ast.name.IndirectNameAssociation
import io.github.charlietap.chasm.ast.name.IndirectNameMap
import io.github.charlietap.chasm.ast.name.NameMap

fun indirectNameAssociation(
    idx: UInt = 0u,
    nameMap: NameMap = nameMap(),
) = IndirectNameAssociation(
    idx = idx,
    nameMap = nameMap,
)

fun indirectNameMap(
    associations: List<IndirectNameAssociation> = emptyList(),
): IndirectNameMap = associations
