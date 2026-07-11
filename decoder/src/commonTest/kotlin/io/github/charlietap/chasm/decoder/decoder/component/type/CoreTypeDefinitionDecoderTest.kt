package io.github.charlietap.chasm.decoder.decoder.component.type

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.CoreTypeDefinition
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.ComponentTypeDecodeError
import io.github.charlietap.chasm.fixture.ast.component.definedTypeCoreTypeDefinition
import io.github.charlietap.chasm.fixture.ast.component.moduleTypeCoreTypeDefinition
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.type.RecursiveType
import kotlin.test.Test
import kotlin.test.assertEquals

class CoreTypeDefinitionDecoderTest {

    @Test
    fun `delegates core module types to the module type decoder`() {
        val context = componentTypeDecoderContext(bytes(0x50))

        val recursiveType = functionRecursiveType()
        val recursiveTypeDecoder: Decoder<RecursiveType> = {
            Ok(recursiveType)
        }

        val moduleType = moduleTypeCoreTypeDefinition()
        val moduleTypeDecoder: ComponentDecoder<CoreTypeDefinition.ModuleType> = {
            Ok(moduleType)
        }

        val actual = CoreTypeDefinitionDecoder(
            context = context,
            recursiveTypeDecoder = recursiveTypeDecoder,
            moduleTypeDecoder = moduleTypeDecoder,
        )
        val expected = Ok(moduleType)

        assertEquals(expected, actual)
    }

    @Test
    fun `delegates ordinary and prefixed core types to the recursive type decoder`() {
        val recursiveType = functionRecursiveType()
        val recursiveTypeDecoder: Decoder<RecursiveType> = {
            Ok(recursiveType)
        }

        val moduleType = moduleTypeCoreTypeDefinition()
        val moduleTypeDecoder: ComponentDecoder<CoreTypeDefinition.ModuleType> = {
            Ok(moduleType)
        }

        val decoder: ComponentDecoder<CoreTypeDefinition> = { context ->
            CoreTypeDefinitionDecoder(
                context = context,
                recursiveTypeDecoder = recursiveTypeDecoder,
                moduleTypeDecoder = moduleTypeDecoder,
            )
        }

        val cases = listOf(
            bytes(0x60),
            bytes(0x00, 0x50),
        )

        cases.forEach { bytes ->
            val context = componentTypeDecoderContext(bytes)

            val actual = decoder(context)
            val expected = Ok(definedTypeCoreTypeDefinition(recursiveType))

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `rejects a malformed temporary core subtype prefix`() {
        val context = componentTypeDecoderContext(bytes(0x00, 0x60))

        val recursiveType = functionRecursiveType()
        val recursiveTypeDecoder: Decoder<RecursiveType> = {
            Ok(recursiveType)
        }

        val moduleType = moduleTypeCoreTypeDefinition()
        val moduleTypeDecoder: ComponentDecoder<CoreTypeDefinition.ModuleType> = {
            Ok(moduleType)
        }

        val actual = CoreTypeDefinitionDecoder(
            context = context,
            recursiveTypeDecoder = recursiveTypeDecoder,
            moduleTypeDecoder = moduleTypeDecoder,
        )
        val expected = Err(ComponentTypeDecodeError.InvalidCoreType(0x60u))

        assertEquals(expected, actual)
    }
}

private fun bytes(vararg values: Int): ByteArray = values.map(Int::toByte).toByteArray()
