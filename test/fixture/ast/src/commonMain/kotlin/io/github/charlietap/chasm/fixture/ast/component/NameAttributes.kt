package io.github.charlietap.chasm.fixture.ast.component

import io.github.charlietap.chasm.ast.component.ExternalAttribute
import io.github.charlietap.chasm.ast.component.NameAttributes
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.fixture.ast.value.nameValue

fun nameAttributes(
    name: NameValue = nameValue(),
    attributes: List<ExternalAttribute> = emptyList(),
) = NameAttributes(
    name = name,
    attributes = attributes,
)

fun externalAttribute(): ExternalAttribute = implementsExternalAttribute()

fun implementsExternalAttribute(
    interfaceName: String = "",
) = ExternalAttribute.Implements(
    interfaceName = interfaceName,
)

fun versionSuffixExternalAttribute(
    suffix: String = "",
) = ExternalAttribute.VersionSuffix(
    suffix = suffix,
)

fun externalIdExternalAttribute(
    id: NameValue = nameValue(),
) = ExternalAttribute.ExternalId(
    id = id,
)
