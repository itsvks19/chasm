package io.github.charlietap.chasm.decoder.decoder.component.custom

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.NameData
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.decoder.ReaderDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.fixture.ast.component.componentNameSubsection
import io.github.charlietap.chasm.fixture.ast.component.nameDataComponentCustom
import io.github.charlietap.chasm.fixture.ast.component.uninterpretedComponentCustom
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.fixture.config.componentConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ComponentCustomSectionDecoderTest {

    @Test
    fun `decodes a component name custom section when enabled`() {
        val payload = byteArrayOf(0x01)
        val context = context(payload = payload, decodeNameSection = true)
        val name = nameValue(COMPONENT_NAME)
        val nameData = nameDataComponentCustom(
            subsections = listOf(componentNameSubsection(name = nameValue("app"))),
        )
        val nameDecoder: ReaderDecoder<NameValue> = { scopedContext ->
            scopedContext.reader.ubytes(COMPONENT_NAME.length.toUInt() + 1u)
            Ok(name)
        }
        val nameDataDecoder: ReaderDecoder<NameData> = { Ok(nameData) }

        val actual = ComponentCustomSectionDecoder(
            context = context,
            nameDecoder = nameDecoder,
            nameDataDecoder = nameDataDecoder,
        )

        val expected = Ok(nameData)
        assertEquals(expected, actual)
    }

    @Test
    fun `falls back to uninterpreted data when component name decoding fails`() {
        val payload = byteArrayOf(0x01)
        val context = context(payload = payload, decodeNameSection = true)
        val name = nameValue(COMPONENT_NAME)
        val error = WasmDecodeError.IOError(Exception())
        val nameDecoder: ReaderDecoder<NameValue> = { scopedContext ->
            scopedContext.reader.ubytes(COMPONENT_NAME.length.toUInt() + 1u)
            Ok(name)
        }
        val nameDataDecoder: ReaderDecoder<NameData> = { Err(error) }

        val actual = ComponentCustomSectionDecoder(
            context = context,
            nameDecoder = nameDecoder,
            nameDataDecoder = nameDataDecoder,
        )

        val expected = Ok(uninterpretedComponentCustom(name = name, data = payload.asUByteArray()))
        assertEquals(expected, actual)
    }

    @Test
    fun `preserves component name data when name decoding is disabled`() {
        val payload = byteArrayOf(0x00, 0x00)
        val context = context(payload = payload, decodeNameSection = false)
        val name = nameValue(COMPONENT_NAME)
        val nameDecoder: ReaderDecoder<NameValue> = { scopedContext ->
            scopedContext.reader.ubytes(COMPONENT_NAME.length.toUInt() + 1u)
            Ok(name)
        }
        val actual = ComponentCustomSectionDecoder(
            context = context,
            nameDecoder = nameDecoder,
            nameDataDecoder = neverNameDataDecoder,
        )

        val expected = Ok(uninterpretedComponentCustom(name = name, data = payload.asUByteArray()))
        assertEquals(expected, actual)
    }

    private fun context(
        payload: ByteArray,
        decodeNameSection: Boolean,
    ) = (byteArrayOf(COMPONENT_NAME.length.toByte()) + COMPONENT_NAME.encodeToByteArray() + payload).let { bytes ->
        componentDecoderContext(
            reader = BinaryReader(bytes),
            config = componentConfig(decodeNameSection = decodeNameSection),
            sectionSize = SectionSize(bytes.size.toUInt()),
        )
    }

    private companion object {
        const val COMPONENT_NAME = "component-name"

        val neverNameDataDecoder: ReaderDecoder<NameData> = {
            fail("component name data decoder should not be called")
        }
    }
}
