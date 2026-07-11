package io.github.charlietap.chasm.decoder.decoder.component.section

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.CoreTypeDefinition
import io.github.charlietap.chasm.ast.component.Export
import io.github.charlietap.chasm.ast.component.Import
import io.github.charlietap.chasm.ast.component.TypeDefinition
import io.github.charlietap.chasm.decoder.component.section.ComponentSection
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.decoder.component.type.componentTypeDecoderContext
import io.github.charlietap.chasm.decoder.decoder.vector.ComponentVectorDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.fixture.ast.component.boolComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.componentFunctionIndex
import io.github.charlietap.chasm.fixture.ast.component.componentTypeIndex
import io.github.charlietap.chasm.fixture.ast.component.coreTypeComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.exportComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.functionExportTarget
import io.github.charlietap.chasm.fixture.ast.component.functionExternalType
import io.github.charlietap.chasm.fixture.ast.component.importComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.moduleTypeCoreTypeDefinition
import io.github.charlietap.chasm.fixture.ast.component.nameAttributes
import io.github.charlietap.chasm.fixture.ast.component.typeComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.valueTypeDefinition
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentTypeSectionDecoderTest {

    @Test
    fun `wraps core type definitions`() {
        val type = moduleTypeCoreTypeDefinition(emptyList())
        val context = componentTypeDecoderContext(byteArrayOf())

        val typeDecoder: ComponentDecoder<CoreTypeDefinition> = { Ok(type) }
        val vectorDecoder: ComponentVectorDecoder<CoreTypeDefinition> = { _, _ ->
            Ok(Vector(listOf(type)))
        }

        val actual = CoreTypeSectionDecoder(
            context = context,
            typeDecoder = typeDecoder,
            vectorDecoder = vectorDecoder,
        )

        val expected = Ok(
            ComponentSection.Definitions(listOf(coreTypeComponentDefinition(type))),
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `wraps component type definitions`() {
        val type = valueTypeDefinition(boolComponentValueType())
        val context = componentTypeDecoderContext(byteArrayOf())

        val typeDecoder: ComponentDecoder<TypeDefinition> = { Ok(type) }
        val vectorDecoder: ComponentVectorDecoder<TypeDefinition> = { _, _ ->
            Ok(Vector(listOf(type)))
        }

        val actual = TypeSectionDecoder(
            context = context,
            typeDecoder = typeDecoder,
            vectorDecoder = vectorDecoder,
        )

        val expected = Ok(
            ComponentSection.Definitions(listOf(typeComponentDefinition(type))),
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `preserves import definitions`() {
        val definition = importComponentDefinition(
            nameAttributes(nameValue("i")),
            functionExternalType(componentTypeIndex(0u)),
        )
        val context = componentTypeDecoderContext(byteArrayOf())

        val importDecoder: ComponentDecoder<Import> = { Ok(definition) }
        val vectorDecoder: ComponentVectorDecoder<Import> = { _, _ ->
            Ok(Vector(listOf(definition)))
        }

        val actual = ImportSectionDecoder(
            context = context,
            importDecoder = importDecoder,
            vectorDecoder = vectorDecoder,
        )

        val expected = Ok(ComponentSection.Definitions(listOf(definition)))

        assertEquals(expected, actual)
    }

    @Test
    fun `preserves export definitions`() {
        val definition = exportComponentDefinition(
            name = nameAttributes(nameValue("e")),
            target = functionExportTarget(componentFunctionIndex(0u)),
            type = null,
        )
        val context = componentTypeDecoderContext(byteArrayOf())

        val exportDecoder: ComponentDecoder<Export> = { Ok(definition) }
        val vectorDecoder: ComponentVectorDecoder<Export> = { _, _ ->
            Ok(Vector(listOf(definition)))
        }

        val actual = ExportSectionDecoder(
            context = context,
            exportDecoder = exportDecoder,
            vectorDecoder = vectorDecoder,
        )

        val expected = Ok(ComponentSection.Definitions(listOf(definition)))

        assertEquals(expected, actual)
    }
}
