package io.github.charlietap.chasm.decoder.decoder.component.instance

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.CoreExport
import io.github.charlietap.chasm.ast.component.CoreExportTarget
import io.github.charlietap.chasm.ast.component.CoreInstantiateArgument
import io.github.charlietap.chasm.ast.component.Index.ComponentModuleIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentModuleInstanceIndex
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
import io.github.charlietap.chasm.fixture.ast.component.componentModuleIndex
import io.github.charlietap.chasm.fixture.ast.component.componentModuleInstanceIndex
import io.github.charlietap.chasm.fixture.ast.component.coreExport
import io.github.charlietap.chasm.fixture.ast.component.coreInstantiateArgument
import io.github.charlietap.chasm.fixture.ast.component.globalCoreExportTarget
import io.github.charlietap.chasm.fixture.ast.component.inlineExportsCoreInstanceDefinition
import io.github.charlietap.chasm.fixture.ast.component.instantiateCoreInstanceDefinition
import io.github.charlietap.chasm.fixture.ast.module.globalIndex
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import kotlin.test.Test
import kotlin.test.assertEquals

class CoreInstantiateArgumentDecoderTest {

    @Test
    fun `decodes a core instantiate argument`() {
        val name = nameValue("host")
        val index = componentModuleInstanceIndex(3u)
        val reader = FakeUByteReader { Ok(0x12u) }
        val context = componentDecoderContext(reader)

        val nameValueDecoder: ReaderDecoder<NameValue> = { Ok(name) }
        val instanceIndexDecoder: ComponentDecoder<ComponentModuleInstanceIndex> = { Ok(index) }

        val actual = CoreInstantiateArgumentDecoder(
            context = context,
            nameValueDecoder = nameValueDecoder,
            instanceIndexDecoder = instanceIndexDecoder,
        )

        val expected = Ok(coreInstantiateArgument(name = name, instanceIndex = index))

        assertEquals(expected, actual)
    }

    @Test
    fun `rejects a non-instance core sort marker`() {
        val name = nameValue("host")
        val unexpectedError = WasmDecodeError.IOError(Exception())
        val reader = FakeUByteReader { Ok(0x11u) }
        val context = componentDecoderContext(reader)

        val nameValueDecoder: ReaderDecoder<NameValue> = { Ok(name) }
        val instanceIndexDecoder: ComponentDecoder<ComponentModuleInstanceIndex> = { Err(unexpectedError) }

        val actual = CoreInstantiateArgumentDecoder(
            context = context,
            nameValueDecoder = nameValueDecoder,
            instanceIndexDecoder = instanceIndexDecoder,
        )

        val expected = Err(ComponentDecodeError.InvalidMarker(0x12u, 0x11u))

        assertEquals(expected, actual)
    }

    @Test
    fun `propagates a marker reader error`() {
        val name = nameValue("host")
        val error = WasmDecodeError.IOError(Exception())
        val unexpectedError = WasmDecodeError.IOError(Exception())
        val reader = FakeUByteReader { Err(error) }
        val context = componentDecoderContext(reader)

        val nameValueDecoder: ReaderDecoder<NameValue> = { Ok(name) }
        val instanceIndexDecoder: ComponentDecoder<ComponentModuleInstanceIndex> = { Err(unexpectedError) }

        assertWasmDecodeError(error) {
            CoreInstantiateArgumentDecoder(
                context = context,
                nameValueDecoder = nameValueDecoder,
                instanceIndexDecoder = instanceIndexDecoder,
            )
        }
    }
}

class CoreInlineExportDecoderTest {

    @Test
    fun `decodes a core inline export`() {
        val name = nameValue("memory")
        val target = globalCoreExportTarget(index = globalIndex(4u))
        val context = componentDecoderContext()

        val nameValueDecoder: ReaderDecoder<NameValue> = { Ok(name) }
        val targetDecoder: ComponentDecoder<CoreExportTarget> = { Ok(target) }

        val actual = CoreInlineExportDecoder(
            context = context,
            nameValueDecoder = nameValueDecoder,
            targetDecoder = targetDecoder,
        )

        val expected = Ok(coreExport(name = name, target = target))

        assertEquals(expected, actual)
    }

    @Test
    fun `propagates a name decoder error without decoding a target`() {
        val error = WasmDecodeError.IOError(Exception())
        val unexpectedError = WasmDecodeError.IOError(Exception())
        val context = componentDecoderContext()

        val nameValueDecoder: ReaderDecoder<NameValue> = { Err(error) }
        val targetDecoder: ComponentDecoder<CoreExportTarget> = { Err(unexpectedError) }

        val actual = CoreInlineExportDecoder(
            context = context,
            nameValueDecoder = nameValueDecoder,
            targetDecoder = targetDecoder,
        )

        val expected = Err(error)

        assertEquals(expected, actual)
    }
}

class CoreInstanceDefinitionDecoderTest {

