package io.github.charlietap.chasm.decoder.decoder.component.instance

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.ExportTarget
import io.github.charlietap.chasm.ast.component.Index.ComponentIndex
import io.github.charlietap.chasm.ast.component.InlineExport
import io.github.charlietap.chasm.ast.component.InstantiateArgument
import io.github.charlietap.chasm.ast.component.NameAttributes
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.decoder.ReaderDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.ComponentVectorDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.error.ComponentDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.assertWasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.fixture.ast.component.componentIndex
import io.github.charlietap.chasm.fixture.ast.component.componentInstanceIndex
import io.github.charlietap.chasm.fixture.ast.component.componentValueIndex
import io.github.charlietap.chasm.fixture.ast.component.inlineExport
import io.github.charlietap.chasm.fixture.ast.component.inlineExportsInstanceDefinition
import io.github.charlietap.chasm.fixture.ast.component.instanceExportTarget
import io.github.charlietap.chasm.fixture.ast.component.instantiateArgument
import io.github.charlietap.chasm.fixture.ast.component.instantiateInstanceDefinition
import io.github.charlietap.chasm.fixture.ast.component.nameAttributes
import io.github.charlietap.chasm.fixture.ast.component.valueExportTarget
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import kotlin.test.Test
import kotlin.test.assertEquals

class InstantiateArgumentDecoderTest {

    @Test
    fun `decodes a component instantiate argument`() {
        val name = nameValue("state")
        val target = valueExportTarget(index = componentValueIndex(3u))
        val context = componentDecoderContext()

        val nameValueDecoder: ReaderDecoder<NameValue> = { Ok(name) }
        val targetDecoder: ComponentDecoder<ExportTarget> = { Ok(target) }

        val actual = InstantiateArgumentDecoder(
            context = context,
            nameValueDecoder = nameValueDecoder,
            targetDecoder = targetDecoder,
        )

        val expected = Ok(instantiateArgument(name = name, target = target))

        assertEquals(expected, actual)
    }

    @Test
    fun `propagates a name decoder error without decoding a target`() {
        val error = WasmDecodeError.IOError(Exception())
        val unexpectedError = WasmDecodeError.IOError(Exception())
        val context = componentDecoderContext()

        val nameValueDecoder: ReaderDecoder<NameValue> = { Err(error) }
        val targetDecoder: ComponentDecoder<ExportTarget> = { Err(unexpectedError) }

        val actual = InstantiateArgumentDecoder(
            context = context,
            nameValueDecoder = nameValueDecoder,
            targetDecoder = targetDecoder,
        )

        val expected = Err(error)

        assertEquals(expected, actual)
    }
}

class InlineExportDecoderTest {

    @Test
    fun `decodes a component inline export`() {
        val name = nameAttributes(name = nameValue("service"))
        val target = instanceExportTarget(index = componentInstanceIndex(4u))
        val context = componentDecoderContext()

        val nameAttributesDecoder: ComponentDecoder<NameAttributes> = { Ok(name) }
        val targetDecoder: ComponentDecoder<ExportTarget> = { Ok(target) }

        val actual = InlineExportDecoder(
            context = context,
            nameAttributesDecoder = nameAttributesDecoder,
            targetDecoder = targetDecoder,
        )

        val expected = Ok(inlineExport(name = name, target = target))

        assertEquals(expected, actual)
    }

    @Test
    fun `propagates a name attributes error without decoding a target`() {
        val error = WasmDecodeError.IOError(Exception())
        val unexpectedError = WasmDecodeError.IOError(Exception())
        val context = componentDecoderContext()

        val nameAttributesDecoder: ComponentDecoder<NameAttributes> = { Err(error) }
        val targetDecoder: ComponentDecoder<ExportTarget> = { Err(unexpectedError) }

        val actual = InlineExportDecoder(
            context = context,
            nameAttributesDecoder = nameAttributesDecoder,
            targetDecoder = targetDecoder,
        )

        val expected = Err(error)

        assertEquals(expected, actual)
    }
}

class InstanceDefinitionDecoderTest {

