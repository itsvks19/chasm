package io.github.charlietap.chasm.fixture.ast.component

import io.github.charlietap.chasm.ast.component.CanonicalDefinition
import io.github.charlietap.chasm.ast.component.CanonicalOption
import io.github.charlietap.chasm.ast.component.ComponentStringEncoding
import io.github.charlietap.chasm.ast.component.Index
import io.github.charlietap.chasm.ast.component.ValueType
import io.github.charlietap.chasm.fixture.ast.module.functionIndex
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex
import io.github.charlietap.chasm.fixture.ast.module.tableIndex
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.ast.module.Index as ModuleIndex
import io.github.charlietap.chasm.type.ValueType as CoreValueType

fun canonicalDefinition(): CanonicalDefinition = liftCanonicalDefinition()

fun liftCanonicalDefinition(
    functionIndex: ModuleIndex.FunctionIndex = functionIndex(),
    options: List<CanonicalOption> = [],
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = CanonicalDefinition.Lift(
    functionIndex = functionIndex,
    options = options,
    typeIndex = typeIndex,
)

fun lowerCanonicalDefinition(
    functionIndex: Index.ComponentFunctionIndex = componentFunctionIndex(),
    options: List<CanonicalOption> = [],
) = CanonicalDefinition.Lower(
    functionIndex = functionIndex,
    options = options,
)

fun resourceNewCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = CanonicalDefinition.ResourceNew(
    typeIndex = typeIndex,
)

fun resourceDropCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = CanonicalDefinition.ResourceDrop(
    typeIndex = typeIndex,
)

fun resourceRepCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = CanonicalDefinition.ResourceRep(
    typeIndex = typeIndex,
)

fun backpressureSetCanonicalDefinition() = CanonicalDefinition.BackpressureSet

fun backpressureIncCanonicalDefinition() = CanonicalDefinition.BackpressureInc

fun backpressureDecCanonicalDefinition() = CanonicalDefinition.BackpressureDec

fun taskReturnCanonicalDefinition(
    result: ValueType? = componentValueType(),
    options: List<CanonicalOption> = [],
) = CanonicalDefinition.TaskReturn(
    result = result,
    options = options,
)

fun taskCancelCanonicalDefinition() = CanonicalDefinition.TaskCancel

fun contextGetCanonicalDefinition(
    type: CoreValueType = i32ValueType(),
    index: UInt = 0u,
) = CanonicalDefinition.ContextGet(
    type = type,
    index = index,
)

fun contextSetCanonicalDefinition(
    type: CoreValueType = i32ValueType(),
    index: UInt = 0u,
) = CanonicalDefinition.ContextSet(
    type = type,
    index = index,
)

fun subtaskCancelCanonicalDefinition(
    async: Boolean = false,
) = CanonicalDefinition.SubtaskCancel(
    async = async,
)

fun subtaskDropCanonicalDefinition() = CanonicalDefinition.SubtaskDrop

fun streamNewCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = CanonicalDefinition.StreamNew(
    typeIndex = typeIndex,
)

fun streamReadCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
    options: List<CanonicalOption> = [],
) = CanonicalDefinition.StreamRead(
    typeIndex = typeIndex,
    options = options,
)

fun streamWriteCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
    options: List<CanonicalOption> = [],
) = CanonicalDefinition.StreamWrite(
    typeIndex = typeIndex,
    options = options,
)

fun streamCancelReadCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
    async: Boolean = false,
) = CanonicalDefinition.StreamCancelRead(
    typeIndex = typeIndex,
    async = async,
)

fun streamCancelWriteCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
    async: Boolean = false,
) = CanonicalDefinition.StreamCancelWrite(
    typeIndex = typeIndex,
    async = async,
)

fun streamDropReadableCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = CanonicalDefinition.StreamDropReadable(
    typeIndex = typeIndex,
)

fun streamDropWritableCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = CanonicalDefinition.StreamDropWritable(
    typeIndex = typeIndex,
)

fun futureNewCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = CanonicalDefinition.FutureNew(
    typeIndex = typeIndex,
)

fun futureReadCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
    options: List<CanonicalOption> = [],
) = CanonicalDefinition.FutureRead(
    typeIndex = typeIndex,
    options = options,
)

fun futureWriteCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
    options: List<CanonicalOption> = [],
) = CanonicalDefinition.FutureWrite(
    typeIndex = typeIndex,
    options = options,
)

fun futureCancelReadCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
    async: Boolean = false,
) = CanonicalDefinition.FutureCancelRead(
    typeIndex = typeIndex,
    async = async,
)

fun futureCancelWriteCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
    async: Boolean = false,
) = CanonicalDefinition.FutureCancelWrite(
    typeIndex = typeIndex,
    async = async,
)

fun futureDropReadableCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = CanonicalDefinition.FutureDropReadable(
    typeIndex = typeIndex,
)

fun futureDropWritableCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = CanonicalDefinition.FutureDropWritable(
    typeIndex = typeIndex,
)

