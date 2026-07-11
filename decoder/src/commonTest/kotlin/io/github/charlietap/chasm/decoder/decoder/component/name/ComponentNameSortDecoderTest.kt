package io.github.charlietap.chasm.decoder.decoder.component.name

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.component.NameSort
import io.github.charlietap.chasm.decoder.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.error.ComponentDecodeError
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import io.github.charlietap.chasm.fixture.ast.component.componentNameSort
import io.github.charlietap.chasm.fixture.ast.component.coreFunctionNameSort
import io.github.charlietap.chasm.fixture.ast.component.coreGlobalNameSort
import io.github.charlietap.chasm.fixture.ast.component.coreInstanceNameSort
import io.github.charlietap.chasm.fixture.ast.component.coreMemoryNameSort
import io.github.charlietap.chasm.fixture.ast.component.coreModuleNameSort
import io.github.charlietap.chasm.fixture.ast.component.coreTableNameSort
import io.github.charlietap.chasm.fixture.ast.component.coreTagNameSort
import io.github.charlietap.chasm.fixture.ast.component.coreTypeNameSort
import io.github.charlietap.chasm.fixture.ast.component.functionNameSort
import io.github.charlietap.chasm.fixture.ast.component.instanceNameSort
import io.github.charlietap.chasm.fixture.ast.component.typeNameSort
import io.github.charlietap.chasm.fixture.ast.component.valueNameSort
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentNameSortDecoderTest {

    @Test
    fun `decodes every core and component name sort`() {
        val cases = listOf(
            intArrayOf(0x00, 0x00) to coreFunctionNameSort(),
            intArrayOf(0x00, 0x01) to coreTableNameSort(),
            intArrayOf(0x00, 0x02) to coreMemoryNameSort(),
            intArrayOf(0x00, 0x03) to coreGlobalNameSort(),
            intArrayOf(0x00, 0x04) to coreTagNameSort(),
            intArrayOf(0x00, 0x10) to coreTypeNameSort(),
            intArrayOf(0x00, 0x11) to coreModuleNameSort(),
            intArrayOf(0x00, 0x12) to coreInstanceNameSort(),
            intArrayOf(0x01) to functionNameSort(),
            intArrayOf(0x02) to valueNameSort(),
            intArrayOf(0x03) to typeNameSort(),
            intArrayOf(0x04) to componentNameSort(),
            intArrayOf(0x05) to instanceNameSort(),
        )
        val decoder: ComponentDecoder<NameSort> = ::ComponentNameSortDecoder

        cases.forEach { (bytes, sort) ->
            val context = componentDecoderContext(
                reader = BinaryReader(bytes.map(Int::toByte).toByteArray()),
            )

            val actual = decoder(context)

            val expected = Ok(sort)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `rejects unknown component and core sorts`() {
        val cases = listOf(
            intArrayOf(0x06) to ComponentDecodeError.UnknownSort(0x06u),
            intArrayOf(0x00, 0x05) to ComponentDecodeError.UnknownCoreSort(0x05u),
        )
        val decoder: ComponentDecoder<NameSort> = ::ComponentNameSortDecoder

        cases.forEach { (bytes, error) ->
            val context = componentDecoderContext(
                reader = BinaryReader(bytes.map(Int::toByte).toByteArray()),
            )

            val actual = decoder(context)

            val expected = Err(error)
            assertEquals(expected, actual)
        }
    }
}
