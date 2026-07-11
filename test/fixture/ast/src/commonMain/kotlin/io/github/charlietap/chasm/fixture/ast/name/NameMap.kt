package io.github.charlietap.chasm.fixture.ast.name

import io.github.charlietap.chasm.ast.name.NameAssociation
import io.github.charlietap.chasm.ast.name.NameMap
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.fixture.ast.value.nameValue

fun nameAssociation(
    idx: UInt = 0u,
    name: NameValue = nameValue(),
) = NameAssociation(
    idx = idx,
    name = name,
)

fun nameMap(
    associations: List<NameAssociation> = emptyList(),
): NameMap = associations
