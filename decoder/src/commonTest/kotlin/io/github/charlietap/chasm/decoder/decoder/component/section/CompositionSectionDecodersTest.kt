package io.github.charlietap.chasm.decoder.decoder.component.section

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.Component
import io.github.charlietap.chasm.ast.component.CoreInstanceDefinition
import io.github.charlietap.chasm.ast.component.InstanceDefinition
import io.github.charlietap.chasm.ast.component.StartDefinition
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.decoder.component.section.ComponentSection
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.ComponentVectorDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.fixture.ast.component.component
import io.github.charlietap.chasm.fixture.ast.component.componentFunctionIndex
import io.github.charlietap.chasm.fixture.ast.component.componentVersion
import io.github.charlietap.chasm.fixture.ast.component.coreInstanceComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.coreModuleComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.inlineExportsCoreInstanceDefinition
import io.github.charlietap.chasm.fixture.ast.component.inlineExportsInstanceDefinition
import io.github.charlietap.chasm.fixture.ast.component.instanceComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.nestedComponentComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.startComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.startDefinition
import io.github.charlietap.chasm.fixture.ast.module.module
import kotlin.test.Test
import kotlin.test.assertEquals

class CoreModuleSectionDecoderTest {

    @Test
    fun `wraps one decoded core module`() {
        val module = module()
        val context = componentDecoderContext()

        val moduleDecoder: Decoder<Module> = { Ok(module) }

        val actual = CoreModuleSectionDecoder(
            context = context,
            moduleDecoder = moduleDecoder,
        )

        val expected = Ok(
            ComponentSection.Definitions(
                listOf(coreModuleComponentDefinition(module = module)),
            ),
        )

        assertEquals(expected, actual)
    }
}

class CoreInstanceSectionDecoderTest {

    @Test
    fun `flattens decoded core instances into definitions`() {
        val instance = inlineExportsCoreInstanceDefinition()
        val context = componentDecoderContext()

        val instanceDecoder: ComponentDecoder<CoreInstanceDefinition> = { Ok(instance) }
        val vectorDecoder: ComponentVectorDecoder<CoreInstanceDefinition> = { _, _ ->
            Ok(Vector(listOf(instance)))
        }

        val actual = CoreInstanceSectionDecoder(
            context = context,
            instanceDecoder = instanceDecoder,
            vectorDecoder = vectorDecoder,
        )

        val expected = Ok(
            ComponentSection.Definitions(
                listOf(coreInstanceComponentDefinition(instance = instance)),
            ),
        )

        assertEquals(expected, actual)
    }
}

class NestedComponentSectionDecoderTest {

    @Test
    fun `wraps one decoded nested component`() {
        val component = component(version = componentVersion())
        val context = componentDecoderContext()

        val componentDecoder: ComponentDecoder<Component> = { Ok(component) }

        val actual = NestedComponentSectionDecoder(
            context = context,
            componentDecoder = componentDecoder,
        )

        val expected = Ok(
            ComponentSection.Definitions(
                listOf(nestedComponentComponentDefinition(component = component)),
            ),
        )

        assertEquals(expected, actual)
    }
}

class InstanceSectionDecoderTest {

    @Test
    fun `flattens decoded component instances into definitions`() {
        val instance = inlineExportsInstanceDefinition()
        val context = componentDecoderContext()

        val instanceDecoder: ComponentDecoder<InstanceDefinition> = { Ok(instance) }
        val vectorDecoder: ComponentVectorDecoder<InstanceDefinition> = { _, _ ->
            Ok(Vector(listOf(instance)))
        }

        val actual = InstanceSectionDecoder(
            context = context,
            instanceDecoder = instanceDecoder,
            vectorDecoder = vectorDecoder,
        )

        val expected = Ok(
            ComponentSection.Definitions(
                listOf(instanceComponentDefinition(instance = instance)),
            ),
        )

        assertEquals(expected, actual)
    }
}

class StartSectionDecoderTest {

    @Test
    fun `wraps one decoded component start definition`() {
        val start = startDefinition(functionIndex = componentFunctionIndex(2u))
        val context = componentDecoderContext()

        val startDecoder: ComponentDecoder<StartDefinition> = { Ok(start) }

        val actual = StartSectionDecoder(
            context = context,
            startDecoder = startDecoder,
        )

        val expected = Ok(
            ComponentSection.Definitions(
                listOf(startComponentDefinition(start = start)),
            ),
        )

        assertEquals(expected, actual)
    }
}
