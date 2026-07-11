package io.github.charlietap.chasm.decoder.decoder.component.type

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.Index.ComponentTypeIndex
import io.github.charlietap.chasm.ast.component.KeyType
import io.github.charlietap.chasm.ast.component.LabeledValueType
import io.github.charlietap.chasm.ast.component.ValueType
import io.github.charlietap.chasm.ast.component.VariantCase
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.ComponentVectorDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.error.ComponentTypeDecodeError
import io.github.charlietap.chasm.fixture.ast.component.boolComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.borrowComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.componentTypeIndex
import io.github.charlietap.chasm.fixture.ast.component.componentVariantCase
import io.github.charlietap.chasm.fixture.ast.component.enumComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.fixedLengthListComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.flagsComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.futureComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.labeledComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.listComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.mapComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.optionComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.ownComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.recordComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.resultComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.streamComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.stringComponentKeyType
import io.github.charlietap.chasm.fixture.ast.component.tupleComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.variantComponentValueType
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentDefinedValueTypeDecoderTest {

    @Test
    fun `can decode each defined value type`() {
        val valueType = boolComponentValueType()
        val primitiveValueTypeDecoder: ComponentDecoder<ValueType> = {
            Ok(valueType)
        }

        val labeledValueType = labeledComponentValueType(
            label = nameValue("value"),
            type = valueType,
        )
        val labeledValueTypeDecoder: ComponentDecoder<LabeledValueType> = {
            Ok(labeledValueType)
        }
        val labeledValueTypeVectorDecoder: ComponentVectorDecoder<LabeledValueType> = { _, _ ->
            Ok(Vector(listOf(labeledValueType)))
        }

        val variantCase = componentVariantCase(
            label = nameValue("case"),
            type = valueType,
        )
        val variantCaseDecoder: ComponentDecoder<VariantCase> = {
            Ok(variantCase)
        }
        val variantCaseVectorDecoder: ComponentVectorDecoder<VariantCase> = { _, _ ->
            Ok(Vector(listOf(variantCase)))
        }

        val valueTypeDecoder: ComponentDecoder<ValueType> = {
            Ok(valueType)
        }
        val valueTypeVectorDecoder: ComponentVectorDecoder<ValueType> = { _, _ ->
            Ok(Vector(listOf(valueType)))
        }

        val label = nameValue("label")
        val labelDecoder: ComponentDecoder<NameValue> = {
            Ok(label)
        }
        val labelVectorDecoder: ComponentVectorDecoder<NameValue> = { _, _ ->
            Ok(Vector(listOf(label)))
        }

        var optionalValues = emptyList<ValueType?>().iterator()
        val optionalValueTypeDecoder: ComponentDecoder<ValueType?> = {
            Ok(optionalValues.next())
        }

        val typeIndex = componentTypeIndex(3u)
        val typeIndexDecoder: ComponentDecoder<ComponentTypeIndex> = {
            Ok(typeIndex)
        }

        val keyType = stringComponentKeyType()
        val mapKeyDecoder: ComponentDecoder<KeyType> = {
            Ok(keyType)
        }

        val decoder: ComponentDecoder<ValueType> = { context ->
            ComponentDefinedValueTypeDecoder(
                context = context,
                primitiveValueTypeDecoder = primitiveValueTypeDecoder,
                labeledValueTypeDecoder = labeledValueTypeDecoder,
                labeledValueTypeVectorDecoder = labeledValueTypeVectorDecoder,
                variantCaseDecoder = variantCaseDecoder,
                variantCaseVectorDecoder = variantCaseVectorDecoder,
                valueTypeDecoder = valueTypeDecoder,
                valueTypeVectorDecoder = valueTypeVectorDecoder,
                labelDecoder = labelDecoder,
                labelVectorDecoder = labelVectorDecoder,
                optionalValueTypeDecoder = optionalValueTypeDecoder,
                typeIndexDecoder = typeIndexDecoder,
                mapKeyDecoder = mapKeyDecoder,
            )
        }

        val cases = listOf(
            Case(bytes(0x7F), valueType),
            Case(bytes(0x72), recordComponentValueType(listOf(labeledValueType))),
            Case(bytes(0x71), variantComponentValueType(listOf(variantCase))),
            Case(bytes(0x70), listComponentValueType(valueType)),
            Case(bytes(0x67, 0x04), fixedLengthListComponentValueType(valueType, 4u)),
            Case(bytes(0x6F), tupleComponentValueType(listOf(valueType))),
            Case(bytes(0x6E), flagsComponentValueType(listOf(label))),
            Case(bytes(0x6D), enumComponentValueType(listOf(label))),
            Case(bytes(0x6B), optionComponentValueType(valueType), listOf(valueType)),
            Case(bytes(0x6A), resultComponentValueType(valueType, null), listOf(valueType, null)),
            Case(bytes(0x69), ownComponentValueType(typeIndex)),
            Case(bytes(0x68), borrowComponentValueType(typeIndex)),
            Case(bytes(0x66), streamComponentValueType(null), listOf(null)),
            Case(bytes(0x65), futureComponentValueType(valueType), listOf(valueType)),
            Case(bytes(0x63), mapComponentValueType(keyType, valueType)),
        )

        cases.forEach { case ->
            val context = componentTypeDecoderContext(case.bytes)
            optionalValues = case.optionalValues.iterator()

            val actual = decoder(context)
            val expected = Ok(case.expected)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `rejects an unknown defined value type`() {
        val context = componentTypeDecoderContext(bytes(0x62))

        val actual = ComponentDefinedValueTypeDecoder(context)
        val expected = Err(ComponentTypeDecodeError.UnknownDefinedValueType(0x62u))

        assertEquals(expected, actual)
    }

    private data class Case(
        val bytes: ByteArray,
        val expected: ValueType,
        val optionalValues: List<ValueType?> = emptyList(),
    )
}

private fun bytes(vararg values: Int): ByteArray = values.map(Int::toByte).toByteArray()
