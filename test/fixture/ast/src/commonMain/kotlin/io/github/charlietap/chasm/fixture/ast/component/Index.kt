package io.github.charlietap.chasm.fixture.ast.component

import io.github.charlietap.chasm.ast.component.Index

fun index(): Index = componentTypeIndex()

fun componentTypeIndex(
    index: UInt = 0u,
) = Index.ComponentTypeIndex(
    idx = index,
)

fun componentFunctionIndex(
    index: UInt = 0u,
) = Index.ComponentFunctionIndex(
    idx = index,
)

fun componentValueIndex(
    index: UInt = 0u,
) = Index.ComponentValueIndex(
    idx = index,
)

fun componentModuleIndex(
    index: UInt = 0u,
) = Index.ComponentModuleIndex(
    idx = index,
)

fun componentModuleInstanceIndex(
    index: UInt = 0u,
) = Index.ComponentModuleInstanceIndex(
    idx = index,
)

fun componentIndex(
    index: UInt = 0u,
) = Index.ComponentIndex(
    idx = index,
)

fun componentInstanceIndex(
    index: UInt = 0u,
) = Index.ComponentInstanceIndex(
    idx = index,
)
