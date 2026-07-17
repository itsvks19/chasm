package io.github.charlietap.chasm.compiler.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.VariableInstruction
import io.github.charlietap.chasm.ir.module.Index

internal data class FusedDestinationPlan(
    val destination: FusedDestination,
    val retainedLocal: Index.LocalIndex? = null,
)

internal typealias FusedDestinationFactory = (Instruction?) -> FusedDestinationPlan

internal fun FusedDestinationFactory(
    instruction: Instruction?,
): FusedDestinationPlan = when (instruction) {
    is VariableInstruction.LocalSet -> FusedDestinationPlan(
        destination = FusedDestination.LocalSet(instruction.localIdx),
    )
    is VariableInstruction.LocalTee -> FusedDestinationPlan(
        destination = FusedDestination.LocalSet(instruction.localIdx),
        retainedLocal = instruction.localIdx,
    )
    else -> FusedDestinationPlan(
        destination = FusedDestination.ValueStack,
    )
}

internal fun FusedDestinationPlan.complete(
    index: Int,
    output: MutableList<Instruction>,
    destinationConsumed: Boolean,
): Int {
    if (!destinationConsumed) return index
    retainedLocal?.let { localIdx ->
        output.add(VariableInstruction.LocalGet(localIdx))
    }
    return index + 1
}