    @Test
    fun `decodes a component instantiation`() {
        val componentIndex = componentIndex(2u)
        val argument = instantiateArgument(
            name = nameValue("state"),
            target = valueExportTarget(index = componentValueIndex(3u)),
        )
        val unexpectedError = WasmDecodeError.IOError(Exception())
        val reader = FakeUByteReader { Ok(0x00u) }
        val context = componentDecoderContext(reader)

        val componentIndexDecoder: ComponentDecoder<ComponentIndex> = { Ok(componentIndex) }
        val instantiateArgumentDecoder: ComponentDecoder<InstantiateArgument> = { Ok(argument) }
        val inlineExportDecoder: ComponentDecoder<InlineExport> = { Err(unexpectedError) }
        val instantiateArgumentVectorDecoder: ComponentVectorDecoder<InstantiateArgument> = { _, _ ->
            Ok(Vector(listOf(argument)))
        }
        val inlineExportVectorDecoder: ComponentVectorDecoder<InlineExport> = { _, _ -> Err(unexpectedError) }

        val actual = InstanceDefinitionDecoder(
            context = context,
            componentIndexDecoder = componentIndexDecoder,
            instantiateArgumentDecoder = instantiateArgumentDecoder,
            inlineExportDecoder = inlineExportDecoder,
            instantiateArgumentVectorDecoder = instantiateArgumentVectorDecoder,
            inlineExportVectorDecoder = inlineExportVectorDecoder,
        )

        val expected = Ok(
            instantiateInstanceDefinition(
                componentIndex = componentIndex,
                args = listOf(argument),
            ),
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `decodes component inline exports`() {
        val export = inlineExport(
            name = nameAttributes(name = nameValue("service")),
            target = instanceExportTarget(index = componentInstanceIndex(4u)),
        )
        val unexpectedError = WasmDecodeError.IOError(Exception())
        val reader = FakeUByteReader { Ok(0x01u) }
        val context = componentDecoderContext(reader)

        val componentIndexDecoder: ComponentDecoder<ComponentIndex> = { Err(unexpectedError) }
        val instantiateArgumentDecoder: ComponentDecoder<InstantiateArgument> = { Err(unexpectedError) }
        val inlineExportDecoder: ComponentDecoder<InlineExport> = { Ok(export) }
        val instantiateArgumentVectorDecoder: ComponentVectorDecoder<InstantiateArgument> = { _, _ ->
            Err(unexpectedError)
        }
        val inlineExportVectorDecoder: ComponentVectorDecoder<InlineExport> = { _, _ ->
            Ok(Vector(listOf(export)))
        }

        val actual = InstanceDefinitionDecoder(
            context = context,
            componentIndexDecoder = componentIndexDecoder,
            instantiateArgumentDecoder = instantiateArgumentDecoder,
            inlineExportDecoder = inlineExportDecoder,
            instantiateArgumentVectorDecoder = instantiateArgumentVectorDecoder,
            inlineExportVectorDecoder = inlineExportVectorDecoder,
        )

        val expected = Ok(inlineExportsInstanceDefinition(exports = listOf(export)))

        assertEquals(expected, actual)
    }

    @Test
    fun `rejects an unknown component instance expression`() {
        val unexpectedError = WasmDecodeError.IOError(Exception())
        val reader = FakeUByteReader { Ok(0x02u) }
        val context = componentDecoderContext(reader)

        val componentIndexDecoder: ComponentDecoder<ComponentIndex> = { Err(unexpectedError) }
        val instantiateArgumentDecoder: ComponentDecoder<InstantiateArgument> = { Err(unexpectedError) }
        val inlineExportDecoder: ComponentDecoder<InlineExport> = { Err(unexpectedError) }
        val instantiateArgumentVectorDecoder: ComponentVectorDecoder<InstantiateArgument> = { _, _ ->
            Err(unexpectedError)
        }
        val inlineExportVectorDecoder: ComponentVectorDecoder<InlineExport> = { _, _ -> Err(unexpectedError) }

        val actual = InstanceDefinitionDecoder(
            context = context,
            componentIndexDecoder = componentIndexDecoder,
            instantiateArgumentDecoder = instantiateArgumentDecoder,
            inlineExportDecoder = inlineExportDecoder,
            instantiateArgumentVectorDecoder = instantiateArgumentVectorDecoder,
            inlineExportVectorDecoder = inlineExportVectorDecoder,
        )

        val expected = Err(ComponentDecodeError.UnknownInstanceExpression(0x02u))

        assertEquals(expected, actual)
    }

    @Test
    fun `propagates a component instance expression reader error`() {
        val error = WasmDecodeError.IOError(Exception())
        val unexpectedError = WasmDecodeError.IOError(Exception())
        val reader = FakeUByteReader { Err(error) }
        val context = componentDecoderContext(reader)

        val componentIndexDecoder: ComponentDecoder<ComponentIndex> = { Err(unexpectedError) }
        val instantiateArgumentDecoder: ComponentDecoder<InstantiateArgument> = { Err(unexpectedError) }
        val inlineExportDecoder: ComponentDecoder<InlineExport> = { Err(unexpectedError) }
        val instantiateArgumentVectorDecoder: ComponentVectorDecoder<InstantiateArgument> = { _, _ ->
            Err(unexpectedError)
        }
        val inlineExportVectorDecoder: ComponentVectorDecoder<InlineExport> = { _, _ -> Err(unexpectedError) }

        assertWasmDecodeError(error) {
            InstanceDefinitionDecoder(
                context = context,
                componentIndexDecoder = componentIndexDecoder,
                instantiateArgumentDecoder = instantiateArgumentDecoder,
                inlineExportDecoder = inlineExportDecoder,
                instantiateArgumentVectorDecoder = instantiateArgumentVectorDecoder,
                inlineExportVectorDecoder = inlineExportVectorDecoder,
            )
        }
    }
}
