package io.github.charlietap.chasm.decoder.decoder.component.type

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.FunctionType
import io.github.charlietap.chasm.ast.component.TypeDefinition
import io.github.charlietap.chasm.ast.component.ValueType
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.fixture.ast.component.componentFunctionType
import io.github.charlietap.chasm.fixture.ast.component.componentTypeDefinition
import io.github.charlietap.chasm.fixture.ast.component.functionTypeDefinition
import io.github.charlietap.chasm.fixture.ast.component.instanceTypeDefinition
import io.github.charlietap.chasm.fixture.ast.component.resourceTypeDefinition
import io.github.charlietap.chasm.fixture.ast.component.u32ComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.valueTypeDefinition
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentTypeDefinitionDecoderTest {

    @Test
    fun `delegates each type definition to its decoder`() {
        val valueType = u32ComponentValueType()
        val definedValueTypeDecoder: ComponentDecoder<ValueType> = {
            Ok(valueType)
        }

        val functionType = componentFunctionType()
        val functionTypeDecoder: ComponentDecoder<FunctionType> = {
            Ok(functionType)
        }

        val componentType = componentTypeDefinition()
        val componentTypeDecoder: ComponentDecoder<TypeDefinition.Component> = {
            Ok(componentType)
        }

        val instanceType = instanceTypeDefinition()
        val instanceTypeDecoder: ComponentDecoder<TypeDefinition.Instance> = {
            Ok(instanceType)
        }

        val resourceType = resourceTypeDefinition()
        val resourceTypeDecoder: ComponentDecoder<TypeDefinition.Resource> = {
            Ok(resourceType)
        }

        val decoder: ComponentDecoder<TypeDefinition> = { context ->
            ComponentTypeDefinitionDecoder(
                context = context,
                definedValueTypeDecoder = definedValueTypeDecoder,
                functionTypeDecoder = functionTypeDecoder,
                componentTypeDecoder = componentTypeDecoder,
                instanceTypeDecoder = instanceTypeDecoder,
                resourceTypeDecoder = resourceTypeDecoder,
            )
        }

        val cases = listOf(
            Case(0x40, functionTypeDefinition(functionType)),
            Case(0x43, functionTypeDefinition(functionType)),
            Case(0x41, componentType),
            Case(0x42, instanceType),
            Case(0x3F, resourceType),
            Case(0x7F, valueTypeDefinition(valueType)),
        )

        cases.forEach { case ->
            val context = componentTypeDecoderContext(bytes(case.opcode))

            val actual = decoder(context)
            val expected = Ok(case.expected)

            assertEquals(expected, actual)
        }
    }

    private data class Case(
        val opcode: Int,
        val expected: TypeDefinition,
    )
}

private fun bytes(vararg values: Int): ByteArray = values.map(Int::toByte).toByteArray()
