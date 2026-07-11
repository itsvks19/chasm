package io.github.charlietap.chasm.decoder.decoder.component.name

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.NameSort
import io.github.charlietap.chasm.ast.name.NameMap
import io.github.charlietap.chasm.decoder.decoder.ReaderDecoder
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.fixture.ast.component.functionNameSort
import io.github.charlietap.chasm.fixture.ast.component.sortNameSubsection
import io.github.charlietap.chasm.fixture.ast.name.nameAssociation
import io.github.charlietap.chasm.fixture.ast.name.nameMap
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import kotlin.test.Test
import kotlin.test.assertEquals

class SortNameSubsectionDecoderTest {

    @Test
    fun `composes the injected sort and name map decoders`() {
        val context = componentDecoderContext()
        val sort = functionNameSort()
        val names = nameMap(
            associations = listOf(nameAssociation(idx = 4u, name = nameValue("run"))),
        )
        val sortDecoder: ReaderDecoder<NameSort> = { Ok(sort) }
        val nameMapDecoder: ReaderDecoder<NameMap> = { Ok(names) }

        val actual = SortNameSubsectionDecoder(
            context = context,
            sortDecoder = sortDecoder,
            nameMapDecoder = nameMapDecoder,
        )

        val expected = Ok(sortNameSubsection(sort = sort, nameMap = names))
        assertEquals(expected, actual)
    }
}
