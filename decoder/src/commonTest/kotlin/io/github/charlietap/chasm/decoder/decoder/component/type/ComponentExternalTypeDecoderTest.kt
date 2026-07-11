package io.github.charlietap.chasm.decoder.decoder.component.type

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.ExternalType
import io.github.charlietap.chasm.ast.component.Index.ComponentTypeIndex
import io.github.charlietap.chasm.ast.component.TypeBound
import io.github.charlietap.chasm.ast.component.ValueBound
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.ComponentTypeDecodeError
import io.github.charlietap.chasm.decoder.fixture.assertWasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.ast.component.componentExternalType
import io.github.charlietap.chasm.fixture.ast.component.coreModuleExternalType
import io.github.charlietap.chasm.fixture.ast.component.functionExternalType
import io.github.charlietap.chasm.fixture.ast.component.instanceExternalType
import io.github.charlietap.chasm.fixture.ast.component.typeBound
import io.github.charlietap.chasm.fixture.ast.component.typeExternalType
import io.github.charlietap.chasm.fixture.ast.component.valueBound
import io.github.charlietap.chasm.fixture.ast.component.valueExternalType
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.ast.module.Index.TypeIndex as ModuleTypeIndex
import io.github.charlietap.chasm.fixture.ast.component.componentTypeIndex as componentTypeIndexFixture

class ComponentExternalTypeDecoderTest {

    @Test
    fun `can decode each external type`() {
        val moduleTypeIndex = typeIndex(2u)
        val moduleTypeIndexDecoder: Decoder<ModuleTypeIndex> = {
            Ok(moduleTypeIndex)
        }

        val componentTypeIndex = componentTypeIndexFixture(3u)
        val componentTypeIndexDecoder: ComponentDecoder<ComponentTypeIndex> = {
            Ok(componentTypeIndex)
        }

        val valueBound = valueBound()
        val valueBoundDecoder: ComponentDecoder<ValueBound> = {
            Ok(valueBound)
        }

        val typeBound = typeBound()
        val typeBoundDecoder: ComponentDecoder<TypeBound> = {
            Ok(typeBound)
        }

        val decoder: ComponentDecoder<ExternalType> = { context ->
            ComponentExternalTypeDecoder(
                context = context,
                moduleTypeIndexDecoder = moduleTypeIndexDecoder,
                componentTypeIndexDecoder = componentTypeIndexDecoder,
                valueBoundDecoder = valueBoundDecoder,
                typeBoundDecoder = typeBoundDecoder,
            )
        }

        val cases = listOf(
            Case(bytes(0x00, 0x11), coreModuleExternalType(moduleTypeIndex)),
            Case(bytes(0x01), functionExternalType(componentTypeIndex)),
            Case(bytes(0x02), valueExternalType(valueBound)),
            Case(bytes(0x03), typeExternalType(typeBound)),
            Case(bytes(0x04), componentExternalType(componentTypeIndex)),
            Case(bytes(0x05), instanceExternalType(componentTypeIndex)),
        )

        cases.forEach { case ->
            val context = componentTypeDecoderContext(case.bytes)

            val actual = decoder(context)
            val expected = Ok(case.expected)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `validates the core module marker`() {
        val context = componentTypeDecoderContext(bytes(0x00, 0x10))

        val actual = ComponentExternalTypeDecoder(context)
        val expected = Err(ComponentTypeDecodeError.InvalidReservedByte(0x11u, 0x10u))

        assertEquals(expected, actual)
    }

    @Test
    fun `propagates reader errors exactly`() {
        val error = ComponentTypeDecodeError.UnknownExternalType(0xFFu)
        val context = componentDecoderContext(
            reader = FakeWasmBinaryReader(fakeUByteReader = { Err(error) }),
        )

        assertWasmDecodeError(error) {
            ComponentExternalTypeDecoder(context)
        }
    }

    private data class Case(
        val bytes: ByteArray,
        val expected: ExternalType,
    )
}

private fun bytes(vararg values: Int): ByteArray = values.map(Int::toByte).toByteArray()
