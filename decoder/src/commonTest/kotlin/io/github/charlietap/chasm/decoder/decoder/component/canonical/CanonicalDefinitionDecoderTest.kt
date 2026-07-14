package io.github.charlietap.chasm.decoder.decoder.component.canonical

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.CanonicalDefinition
import io.github.charlietap.chasm.ast.component.CanonicalOption
import io.github.charlietap.chasm.ast.component.Index.ComponentFunctionIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentTypeIndex
import io.github.charlietap.chasm.ast.component.ValueType
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.ComponentVectorDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.error.ComponentCanonicalDecodeError
import io.github.charlietap.chasm.decoder.error.ComponentDecodeError
import io.github.charlietap.chasm.decoder.fixture.assertWasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.ast.component.asyncCanonicalOption
import io.github.charlietap.chasm.fixture.ast.component.backpressureDecCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.backpressureIncCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.backpressureSetCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.boolComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.componentFunctionIndex
import io.github.charlietap.chasm.fixture.ast.component.componentTypeIndex
import io.github.charlietap.chasm.fixture.ast.component.contextGetCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.contextSetCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.errorContextDebugMessageCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.errorContextDropCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.errorContextNewCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.futureCancelReadCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.futureCancelWriteCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.futureDropReadableCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.futureDropWritableCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.futureNewCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.futureReadCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.futureWriteCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.liftCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.lowerCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.resourceDropCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.resourceNewCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.resourceRepCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.streamCancelReadCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.streamCancelWriteCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.streamDropReadableCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.streamDropWritableCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.streamNewCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.streamReadCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.streamWriteCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.stringComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.subtaskCancelCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.subtaskDropCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.taskCancelCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.taskReturnCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.threadAvailableParallelismCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.threadIndexCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.threadNewIndirectCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.threadResumeLaterCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.threadSpawnIndirectCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.threadSpawnRefCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.threadSuspendCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.threadSuspendThenPromoteCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.threadSuspendThenResumeCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.threadYieldCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.threadYieldThenPromoteCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.threadYieldThenResumeCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.waitableJoinCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.waitableSetDropCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.waitableSetNewCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.waitableSetPollCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.waitableSetWaitCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.module.functionIndex
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex
import io.github.charlietap.chasm.fixture.ast.module.tableIndex
import io.github.charlietap.chasm.fixture.type.i32ValueType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail
import io.github.charlietap.chasm.ast.module.Index.FunctionIndex as ModuleFunctionIndex
import io.github.charlietap.chasm.ast.module.Index.MemoryIndex as ModuleMemoryIndex
import io.github.charlietap.chasm.ast.module.Index.TableIndex as ModuleTableIndex
import io.github.charlietap.chasm.type.ValueType as CoreValueType

class CanonicalDefinitionDecoderTest {

