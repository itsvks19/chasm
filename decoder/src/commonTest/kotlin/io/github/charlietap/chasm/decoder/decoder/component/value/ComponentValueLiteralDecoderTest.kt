package io.github.charlietap.chasm.decoder.decoder.component.value

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.ast.component.ComponentValueLiteral
import io.github.charlietap.chasm.ast.component.ValueType
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.context.ComponentDecoderContext
import io.github.charlietap.chasm.decoder.decoder.ReaderDecoder
import io.github.charlietap.chasm.decoder.error.ComponentValueDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.assertWasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.ast.component.binaryComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.boolComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.boolComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.charComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.charComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.componentTypeIndex
import io.github.charlietap.chasm.fixture.ast.component.errorContextComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.f32ComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.f32ComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.f64ComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.f64ComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.nanComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.s16ComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.s16ComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.s32ComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.s32ComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.s64ComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.s64ComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.s8ComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.s8ComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.stringComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.stringComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.typeIndexComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.u16ComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.u16ComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.u32ComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.u32ComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.u64ComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.u64ComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.u8ComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.u8ComponentValueType
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ComponentValueLiteralDecoderTest {

    @Test
    fun `decodes primitive literals and rejects invalid encodings`() {
        val cases = listOf(
            Case(bytes(0x00), boolComponentValueType(), Ok(boolComponentValueLiteral(value = false))),
            Case(bytes(0x01), boolComponentValueType(), Ok(boolComponentValueLiteral(value = true))),
            Case(
                bytes(0x02),
                boolComponentValueType(),
                Err(ComponentValueDecodeError.InvalidBoolean(0x02u)),
            ),
            Case(bytes(0xFF), s8ComponentValueType(), Ok(s8ComponentValueLiteral(value = -1))),
            Case(bytes(0xFF), u8ComponentValueType(), Ok(u8ComponentValueLiteral(value = 255u))),
            Case(bytes(0xFF, 0x7E), s16ComponentValueType(), Ok(s16ComponentValueLiteral(value = -129))),
            Case(
                bytes(0xFF, 0xFF, 0x03),
                u16ComponentValueType(),
                Ok(u16ComponentValueLiteral(value = 65535u)),
            ),
            Case(bytes(0x7E), s32ComponentValueType(), Ok(s32ComponentValueLiteral(value = -2))),
            Case(
                bytes(0xE5, 0x8E, 0x26),
                u32ComponentValueType(),
                Ok(u32ComponentValueLiteral(value = 624485u)),
            ),
            Case(bytes(0x7F), s64ComponentValueType(), Ok(s64ComponentValueLiteral(value = -1))),
            Case(bytes(0x80, 0x01), u64ComponentValueType(), Ok(u64ComponentValueLiteral(value = 128u))),
            Case(
                bytes(0x00, 0x00, 0x80, 0x3F),
                f32ComponentValueType(),
                Ok(f32ComponentValueLiteral(value = 1.0f)),
            ),
            Case(
                bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xF0, 0x3F),
                f64ComponentValueType(),
                Ok(f64ComponentValueLiteral(value = 1.0)),
            ),
            Case(
                bytes(0x00, 0x00, 0xC0, 0x7F),
                f32ComponentValueType(),
                Ok(nanComponentValueLiteral()),
            ),
            Case(
                bytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xF8, 0x7F),
                f64ComponentValueType(),
                Ok(nanComponentValueLiteral()),
            ),
            Case(
                bytes(0x01, 0x00, 0xC0, 0x7F),
                f32ComponentValueType(),
                Err(ComponentValueDecodeError.NonCanonicalNan),
            ),
            Case(
                bytes(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0xF8, 0x7F),
                f64ComponentValueType(),
                Err(ComponentValueDecodeError.NonCanonicalNan),
            ),
            Case(
                bytes(0x41),
                charComponentValueType(),
                Ok(charComponentValueLiteral(codePoint = 'A'.code.toUInt())),
            ),
            Case(
                bytes(0xF0, 0x9F, 0x98, 0x80),
                charComponentValueType(),
                Ok(charComponentValueLiteral(codePoint = 0x1F600u)),
            ),
        )

        val nameDecoder: ReaderDecoder<NameValue> = {
            fail("name decoder must not be used for primitive literals")
        }
        val decoder: (
            ComponentDecoderContext,
            ValueType,
            UInt,
        ) -> Result<ComponentValueLiteral, WasmDecodeError> = { context, type, payloadSize ->
            ComponentValueLiteralDecoder(context, type, payloadSize, nameDecoder)
        }

        cases.forEach { case ->
            val context = componentDecoderContext(
                reader = BinaryReader(case.bytes),
            )

            val actual = decoder(context, case.type, case.bytes.size.toUInt())
            val expected = case.expected

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `rejects malformed surrogate and multiple-scalar char payloads`() {
        val cases = listOf(
            bytes(0x80),
            bytes(0xED, 0xA0, 0x80),
            bytes(0x61, 0x62),
        )

        val nameDecoder: ReaderDecoder<NameValue> = {
            fail("name decoder must not be used for character literals")
        }
        val decoder: (
            ComponentDecoderContext,
            ValueType,
            UInt,
        ) -> Result<ComponentValueLiteral, WasmDecodeError> = { context, type, payloadSize ->
            ComponentValueLiteralDecoder(context, type, payloadSize, nameDecoder)
        }

        cases.forEach { bytes ->
            val context = componentDecoderContext(
                reader = BinaryReader(bytes),
            )

            val result = decoder(context, charComponentValueType(), bytes.size.toUInt())
            val actual = result.fold(
                success = { null },
                failure = { error ->
                    (error as? ComponentValueDecodeError.InvalidUnicode)?.bytes?.toList()
                },
            )
            val expected = bytes.asUByteArray().toList()

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `decodes strings through the injected core name decoder`() {
        val name = nameValue("component string")
        val nameDecoder: ReaderDecoder<NameValue> = {
            Ok(name)
        }
        val context = componentDecoderContext()

        val actual = ComponentValueLiteralDecoder(
            context = context,
            type = stringComponentValueType(),
            payloadSize = 0u,
            nameDecoder = nameDecoder,
        )
        val expected = Ok(stringComponentValueLiteral(value = name.name))

        assertEquals(expected, actual)
    }

    @Test
    fun `preserves indexed value payloads without invoking primitive dependencies`() {
        val bytes = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
        val context = componentDecoderContext(
            reader = BinaryReader(bytes),
        )
        val nameDecoder: ReaderDecoder<NameValue> = {
            fail("name decoder must not be used for indexed payloads")
        }

        val actual = ComponentValueLiteralDecoder(
            context = context,
            type = typeIndexComponentValueType(index = componentTypeIndex(7u)),
            payloadSize = bytes.size.toUInt(),
            nameDecoder = nameDecoder,
        )
        val expected = Ok(binaryComponentValueLiteral(bytes = bytes.asUByteArray().toList()))

        assertEquals(expected, actual)
    }

    @Test
    fun `rejects value types without a direct literal grammar`() {
        val unsupported = listOf(
            errorContextComponentValueType(),
        )
        val nameDecoder: ReaderDecoder<NameValue> = {
            fail("name decoder must not be used for unsupported value types")
        }
        val decoder: (
            ComponentDecoderContext,
            ValueType,
            UInt,
        ) -> Result<ComponentValueLiteral, WasmDecodeError> = { context, type, payloadSize ->
            ComponentValueLiteralDecoder(context, type, payloadSize, nameDecoder)
        }

        unsupported.forEach { type ->
            val context = componentDecoderContext()

            val actual = decoder(context, type, 0u)
            val expected = Err(ComponentValueDecodeError.UnsupportedValueType(type.toString()))

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `propagates reader errors`() {
        val error = WasmDecodeError.IOError(IllegalStateException("read failed"))
        val context = componentDecoderContext(
            reader = FakeWasmBinaryReader(fakeUByteReader = { Err(error) }),
        )
        val nameDecoder: ReaderDecoder<NameValue> = {
            fail("name decoder must not be used when reading fails")
        }

        assertWasmDecodeError(error) {
            ComponentValueLiteralDecoder(
                context = context,
                type = boolComponentValueType(),
                payloadSize = 1u,
                nameDecoder = nameDecoder,
            )
        }
    }

    private data class Case(
        val bytes: ByteArray,
        val type: ValueType,
        val expected: Result<ComponentValueLiteral, WasmDecodeError>,
    )
}

private fun bytes(vararg values: Int): ByteArray = ByteArray(values.size) { index ->
    values[index].toByte()
}
