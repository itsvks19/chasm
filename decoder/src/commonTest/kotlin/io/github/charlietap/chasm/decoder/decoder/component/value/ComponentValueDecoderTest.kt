package io.github.charlietap.chasm.decoder.decoder.component.value

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.component.ComponentValueLiteral
import io.github.charlietap.chasm.ast.component.ValueType
import io.github.charlietap.chasm.decoder.context.ComponentDecoderContext
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.error.ReaderDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import io.github.charlietap.chasm.fixture.ast.component.componentValue
import io.github.charlietap.chasm.fixture.ast.component.u8ComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.u8ComponentValueType
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentValueDecoderTest {

    @Test
    fun `can decode a component value`() {
        val context = componentDecoderContext(
            reader = BinaryReader(byteArrayOf(0x01, 0x55)),
        )

        val type = u8ComponentValueType()
        val valueTypeDecoder: ComponentDecoder<ValueType> = {
            Ok(type)
        }

        val literal = u8ComponentValueLiteral(value = 0x55u)
        val valueLiteralDecoder: (
            ComponentDecoderContext,
            ValueType,
            UInt,
        ) -> Result<ComponentValueLiteral, WasmDecodeError> = { scopedContext, _, _ ->
            scopedContext.reader.ubyte()
            Ok(literal)
        }

        val actual = ComponentValueDecoder(
            context = context,
            valueTypeDecoder = valueTypeDecoder,
            valueLiteralDecoder = valueLiteralDecoder,
        )
        val expected = Ok(componentValue(type = type, value = literal))

        assertEquals(expected, actual)
    }

    @Test
    fun `rejects a payload with trailing bytes`() {
        val context = componentDecoderContext(
            reader = BinaryReader(byteArrayOf(0x01, 0x55)),
        )

        val type = u8ComponentValueType()
        val valueTypeDecoder: ComponentDecoder<ValueType> = {
            Ok(type)
        }

        val literal = u8ComponentValueLiteral(value = 0x55u)
        val valueLiteralDecoder: (
            ComponentDecoderContext,
            ValueType,
            UInt,
        ) -> Result<ComponentValueLiteral, WasmDecodeError> = { _, _, _ ->
            Ok(literal)
        }

        val actual = ComponentValueDecoder(
            context = context,
            valueTypeDecoder = valueTypeDecoder,
            valueLiteralDecoder = valueLiteralDecoder,
        )
        val expected = Err(ReaderDecodeError.SizeMismatch(expectedSize = 1u, actualSize = 0u))

        assertEquals(expected, actual)
    }
}
