package io.github.charlietap.chasm.decoder

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.context.scope.ScopedDecoder
import io.github.charlietap.chasm.decoder.context.scope.SectionScope
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.factory.BinaryReaderFactory
import io.github.charlietap.chasm.decoder.decoder.magic.MagicNumberValidator
import io.github.charlietap.chasm.decoder.error.ModuleDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeExhaustedReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.section.ImportSection
import io.github.charlietap.chasm.decoder.section.Section
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.decoder.section.SectionType
import io.github.charlietap.chasm.decoder.section.TypeSection
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.module.type
import io.github.charlietap.chasm.fixture.config.moduleConfig
import io.github.charlietap.chasm.fixture.type.definedType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class WasmModuleDecoderTest {

    @Test
    fun `can decode a wasm module with no sections`() {

        val config = moduleConfig()
        val sourceReader = FakeSourceReader()
        val reader = FakeExhaustedReader { Ok(true) }
        val version = Version.One

        val readerFactory: BinaryReaderFactory = { _sourceReader ->
            assertEquals(sourceReader, _sourceReader)
            reader
        }

        val magicNumberValidator: MagicNumberValidator = { _reader ->
            assertEquals(reader, _reader)
            Ok(Unit)
        }

        val versionDecoder: Decoder<Version> = { context ->
            assertEquals(reader, context.reader)
            Ok(version)
        }

        val sectionTypeDecoder: Decoder<SectionType> = { _ ->
            fail("Section type decoder should not be called in this scenario")
        }

        val sectionDecoder: Decoder<Section> = { _ ->
            fail("Section decoder should not be called in this scenario")
        }

        val scope: ScopedDecoder<Pair<SectionSize, SectionType>, Section> = { _, _, _ ->
            fail("Scope should not be called in this scenario")
        }

        val expected = module(
            version = version,
        )

        val actual = WasmModuleDecoder(
            config = config,
            source = sourceReader,
            readerFactory = readerFactory,
            magicNumberValidator = magicNumberValidator,
            versionDecoder = versionDecoder,
            sectionTypeDecoder = sectionTypeDecoder,
            sectionDecoder = sectionDecoder,
            scope = scope,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can reuse a module context without leaking decode state`() {
        val exhausted = sequenceOf(false, true, false, true).iterator()
        val reader = FakeWasmBinaryReader(
            fakeUIntReader = { Ok(0u) },
            fakeExhaustedReader = { Ok(exhausted.next()) },
        )
        val context = decoderContext(reader)

        val firstType = type(idx = Index.TypeIndex(0u))
        val secondType = type(idx = Index.TypeIndex(1u))
        val types = sequenceOf(firstType, secondType).iterator()

        val firstDefinedType = definedType(typeIndex = 0)
        val secondDefinedType = definedType(typeIndex = 1)
        val definedTypes = sequenceOf(firstDefinedType, secondDefinedType).iterator()

        val magicNumberValidator: MagicNumberValidator = { Ok(Unit) }
        val versionDecoder: Decoder<Version> = { Ok(Version.One) }
        val sectionTypeDecoder: Decoder<SectionType> = { Ok(SectionType.Type) }
        val sectionDecoder: Decoder<Section> = { context ->
            context.types += types.next()
            context.definedTypes += definedTypes.next()
            Ok(TypeSection(context.types, context.definedTypes))
        }

        fun decode() = WasmModuleDecoder(
            context = context,
            magicNumberValidator = magicNumberValidator,
            versionDecoder = versionDecoder,
            sectionTypeDecoder = sectionTypeDecoder,
            sectionDecoder = sectionDecoder,
            scope = ::SectionScope,
        )

        val first = decode()
        assertEquals(emptyList(), context.types)
        assertEquals(emptyList(), context.definedTypes)

        val second = decode()

        assertEquals(
            Ok(module(types = listOf(firstType), definedTypes = listOf(firstDefinedType))),
            first,
        )
        assertEquals(
            Ok(module(types = listOf(secondType), definedTypes = listOf(secondDefinedType))),
            second,
        )
        assertEquals(emptyList(), context.types)
        assertEquals(emptyList(), context.definedTypes)
    }

    @Test
    fun `resets a reusable module context after a decode error`() {
        val context = decoderContext()
        val error = WasmDecodeError.IOError(Exception())

        val versionDecoder: Decoder<Version> = { decoderContext ->
            decoderContext.types += type()
            decoderContext.index = 1
            Err(error)
        }
        val sectionTypeDecoder: Decoder<SectionType> = {
            fail("Section type decoder should not be called in this scenario")
        }
        val sectionDecoder: Decoder<Section> = {
            fail("Section decoder should not be called in this scenario")
        }
        val scope: ScopedDecoder<Pair<SectionSize, SectionType>, Section> = { _, _, _ ->
            fail("Scope should not be called in this scenario")
        }

        val actual = WasmModuleDecoder(
            context = context,
            magicNumberValidator = { Ok(Unit) },
            versionDecoder = versionDecoder,
            sectionTypeDecoder = sectionTypeDecoder,
            sectionDecoder = sectionDecoder,
            scope = scope,
        )

        assertEquals(Err(error), actual)
        assertEquals(emptyList(), context.types)
        assertEquals(0, context.index)
    }

    @Test
    fun `malformed error is returned if section order is unexpected`() {

        val config = moduleConfig()
        val sourceReader = FakeSourceReader()
        val reader = FakeWasmBinaryReader(
            fakeUIntReader = { Ok(0u) },
            fakeExhaustedReader = { Ok(false) },
        )
        val version = Version.One

        val readerFactory: BinaryReaderFactory = { _sourceReader ->
            assertEquals(sourceReader, _sourceReader)
            reader
        }

        val magicNumberValidator: MagicNumberValidator = { _reader ->
            assertEquals(reader, _reader)
            Ok(Unit)
        }

        val versionDecoder: Decoder<Version> = { context ->
            assertEquals(reader, context.reader)
            Ok(version)
        }

        val sectionTypeSequence = sequenceOf(SectionType.Import, SectionType.Type).iterator()
        val sectionTypeDecoder: Decoder<SectionType> = { _ ->
            Ok(sectionTypeSequence.next())
        }

        val sectionDecoder: Decoder<Section> = { _ ->
            Ok(ImportSection(emptyList()))
        }

        val scope: ScopedDecoder<Pair<SectionSize, SectionType>, Section> = { context, _, decoder ->
            decoder(context)
        }

        val expected = Err(ModuleDecodeError.ModuleMalformed)

        val actual = WasmModuleDecoder(
            config = config,
            source = sourceReader,
            readerFactory = readerFactory,
            magicNumberValidator = magicNumberValidator,
            versionDecoder = versionDecoder,
            sectionTypeDecoder = sectionTypeDecoder,
            sectionDecoder = sectionDecoder,
            scope = scope,
        )

        assertEquals(expected, actual)
    }
}