fun errorContextNewCanonicalDefinition(
    options: List<CanonicalOption> = [],
) = CanonicalDefinition.ErrorContextNew(
    options = options,
)

fun errorContextDebugMessageCanonicalDefinition(
    options: List<CanonicalOption> = [],
) = CanonicalDefinition.ErrorContextDebugMessage(
    options = options,
)

fun errorContextDropCanonicalDefinition() = CanonicalDefinition.ErrorContextDrop

fun waitableSetNewCanonicalDefinition() = CanonicalDefinition.WaitableSetNew

fun waitableSetWaitCanonicalDefinition(
    cancellable: Boolean = false,
    memoryIndex: ModuleIndex.MemoryIndex = memoryIndex(),
) = CanonicalDefinition.WaitableSetWait(
    cancellable = cancellable,
    memoryIndex = memoryIndex,
)

fun waitableSetPollCanonicalDefinition(
    cancellable: Boolean = false,
    memoryIndex: ModuleIndex.MemoryIndex = memoryIndex(),
) = CanonicalDefinition.WaitableSetPoll(
    cancellable = cancellable,
    memoryIndex = memoryIndex,
)

fun waitableSetDropCanonicalDefinition() = CanonicalDefinition.WaitableSetDrop

fun waitableJoinCanonicalDefinition() = CanonicalDefinition.WaitableJoin

fun threadIndexCanonicalDefinition() = CanonicalDefinition.ThreadIndex

fun threadNewIndirectCanonicalDefinition(
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
    tableIndex: ModuleIndex.TableIndex = tableIndex(),
) = CanonicalDefinition.ThreadNewIndirect(
    typeIndex = typeIndex,
    tableIndex = tableIndex,
)

fun threadResumeLaterCanonicalDefinition() = CanonicalDefinition.ThreadResumeLater

fun threadSuspendCanonicalDefinition(
    cancellable: Boolean = false,
) = CanonicalDefinition.ThreadSuspend(
    cancellable = cancellable,
)

fun threadYieldCanonicalDefinition(
    cancellable: Boolean = false,
) = CanonicalDefinition.ThreadYield(
    cancellable = cancellable,
)

fun threadSuspendThenResumeCanonicalDefinition(
    cancellable: Boolean = false,
) = CanonicalDefinition.ThreadSuspendThenResume(
    cancellable = cancellable,
)

fun threadYieldThenResumeCanonicalDefinition(
    cancellable: Boolean = false,
) = CanonicalDefinition.ThreadYieldThenResume(
    cancellable = cancellable,
)

fun threadSuspendThenPromoteCanonicalDefinition(
    cancellable: Boolean = false,
) = CanonicalDefinition.ThreadSuspendThenPromote(
    cancellable = cancellable,
)

fun threadYieldThenPromoteCanonicalDefinition(
    cancellable: Boolean = false,
) = CanonicalDefinition.ThreadYieldThenPromote(
    cancellable = cancellable,
)

fun threadSpawnRefCanonicalDefinition(
    shared: Boolean = false,
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
) = CanonicalDefinition.ThreadSpawnRef(
    shared = shared,
    typeIndex = typeIndex,
)

fun threadSpawnIndirectCanonicalDefinition(
    shared: Boolean = false,
    typeIndex: Index.ComponentTypeIndex = componentTypeIndex(),
    tableIndex: ModuleIndex.TableIndex = tableIndex(),
) = CanonicalDefinition.ThreadSpawnIndirect(
    shared = shared,
    typeIndex = typeIndex,
    tableIndex = tableIndex,
)

fun threadAvailableParallelismCanonicalDefinition(
    shared: Boolean = false,
) = CanonicalDefinition.ThreadAvailableParallelism(
    shared = shared,
)

fun canonicalOption(): CanonicalOption = stringEncodingCanonicalOption()

fun stringEncodingCanonicalOption(
    encoding: ComponentStringEncoding = componentStringEncoding(),
) = CanonicalOption.StringEncoding(
    encoding = encoding,
)

fun memoryCanonicalOption(
    index: ModuleIndex.MemoryIndex = memoryIndex(),
) = CanonicalOption.Memory(
    index = index,
)

fun reallocCanonicalOption(
    index: ModuleIndex.FunctionIndex = functionIndex(),
) = CanonicalOption.Realloc(
    index = index,
)

fun postReturnCanonicalOption(
    index: ModuleIndex.FunctionIndex = functionIndex(),
) = CanonicalOption.PostReturn(
    index = index,
)

fun asyncCanonicalOption() = CanonicalOption.Async

fun callbackCanonicalOption(
    index: ModuleIndex.FunctionIndex = functionIndex(),
) = CanonicalOption.Callback(
    index = index,
)

fun componentStringEncoding(): ComponentStringEncoding = utf8ComponentStringEncoding()

fun utf8ComponentStringEncoding() = ComponentStringEncoding.Utf8

fun utf16ComponentStringEncoding() = ComponentStringEncoding.Utf16

fun latin1Utf16ComponentStringEncoding() = ComponentStringEncoding.Latin1Utf16
