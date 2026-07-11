package io.github.charlietap.chasm.decoder.decoder.component.section

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.CanonicalDefinition
import io.github.charlietap.chasm.decoder.component.section.ComponentSection
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.ComponentVectorDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.fixture.ast.component.canonComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.taskCancelCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.waitableSetNewCanonicalDefinition
import kotlin.test.Test
import kotlin.test.assertEquals

class CanonicalSectionDecoderTest {

    @Test
    fun `wraps each decoded canonical definition in source order`() {
        val definitions = listOf(
            taskCancelCanonicalDefinition(),
            waitableSetNewCanonicalDefinition(),
        )
        val context = componentDecoderContext()

        val canonicalDefinitionDecoder: ComponentDecoder<CanonicalDefinition> = {
            Ok(definitions.first())
        }
        val vectorDecoder: ComponentVectorDecoder<CanonicalDefinition> = { _, _ ->
            Ok(Vector(definitions))
        }

        val actual = CanonicalSectionDecoder(
            context = context,
            canonicalDefinitionDecoder = canonicalDefinitionDecoder,
            vectorDecoder = vectorDecoder,
        )

        val expected = Ok(
            ComponentSection.Definitions(definitions.map(::canonComponentDefinition)),
        )

        assertEquals(expected, actual)
    }
}
