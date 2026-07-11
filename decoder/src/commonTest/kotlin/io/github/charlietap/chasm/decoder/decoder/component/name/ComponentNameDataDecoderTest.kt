package io.github.charlietap.chasm.decoder.decoder.component.name

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.NameSubsection
import io.github.charlietap.chasm.decoder.decoder.ReaderDecoder
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import io.github.charlietap.chasm.fixture.ast.component.componentNameSubsection
import io.github.charlietap.chasm.fixture.ast.component.functionNameSort
import io.github.charlietap.chasm.fixture.ast.component.nameDataComponentCustom
import io.github.charlietap.chasm.fixture.ast.component.sortNameSubsection
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentNameDataDecoderTest {

    @Test
    fun `composes decoded name subsections in encounter order`() {
        val context = componentDecoderContext(
            reader = BinaryReader(byteArrayOf(0x00, 0x00, 0x00)),
        )
        val componentName = componentNameSubsection(name = nameValue("app"))
        val sortName = sortNameSubsection(sort = functionNameSort())
        val subsections = sequenceOf(componentName, null, sortName).iterator()
        val subsectionDecoder: ReaderDecoder<NameSubsection?> = { scopedContext ->
            scopedContext.reader.ubyte()
            Ok(subsections.next())
        }

        val actual = ComponentNameDataDecoder(
            context = context,
            subsectionDecoder = subsectionDecoder,
        )

        val expected = Ok(
            nameDataComponentCustom(
                subsections = listOf(componentName, sortName),
            ),
        )
        assertEquals(expected, actual)
    }
}
