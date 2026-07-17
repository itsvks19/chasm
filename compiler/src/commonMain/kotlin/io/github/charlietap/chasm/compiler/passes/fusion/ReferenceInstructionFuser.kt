package io.github.charlietap.chasm.compiler.passes.fusion

import io.github.charlietap.chasm.compiler.passes.PassContext
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ir.instruction.ReferenceSuperInstruction

internal typealias ReferenceInstructionFuser = (PassContext, Int, ReferenceInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun ReferenceInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: ReferenceInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = ReferenceInstructionFuser(
    context = context,
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    operandFactory = ::FusedOperandFactory,
    destinationFactory = ::FusedDestinationFactory,
    unop = ::UnopFuser,
)

internal inline fun ReferenceInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: ReferenceInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    operandFactory: FusedOperandFactory,
    destinationFactory: FusedDestinationFactory,
    unop: UnopFuser,
): Int = when (instruction) {
    is ReferenceInstruction.RefCast -> {
        val reference = input.getOrNull(index - 1)?.let(operandFactory)
        val destinationPlan = input.getOrNull(index + 1).let(destinationFactory)
        val destination = destinationPlan.destination

        val instruction = if (reference == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                reference == null -> ReferenceSuperInstruction.RefCast(
                    reference = FusedOperand.ValueStack,
                    destination = destination,
                    referenceType = instruction.referenceType,
                )
                else -> {
                    output.removeLast()
                    ReferenceSuperInstruction.RefCast(
                        reference = reference,
                        destination = destination,
                        referenceType = instruction.referenceType,
                    )
                }
            }
        }

        output.add(instruction)

        destinationPlan.complete(index, output, destination != FusedDestination.ValueStack)
    }
    is ReferenceInstruction.RefEq -> {
        val reference1 = input.getOrNull(index - 1)?.let(operandFactory)
        val reference2 = input.getOrNull(index - 2)?.let(operandFactory)
        val destinationPlan = input.getOrNull(index + 1).let(destinationFactory)
        val destination = destinationPlan.destination

        val instruction = if (reference1 == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                reference1 == null -> ReferenceSuperInstruction.RefEq(
                    reference1 = FusedOperand.ValueStack,
                    reference2 = FusedOperand.ValueStack,
                    destination = destination,
                )
                reference2 == null -> {
                    output.removeLast()
                    ReferenceSuperInstruction.RefEq(
                        reference1 = reference1,
                        reference2 = FusedOperand.ValueStack,
                        destination = destination,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    ReferenceSuperInstruction.RefEq(
                        reference1 = reference1,
                        reference2 = reference2,
                        destination = destination,
                    )
                }
            }
        }

        output.add(instruction)

        destinationPlan.complete(index, output, destination != FusedDestination.ValueStack)
    }
    is ReferenceInstruction.RefIsNull -> {
        val value = input.getOrNull(index - 1)?.let(operandFactory)
        val destinationPlan = input.getOrNull(index + 1).let(destinationFactory)
        val destination = destinationPlan.destination

        val instruction = if (value == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                value == null -> ReferenceSuperInstruction.RefIsNull(
                    value = FusedOperand.ValueStack,
                    destination = destination,
                )
                else -> {
                    output.removeLast()
                    ReferenceSuperInstruction.RefIsNull(
                        value = value,
                        destination = destination,
                    )
                }
            }
        }

        output.add(instruction)

        destinationPlan.complete(index, output, destination != FusedDestination.ValueStack)
    }
    is ReferenceInstruction.RefNull -> {
        val destinationPlan = input.getOrNull(index + 1).let(destinationFactory)
        val destination = destinationPlan.destination

        val instruction = if (destination == FusedDestination.ValueStack) {
            instruction
        } else {
            ReferenceSuperInstruction.RefNull(
                destination = destination,
                type = instruction.type,
            )
        }

        output.add(instruction)

        destinationPlan.complete(index, output, destination != FusedDestination.ValueStack)
    }
    is ReferenceInstruction.RefFunc -> {
        val destinationPlan = input.getOrNull(index + 1).let(destinationFactory)
        val destination = destinationPlan.destination

        val instruction = if (destination == FusedDestination.ValueStack) {
            instruction
        } else {
            ReferenceSuperInstruction.RefFunc(
                destination = destination,
                funcIdx = instruction.funcIdx,
            )
        }

        output.add(instruction)

        destinationPlan.complete(index, output, destination != FusedDestination.ValueStack)
    }
    is ReferenceInstruction.RefAsNonNull -> unop(
        index,
        instruction,
        input,
        output,
        { value, destination ->
            ReferenceSuperInstruction.RefAsNonNull(
                value = value,
                destination = destination,
            )
        },
    )
    is ReferenceInstruction.RefTest -> {
        val reference = input.getOrNull(index - 1)?.let(operandFactory)
        val destinationPlan = input.getOrNull(index + 1).let(destinationFactory)
        val destination = destinationPlan.destination

        val instruction = if (reference == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                reference == null -> ReferenceSuperInstruction.RefTest(
                    reference = FusedOperand.ValueStack,
                    destination = destination,
                    referenceType = instruction.referenceType,
                )
                else -> {
                    output.removeLast()
                    ReferenceSuperInstruction.RefTest(
                        reference = reference,
                        destination = destination,
                        referenceType = instruction.referenceType,
                    )
                }
            }
        }

        output.add(instruction)

        destinationPlan.complete(index, output, destination != FusedDestination.ValueStack)
    }
}