    @Test
    fun `decodes a core module instantiation`() {
        val moduleIndex = componentModuleIndex(2u)
        val argument = coreInstantiateArgument(
            name = nameValue("host"),
            instanceIndex = componentModuleInstanceIndex(3u),
        )
        val unexpectedError = WasmDecodeError.IOError(Exception())
        val reader = FakeUByteReader { Ok(0x00u) }
        val context = componentDecoderContext(reader)

        val moduleIndexDecoder: ComponentDecoder<ComponentModuleIndex> = { Ok(moduleIndex) }
        val instantiateArgumentDecoder: ComponentDecoder<CoreInstantiateArgument> = { Ok(argument) }
        val inlineExportDecoder: ComponentDecoder<CoreExport> = { Err(unexpectedError) }
        val instantiateArgumentVectorDecoder: ComponentVectorDecoder<CoreInstantiateArgument> = { _, _ ->
            Ok(Vector(listOf(argument)))
        }
        val inlineExportVectorDecoder: ComponentVectorDecoder<CoreExport> = { _, _ -> Err(unexpectedError) }

        val actual = CoreInstanceDefinitionDecoder(
            context = context,
            moduleIndexDecoder = moduleIndexDecoder,
            instantiateArgumentDecoder = instantiateArgumentDecoder,
            inlineExportDecoder = inlineExportDecoder,
            instantiateArgumentVectorDecoder = instantiateArgumentVectorDecoder,
            inlineExportVectorDecoder = inlineExportVectorDecoder,
        )

        val expected = Ok(
            instantiateCoreInstanceDefinition(
                moduleIndex = moduleIndex,
                args = listOf(argument),
            ),
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `decodes core inline exports`() {
        val export = coreExport(
            name = nameValue("g"),
            target = globalCoreExportTarget(index = globalIndex(4u)),
        )
        val unexpectedError = WasmDecodeError.IOError(Exception())
        val reader = FakeUByteReader { Ok(0x01u) }
        val context = componentDecoderContext(reader)

        val moduleIndexDecoder: ComponentDecoder<ComponentModuleIndex> = { Err(unexpectedError) }
        val instantiateArgumentDecoder: ComponentDecoder<CoreInstantiateArgument> = { Err(unexpectedError) }
        val inlineExportDecoder: ComponentDecoder<CoreExport> = { Ok(export) }
        val instantiateArgumentVectorDecoder: ComponentVectorDecoder<CoreInstantiateArgument> = { _, _ ->
            Err(unexpectedError)
        }
        val inlineExportVectorDecoder: ComponentVectorDecoder<CoreExport> = { _, _ ->
            Ok(Vector(listOf(export)))
        }

        val actual = CoreInstanceDefinitionDecoder(
            context = context,
            moduleIndexDecoder = moduleIndexDecoder,
            instantiateArgumentDecoder = instantiateArgumentDecoder,
            inlineExportDecoder = inlineExportDecoder,
            instantiateArgumentVectorDecoder = instantiateArgumentVectorDecoder,
            inlineExportVectorDecoder = inlineExportVectorDecoder,
        )

        val expected = Ok(inlineExportsCoreInstanceDefinition(exports = listOf(export)))

        assertEquals(expected, actual)
    }

    @Test
    fun `rejects an unknown core instance expression`() {
        val unexpectedError = WasmDecodeError.IOError(Exception())
        val reader = FakeUByteReader { Ok(0x02u) }
        val context = componentDecoderContext(reader)

        val moduleIndexDecoder: ComponentDecoder<ComponentModuleIndex> = { Err(unexpectedError) }
        val instantiateArgumentDecoder: ComponentDecoder<CoreInstantiateArgument> = { Err(unexpectedError) }
        val inlineExportDecoder: ComponentDecoder<CoreExport> = { Err(unexpectedError) }
        val instantiateArgumentVectorDecoder: ComponentVectorDecoder<CoreInstantiateArgument> = { _, _ ->
            Err(unexpectedError)
        }
        val inlineExportVectorDecoder: ComponentVectorDecoder<CoreExport> = { _, _ -> Err(unexpectedError) }

        val actual = CoreInstanceDefinitionDecoder(
            context = context,
            moduleIndexDecoder = moduleIndexDecoder,
            instantiateArgumentDecoder = instantiateArgumentDecoder,
            inlineExportDecoder = inlineExportDecoder,
            instantiateArgumentVectorDecoder = instantiateArgumentVectorDecoder,
            inlineExportVectorDecoder = inlineExportVectorDecoder,
        )

        val expected = Err(ComponentDecodeError.UnknownCoreInstanceExpression(0x02u))

        assertEquals(expected, actual)
    }

    @Test
    fun `propagates a core instance expression reader error`() {
        val error = WasmDecodeError.IOError(Exception())
        val unexpectedError = WasmDecodeError.IOError(Exception())
        val reader = FakeUByteReader { Err(error) }
        val context = componentDecoderContext(reader)

        val moduleIndexDecoder: ComponentDecoder<ComponentModuleIndex> = { Err(unexpectedError) }
        val instantiateArgumentDecoder: ComponentDecoder<CoreInstantiateArgument> = { Err(unexpectedError) }
        val inlineExportDecoder: ComponentDecoder<CoreExport> = { Err(unexpectedError) }
        val instantiateArgumentVectorDecoder: ComponentVectorDecoder<CoreInstantiateArgument> = { _, _ ->
            Err(unexpectedError)
        }
        val inlineExportVectorDecoder: ComponentVectorDecoder<CoreExport> = { _, _ -> Err(unexpectedError) }

        assertWasmDecodeError(error) {
            CoreInstanceDefinitionDecoder(
                context = context,
                moduleIndexDecoder = moduleIndexDecoder,
                instantiateArgumentDecoder = instantiateArgumentDecoder,
                inlineExportDecoder = inlineExportDecoder,
                instantiateArgumentVectorDecoder = instantiateArgumentVectorDecoder,
                inlineExportVectorDecoder = inlineExportVectorDecoder,
            )
        }
    }
}
