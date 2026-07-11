package io.github.charlietap.chasm.decoder.decoder.component.name

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.SortNameSubsection
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.decoder.ReaderDecoder
import io.github.charlietap.chasm.decoder.error.ReaderDecodeError
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import io.github.charlietap.chasm.fixture.ast.component.componentNameSubsection
import io.github.charlietap.chasm.fixture.ast.component.functionNameSort
import io.github.charlietap.chasm.fixture.ast.component.sortNameSubsection
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ComponentNameSubsectionDecoderTest {

    @Test
    fun `delegates component name subsection payload decoding`() {
        val context = context(0x00, 0x01, 0x7F)
        val name = nameValue("app")
        val nameDecoder: ReaderDecoder<NameValue> = { scopedContext ->
            scopedContext.reader.ubyte()
            Ok(name)
        }
        val neverSortNameDecoder: ReaderDecoder<SortNameSubsection> = {
            fail("sort name decoder should not be called")
        }

        val actual = ComponentNameSubsectionDecoder(
            context = context,
            nameDecoder = nameDecoder,
            sortNameDecoder = neverSortNameDecoder,
        )

        val expected = Ok(componentNameSubsection(name = name))
        assertEquals(expected, actual)
    }

    @Test
    fun `delegates sort name subsection payload decoding`() {
        val context = context(0x01, 0x01, 0x7F)
        val sortName = sortNameSubsection(sort = functionNameSort())
        val neverNameDecoder: ReaderDecoder<NameValue> = {
            fail("name decoder should not be called")
        }
        val sortNameDecoder: ReaderDecoder<SortNameSubsection> = { scopedContext ->
            scopedContext.reader.ubyte()
            Ok(sortName)
        }

        val actual = ComponentNameSubsectionDecoder(
            context = context,
            nameDecoder = neverNameDecoder,
            sortNameDecoder = sortNameDecoder,
        )

        val expected = Ok(sortName)
        assertEquals(expected, actual)
    }

    @Test
    fun `unknown subsections are consumed and omitted`() {
        val context = context(0x09, 0x02, 0xAA, 0xBB)
        val neverNameDecoder: ReaderDecoder<NameValue> = {
            fail("name decoder should not be called")
        }
        val neverSortNameDecoder: ReaderDecoder<SortNameSubsection> = {
            fail("sort name decoder should not be called")
        }

        val actual = ComponentNameSubsectionDecoder(
            context = context,
            nameDecoder = neverNameDecoder,
            sortNameDecoder = neverSortNameDecoder,
        )

        val expected = Ok(null)
        assertEquals(expected, actual)
    }

    @Test
    fun `known subsection payloads must consume their declared size exactly`() {
        val context = context(0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00)
        val nameDecoder: ReaderDecoder<NameValue> = { scopedContext ->
            scopedContext.reader.ubytes(4u)
            Ok(nameValue())
        }
        val neverSortNameDecoder: ReaderDecoder<SortNameSubsection> = {
            fail("sort name decoder should not be called")
        }

        val actual = ComponentNameSubsectionDecoder(
            context = context,
            nameDecoder = nameDecoder,
            sortNameDecoder = neverSortNameDecoder,
        )

        val expected = Err(ReaderDecodeError.SizeMismatch(5u, 4u))
        assertEquals(expected, actual)
    }

    private fun context(vararg bytes: Int) = componentDecoderContext(
        reader = BinaryReader(bytes.map(Int::toByte).toByteArray()),
    )
}