    @Test
    fun `decodes every canonical definition`() {
        val moduleFunctionIndex = functionIndex(index = 1u)
        val componentFunctionIndex = componentFunctionIndex(index = 2u)
        val componentTypeIndex = componentTypeIndex(index = 3u)
        val moduleMemoryIndex = memoryIndex(index = 4u)
        val moduleTableIndex = tableIndex(index = 5u)
        val options = listOf(asyncCanonicalOption())
        val coreValueType = i32ValueType()
        val resultType = boolComponentValueType()
        val cases = listOf(
            bytes(0x00, 0x00) to liftCanonicalDefinition(
                functionIndex = moduleFunctionIndex,
                options = options,
                typeIndex = componentTypeIndex,
            ),
            bytes(0x01, 0x00) to lowerCanonicalDefinition(
                functionIndex = componentFunctionIndex,
                options = options,
            ),
            bytes(0x02) to resourceNewCanonicalDefinition(
                typeIndex = componentTypeIndex,
            ),
            bytes(0x03) to resourceDropCanonicalDefinition(
                typeIndex = componentTypeIndex,
            ),
            bytes(0x04) to resourceRepCanonicalDefinition(
                typeIndex = componentTypeIndex,
            ),
            bytes(0x05) to taskCancelCanonicalDefinition(),
            bytes(0x06) to subtaskCancelCanonicalDefinition(async = true),
            bytes(0x08) to backpressureSetCanonicalDefinition(),
            bytes(0x09) to taskReturnCanonicalDefinition(
                result = resultType,
                options = options,
            ),
            bytes(0x0A, 0x04) to contextGetCanonicalDefinition(
                type = coreValueType,
                index = 4u,
            ),
            bytes(0x0B, 0x05) to contextSetCanonicalDefinition(
                type = coreValueType,
                index = 5u,
            ),
            bytes(0x0C) to threadYieldCanonicalDefinition(cancellable = true),
            bytes(0x0D) to subtaskDropCanonicalDefinition(),
            bytes(0x0E) to streamNewCanonicalDefinition(
                typeIndex = componentTypeIndex,
            ),
            bytes(0x0F) to streamReadCanonicalDefinition(
                typeIndex = componentTypeIndex,
                options = options,
            ),
            bytes(0x10) to streamWriteCanonicalDefinition(
                typeIndex = componentTypeIndex,
                options = options,
            ),
            bytes(0x11) to streamCancelReadCanonicalDefinition(
                typeIndex = componentTypeIndex,
                async = true,
            ),
            bytes(0x12) to streamCancelWriteCanonicalDefinition(
                typeIndex = componentTypeIndex,
                async = true,
            ),
            bytes(0x13) to streamDropReadableCanonicalDefinition(
                typeIndex = componentTypeIndex,
            ),
            bytes(0x14) to streamDropWritableCanonicalDefinition(
                typeIndex = componentTypeIndex,
            ),
            bytes(0x15) to futureNewCanonicalDefinition(
                typeIndex = componentTypeIndex,
            ),
            bytes(0x16) to futureReadCanonicalDefinition(
                typeIndex = componentTypeIndex,
                options = options,
            ),
            bytes(0x17) to futureWriteCanonicalDefinition(
                typeIndex = componentTypeIndex,
                options = options,
            ),
            bytes(0x18) to futureCancelReadCanonicalDefinition(
                typeIndex = componentTypeIndex,
                async = true,
            ),
            bytes(0x19) to futureCancelWriteCanonicalDefinition(
                typeIndex = componentTypeIndex,
                async = true,
            ),
            bytes(0x1A) to futureDropReadableCanonicalDefinition(
                typeIndex = componentTypeIndex,
            ),
            bytes(0x1B) to futureDropWritableCanonicalDefinition(
                typeIndex = componentTypeIndex,
            ),
            bytes(0x1C) to errorContextNewCanonicalDefinition(
                options = options,
            ),
            bytes(0x1D) to errorContextDebugMessageCanonicalDefinition(
                options = options,
            ),
            bytes(0x1E) to errorContextDropCanonicalDefinition(),
            bytes(0x1F) to waitableSetNewCanonicalDefinition(),
            bytes(0x20) to waitableSetWaitCanonicalDefinition(
                cancellable = true,
                memoryIndex = moduleMemoryIndex,
            ),
            bytes(0x21) to waitableSetPollCanonicalDefinition(
                cancellable = true,
                memoryIndex = moduleMemoryIndex,
            ),
            bytes(0x22) to waitableSetDropCanonicalDefinition(),
            bytes(0x23) to waitableJoinCanonicalDefinition(),
            bytes(0x24) to backpressureIncCanonicalDefinition(),
            bytes(0x25) to backpressureDecCanonicalDefinition(),
            bytes(0x26) to threadIndexCanonicalDefinition(),
            bytes(0x27) to threadNewIndirectCanonicalDefinition(
                typeIndex = componentTypeIndex,
                tableIndex = moduleTableIndex,
            ),
            bytes(0x28) to threadResumeLaterCanonicalDefinition(),
            bytes(0x29) to threadSuspendCanonicalDefinition(cancellable = true),
            bytes(0x2A) to threadSuspendThenResumeCanonicalDefinition(cancellable = true),
            bytes(0x2B) to threadYieldThenResumeCanonicalDefinition(cancellable = true),
            bytes(0x2C) to threadSuspendThenPromoteCanonicalDefinition(cancellable = true),
            bytes(0x2D) to threadYieldThenPromoteCanonicalDefinition(cancellable = true),
            bytes(0x40) to threadSpawnRefCanonicalDefinition(
                shared = true,
                typeIndex = componentTypeIndex,
            ),
            bytes(0x41) to threadSpawnIndirectCanonicalDefinition(
                shared = true,
                typeIndex = componentTypeIndex,
                tableIndex = moduleTableIndex,
            ),
            bytes(0x42) to threadAvailableParallelismCanonicalDefinition(shared = true),
        )

        val moduleFunctionIndexDecoder: ComponentDecoder<ModuleFunctionIndex> = { Ok(moduleFunctionIndex) }
        val componentFunctionIndexDecoder: ComponentDecoder<ComponentFunctionIndex> = { Ok(componentFunctionIndex) }
        val componentTypeIndexDecoder: ComponentDecoder<ComponentTypeIndex> = { Ok(componentTypeIndex) }
        val moduleMemoryIndexDecoder: ComponentDecoder<ModuleMemoryIndex> = { Ok(moduleMemoryIndex) }
        val moduleTableIndexDecoder: ComponentDecoder<ModuleTableIndex> = { Ok(moduleTableIndex) }
        val optionVectorDecoder: ComponentVectorDecoder<CanonicalOption> = { _, _ -> Ok(Vector(options)) }
        val coreValueTypeDecoder: ComponentDecoder<CoreValueType> = { Ok(coreValueType) }
        val resultListDecoder: ComponentDecoder<ValueType?> = { Ok(resultType) }
        val asyncFlagDecoder: ComponentDecoder<Boolean> = { Ok(true) }
        val cancellableFlagDecoder: ComponentDecoder<Boolean> = { Ok(true) }
        val sharedFlagDecoder: ComponentDecoder<Boolean> = { Ok(true) }
        val decoder: ComponentDecoder<CanonicalDefinition> = { context ->
            CanonicalDefinitionDecoder(
                context = context,
                moduleFunctionIndexDecoder = moduleFunctionIndexDecoder,
                componentFunctionIndexDecoder = componentFunctionIndexDecoder,
                componentTypeIndexDecoder = componentTypeIndexDecoder,
                moduleMemoryIndexDecoder = moduleMemoryIndexDecoder,
                moduleTableIndexDecoder = moduleTableIndexDecoder,
                canonicalOptionDecoder = neverCanonicalOptionDecoder,
                optionVectorDecoder = optionVectorDecoder,
                coreValueTypeDecoder = coreValueTypeDecoder,
                resultListDecoder = resultListDecoder,
                asyncFlagDecoder = asyncFlagDecoder,
                cancellableFlagDecoder = cancellableFlagDecoder,
                sharedFlagDecoder = sharedFlagDecoder,
            )
        }

        cases.forEach { (encoded, definition) ->
            val context = componentDecoderContext(BinaryReader(encoded))

            val actual = decoder(context)

            val expected = Ok(definition)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `lower invokes only its dependencies`() {
        val context = componentDecoderContext(BinaryReader(bytes(0x01, 0x00)))
        val functionIndex = componentFunctionIndex(index = 9u)
        val options = listOf(asyncCanonicalOption())
        val componentFunctionIndexDecoder: ComponentDecoder<ComponentFunctionIndex> = { Ok(functionIndex) }
        val optionVectorDecoder: ComponentVectorDecoder<CanonicalOption> = { _, _ -> Ok(Vector(options)) }
        val decoder: ComponentDecoder<CanonicalDefinition> = { decoderContext ->
            CanonicalDefinitionDecoder(
                context = decoderContext,
                moduleFunctionIndexDecoder = neverModuleFunctionIndexDecoder,
                componentFunctionIndexDecoder = componentFunctionIndexDecoder,
                componentTypeIndexDecoder = neverComponentTypeIndexDecoder,
                moduleMemoryIndexDecoder = neverModuleMemoryIndexDecoder,
                moduleTableIndexDecoder = neverModuleTableIndexDecoder,
                canonicalOptionDecoder = neverCanonicalOptionDecoder,
                optionVectorDecoder = optionVectorDecoder,
                coreValueTypeDecoder = neverCoreValueTypeDecoder,
                resultListDecoder = neverResultListDecoder,
                asyncFlagDecoder = neverFlagDecoder,
                cancellableFlagDecoder = neverFlagDecoder,
                sharedFlagDecoder = neverFlagDecoder,
            )
        }

        val actual = decoder(context)

        val expected = Ok(lowerCanonicalDefinition(functionIndex = functionIndex, options = options))
        assertEquals(expected, actual)
    }

    @Test
    fun `rejects invalid lift and lower function sort markers before invoking dependencies`() {
        listOf(0x00, 0x01).forEach { opcode ->
            val context = componentDecoderContext(BinaryReader(bytes(opcode, 0x01)))

            val actual = neverDependencyDecoder(context)

            val expected = Err(ComponentDecodeError.InvalidMarker(0x00u, 0x01u))
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `rejects an unknown canonical definition without invoking dependencies`() {
        val context = componentDecoderContext(BinaryReader(bytes(0x07)))

        val actual = neverDependencyDecoder(context)

        val expected = Err(ComponentCanonicalDecodeError.UnknownDefinition(0x07u))
        assertEquals(expected, actual)
    }

    @Test
    fun `propagates a reader error`() {
        val error = ioError()
        val context = componentDecoderContext(IOErrorWasmFileReader(error))

        assertWasmDecodeError(error) {
            neverDependencyDecoder(context)
        }
    }

    private fun bytes(vararg values: Int) = ByteArray(values.size) { index -> values[index].toByte() }

    private companion object {
        val neverDependencyDecoder: ComponentDecoder<CanonicalDefinition> = { context ->
            CanonicalDefinitionDecoder(
                context = context,
                moduleFunctionIndexDecoder = neverModuleFunctionIndexDecoder,
                componentFunctionIndexDecoder = neverComponentFunctionIndexDecoder,
                componentTypeIndexDecoder = neverComponentTypeIndexDecoder,
                moduleMemoryIndexDecoder = neverModuleMemoryIndexDecoder,
                moduleTableIndexDecoder = neverModuleTableIndexDecoder,
                canonicalOptionDecoder = neverCanonicalOptionDecoder,
                optionVectorDecoder = neverOptionVectorDecoder,
                coreValueTypeDecoder = neverCoreValueTypeDecoder,
                resultListDecoder = neverResultListDecoder,
                asyncFlagDecoder = neverFlagDecoder,
                cancellableFlagDecoder = neverFlagDecoder,
                sharedFlagDecoder = neverFlagDecoder,
            )
        }
        val neverModuleFunctionIndexDecoder: ComponentDecoder<ModuleFunctionIndex> = {
            fail("module function index decoder should not be called")
        }
        val neverComponentFunctionIndexDecoder: ComponentDecoder<ComponentFunctionIndex> = {
            fail("component function index decoder should not be called")
        }
        val neverComponentTypeIndexDecoder: ComponentDecoder<ComponentTypeIndex> = {
            fail("component type index decoder should not be called")
        }
        val neverModuleMemoryIndexDecoder: ComponentDecoder<ModuleMemoryIndex> = {
            fail("module memory index decoder should not be called")
        }
        val neverModuleTableIndexDecoder: ComponentDecoder<ModuleTableIndex> = {
            fail("module table index decoder should not be called")
        }
        val neverCanonicalOptionDecoder: ComponentDecoder<CanonicalOption> = {
            fail("canonical option decoder should not be called")
        }
        val neverOptionVectorDecoder: ComponentVectorDecoder<CanonicalOption> = { _, _ ->
            fail("canonical option vector decoder should not be called")
        }
        val neverCoreValueTypeDecoder: ComponentDecoder<CoreValueType> = {
            fail("core value type decoder should not be called")
        }
        val neverResultListDecoder: ComponentDecoder<ValueType?> = {
            fail("component result list decoder should not be called")
        }
        val neverFlagDecoder: ComponentDecoder<Boolean> = {
            fail("flag decoder should not be called")
        }
    }
}
