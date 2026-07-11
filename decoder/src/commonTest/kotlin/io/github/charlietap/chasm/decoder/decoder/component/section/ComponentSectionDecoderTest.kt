package io.github.charlietap.chasm.decoder.decoder.component.section

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.Custom
import io.github.charlietap.chasm.decoder.component.section.ComponentSection
import io.github.charlietap.chasm.decoder.component.section.ComponentSectionType
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.fixture.ast.component.componentDefinition
import io.github.charlietap.chasm.fixture.ast.component.uninterpretedComponentCustom
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentSectionDecoderTest {

    @Test
    fun `routes every component section type to its decoder`() {
        val sections = ComponentSectionType.entries.associateWith { sectionType ->
            ComponentSection.Definitions(
                List(sectionType.ordinal + 1) { componentDefinition() },
            )
        }
        val custom = uninterpretedComponentCustom(
            name = nameValue("custom"),
            data = ubyteArrayOf(),
        )
        val customSectionDecoder: ComponentDecoder<Custom> = { Ok(custom) }
        val coreModuleSectionDecoder = sectionDecoder(ComponentSectionType.CoreModule, sections)
        val coreInstanceSectionDecoder = sectionDecoder(ComponentSectionType.CoreInstance, sections)
        val coreTypeSectionDecoder = sectionDecoder(ComponentSectionType.CoreType, sections)
        val nestedComponentSectionDecoder = sectionDecoder(ComponentSectionType.Component, sections)
        val instanceSectionDecoder = sectionDecoder(ComponentSectionType.Instance, sections)
        val aliasSectionDecoder = sectionDecoder(ComponentSectionType.Alias, sections)
        val typeSectionDecoder = sectionDecoder(ComponentSectionType.Type, sections)
        val canonicalSectionDecoder = sectionDecoder(ComponentSectionType.Canonical, sections)
        val startSectionDecoder = sectionDecoder(ComponentSectionType.Start, sections)
        val importSectionDecoder = sectionDecoder(ComponentSectionType.Import, sections)
        val exportSectionDecoder = sectionDecoder(ComponentSectionType.Export, sections)
        val valueSectionDecoder = sectionDecoder(ComponentSectionType.Value, sections)
        val decoder: ComponentDecoder<ComponentSection> = { context ->
            ComponentSectionDecoder(
                context = context,
                customSectionDecoder = customSectionDecoder,
                coreModuleSectionDecoder = coreModuleSectionDecoder,
                coreInstanceSectionDecoder = coreInstanceSectionDecoder,
                coreTypeSectionDecoder = coreTypeSectionDecoder,
                nestedComponentSectionDecoder = nestedComponentSectionDecoder,
                instanceSectionDecoder = instanceSectionDecoder,
                aliasSectionDecoder = aliasSectionDecoder,
                typeSectionDecoder = typeSectionDecoder,
                canonicalSectionDecoder = canonicalSectionDecoder,
                startSectionDecoder = startSectionDecoder,
                importSectionDecoder = importSectionDecoder,
                exportSectionDecoder = exportSectionDecoder,
                valueSectionDecoder = valueSectionDecoder,
            )
        }

        ComponentSectionType.entries.forEach { sectionType ->
            val context = componentDecoderContext(sectionType = sectionType)

            val actual = decoder(context)

            val expectedSection = if (sectionType == ComponentSectionType.Custom) {
                ComponentSection.Custom(custom)
            } else {
                sections.getValue(sectionType)
            }
            val expected = Ok(expectedSection)

            assertEquals(expected, actual)
        }
    }

    private fun sectionDecoder(
        sectionType: ComponentSectionType,
        sections: Map<ComponentSectionType, ComponentSection>,
    ): ComponentDecoder<ComponentSection> = { Ok(sections.getValue(sectionType)) }
}
