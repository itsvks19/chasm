package io.github.charlietap.chasm.decoder.decoder.component.section

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.ComponentValue
import io.github.charlietap.chasm.decoder.component.section.ComponentSection
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.ComponentVectorDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.fixture.ast.component.boolComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.boolComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.componentValue
import io.github.charlietap.chasm.fixture.ast.component.u32ComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.u32ComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.valueComponentDefinition
import kotlin.test.Test
import kotlin.test.assertEquals

class ValueSectionDecoderTest {

    @Test
    fun `wraps every decoded value in a source-ordered definition`() {
        val first = componentValue(
            type = boolComponentValueType(),
            value = boolComponentValueLiteral(value = false),
        )
        val second = componentValue(
            type = u32ComponentValueType(),
            value = u32ComponentValueLiteral(value = 42u),
        )
        val context = componentDecoderContext()

        val valueDecoder: ComponentDecoder<ComponentValue> = { Ok(first) }
        val vectorDecoder: ComponentVectorDecoder<ComponentValue> = { _, _ ->
            Ok(Vector(listOf(first, second)))
        }

        val actual = ValueSectionDecoder(
            context = context,
            valueDecoder = valueDecoder,
            vectorDecoder = vectorDecoder,
        )

        val expected = Ok(
            ComponentSection.Definitions(
                listOf(
                    valueComponentDefinition(value = first),
                    valueComponentDefinition(value = second),
                ),
            ),
        )

        assertEquals(expected, actual)
    }
}
