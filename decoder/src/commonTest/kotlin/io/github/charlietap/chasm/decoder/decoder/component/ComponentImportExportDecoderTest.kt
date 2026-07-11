package io.github.charlietap.chasm.decoder.decoder.component

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.ExportTarget
import io.github.charlietap.chasm.ast.component.ExternalType
import io.github.charlietap.chasm.ast.component.NameAttributes
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.fixture.ast.component.componentFunctionIndex
import io.github.charlietap.chasm.fixture.ast.component.componentTypeIndex
import io.github.charlietap.chasm.fixture.ast.component.exportComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.functionExportTarget
import io.github.charlietap.chasm.fixture.ast.component.functionExternalType
import io.github.charlietap.chasm.fixture.ast.component.importComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.nameAttributes
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentImportExportDecoderTest {

    @Test
    fun `decodes a component import`() {
        val context = componentDecoderContext()
        val name = nameAttributes(name = nameValue("i"))
        val type = functionExternalType(typeIndex = componentTypeIndex(2u))
        val nameAttributesDecoder: ComponentDecoder<NameAttributes> = { Ok(name) }
        val externalTypeDecoder: ComponentDecoder<ExternalType> = { Ok(type) }

        val actual = ComponentImportDecoder(
            context = context,
            nameAttributesDecoder = nameAttributesDecoder,
            externalTypeDecoder = externalTypeDecoder,
        )

        val expected = Ok(importComponentDefinition(name = name, type = type))
        assertEquals(expected, actual)
    }

    @Test
    fun `decodes a component export with an external type ascription`() {
        val context = componentDecoderContext()
        val name = nameAttributes(name = nameValue("e"))
        val target = functionExportTarget(index = componentFunctionIndex(3u))
        val type = functionExternalType(typeIndex = componentTypeIndex(4u))
        val nameAttributesDecoder: ComponentDecoder<NameAttributes> = { Ok(name) }
        val targetDecoder: ComponentDecoder<ExportTarget> = { Ok(target) }
        val optionalExternalTypeDecoder: ComponentDecoder<ExternalType?> = { Ok(type) }

        val actual = ComponentExportDecoder(
            context = context,
            nameAttributesDecoder = nameAttributesDecoder,
            targetDecoder = targetDecoder,
            optionalExternalTypeDecoder = optionalExternalTypeDecoder,
        )

        val expected = Ok(exportComponentDefinition(name = name, target = target, type = type))
        assertEquals(expected, actual)
    }
}
