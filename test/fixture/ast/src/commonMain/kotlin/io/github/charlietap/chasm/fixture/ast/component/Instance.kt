package io.github.charlietap.chasm.fixture.ast.component

import io.github.charlietap.chasm.ast.component.ComponentValue
import io.github.charlietap.chasm.ast.component.ComponentValueLiteral
import io.github.charlietap.chasm.ast.component.CoreExport
import io.github.charlietap.chasm.ast.component.CoreExportTarget
import io.github.charlietap.chasm.ast.component.CoreInstanceDefinition
import io.github.charlietap.chasm.ast.component.CoreInstantiateArgument
import io.github.charlietap.chasm.ast.component.ExportTarget
import io.github.charlietap.chasm.ast.component.Index
import io.github.charlietap.chasm.ast.component.InlineExport
import io.github.charlietap.chasm.ast.component.InstanceDefinition
import io.github.charlietap.chasm.ast.component.InstantiateArgument
import io.github.charlietap.chasm.ast.component.NameAttributes
import io.github.charlietap.chasm.ast.component.StartDefinition
import io.github.charlietap.chasm.ast.component.ValueType
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.fixture.ast.value.nameValue

fun coreInstanceDefinition(): CoreInstanceDefinition = instantiateCoreInstanceDefinition()

fun instantiateCoreInstanceDefinition(
    moduleIndex: Index.ComponentModuleIndex = componentModuleIndex(),
    args: List<CoreInstantiateArgument> = emptyList(),
) = CoreInstanceDefinition.Instantiate(
    moduleIndex = moduleIndex,
    args = args,
)

fun inlineExportsCoreInstanceDefinition(
    exports: List<CoreExport> = emptyList(),
) = CoreInstanceDefinition.InlineExports(
    exports = exports,
)

fun coreInstantiateArgument(
    name: NameValue = nameValue(),
    instanceIndex: Index.ComponentModuleInstanceIndex = componentModuleInstanceIndex(),
) = CoreInstantiateArgument(
    name = name,
    instanceIndex = instanceIndex,
)

fun coreExport(
    name: NameValue = nameValue(),
    target: CoreExportTarget = coreExportTarget(),
) = CoreExport(
    name = name,
    target = target,
)

fun instanceDefinition(): InstanceDefinition = instantiateInstanceDefinition()

fun instantiateInstanceDefinition(
    componentIndex: Index.ComponentIndex = componentIndex(),
    args: List<InstantiateArgument> = emptyList(),
) = InstanceDefinition.Instantiate(
    componentIndex = componentIndex,
    args = args,
)

fun inlineExportsInstanceDefinition(
    exports: List<InlineExport> = emptyList(),
) = InstanceDefinition.InlineExports(
    exports = exports,
)

fun instantiateArgument(
    name: NameValue = nameValue(),
    target: ExportTarget = exportTarget(),
) = InstantiateArgument(
    name = name,
    target = target,
)

fun inlineExport(
    name: NameAttributes = nameAttributes(),
    target: ExportTarget = exportTarget(),
) = InlineExport(
    name = name,
    target = target,
)

fun startDefinition(
    functionIndex: Index.ComponentFunctionIndex = componentFunctionIndex(),
    args: List<Index.ComponentValueIndex> = emptyList(),
    resultCount: UInt = 0u,
) = StartDefinition(
    functionIndex = functionIndex,
    args = args,
    resultCount = resultCount,
)

fun componentValue(
    type: ValueType = componentValueType(),
    value: ComponentValueLiteral = componentValueLiteral(),
) = ComponentValue(
    type = type,
    value = value,
)

fun componentValueLiteral(): ComponentValueLiteral = boolComponentValueLiteral()

fun boolComponentValueLiteral(
    value: Boolean = false,
) = ComponentValueLiteral.Bool(
    value = value,
)

fun s8ComponentValueLiteral(
    value: Byte = 0,
) = ComponentValueLiteral.S8(
    value = value,
)

fun u8ComponentValueLiteral(
    value: UByte = 0u,
) = ComponentValueLiteral.U8(
    value = value,
)

fun s16ComponentValueLiteral(
    value: Short = 0,
) = ComponentValueLiteral.S16(
    value = value,
)

fun u16ComponentValueLiteral(
    value: UShort = 0u,
) = ComponentValueLiteral.U16(
    value = value,
)

fun s32ComponentValueLiteral(
    value: Int = 0,
) = ComponentValueLiteral.S32(
    value = value,
)

fun u32ComponentValueLiteral(
    value: UInt = 0u,
) = ComponentValueLiteral.U32(
    value = value,
)

fun s64ComponentValueLiteral(
    value: Long = 0L,
) = ComponentValueLiteral.S64(
    value = value,
)

fun u64ComponentValueLiteral(
    value: ULong = 0uL,
) = ComponentValueLiteral.U64(
    value = value,
)

fun f32ComponentValueLiteral(
    value: Float = 0.0f,
) = ComponentValueLiteral.F32(
    value = value,
)

fun f64ComponentValueLiteral(
    value: Double = 0.0,
) = ComponentValueLiteral.F64(
    value = value,
)

fun nanComponentValueLiteral() = ComponentValueLiteral.Nan

fun charComponentValueLiteral(
    codePoint: UInt = 0u,
) = ComponentValueLiteral.Char(
    codePoint = codePoint,
)

fun stringComponentValueLiteral(
    value: String = "",
) = ComponentValueLiteral.String(
    value = value,
)

fun recordComponentValueLiteral(
    fields: List<ComponentValueLiteral> = emptyList(),
) = ComponentValueLiteral.Record(
    fields = fields,
)

fun variantComponentValueLiteral(
    label: NameValue = nameValue(),
    value: ComponentValueLiteral? = null,
) = ComponentValueLiteral.Variant(
    label = label,
    value = value,
)

fun listValueComponentValueLiteral(
    elements: List<ComponentValueLiteral> = emptyList(),
) = ComponentValueLiteral.ListValue(
    elements = elements,
)

fun tupleComponentValueLiteral(
    elements: List<ComponentValueLiteral> = emptyList(),
) = ComponentValueLiteral.Tuple(
    elements = elements,
)

fun flagsComponentValueLiteral(
    labels: List<NameValue> = emptyList(),
) = ComponentValueLiteral.Flags(
    labels = labels,
)

fun enumComponentValueLiteral(
    label: NameValue = nameValue(),
) = ComponentValueLiteral.Enum(
    label = label,
)

fun noneComponentValueLiteral() = ComponentValueLiteral.None

fun someComponentValueLiteral(
    value: ComponentValueLiteral = componentValueLiteral(),
) = ComponentValueLiteral.Some(
    value = value,
)

fun okComponentValueLiteral() = ComponentValueLiteral.Ok

fun okValueComponentValueLiteral(
    value: ComponentValueLiteral = componentValueLiteral(),
) = ComponentValueLiteral.OkValue(
    value = value,
)

fun errorComponentValueLiteral() = ComponentValueLiteral.Error

fun errorValueComponentValueLiteral(
    value: ComponentValueLiteral = componentValueLiteral(),
) = ComponentValueLiteral.ErrorValue(
    value = value,
)

fun binaryComponentValueLiteral(
    bytes: List<UByte> = emptyList(),
) = ComponentValueLiteral.Binary(
    bytes = bytes,
)
