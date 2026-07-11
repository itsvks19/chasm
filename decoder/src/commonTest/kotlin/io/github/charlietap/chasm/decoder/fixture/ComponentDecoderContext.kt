package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.decoder.component.section.ComponentSectionType
import io.github.charlietap.chasm.decoder.context.ComponentDecoderContext
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.fixture.config.componentConfig

internal fun componentDecoderContext(
    reader: WasmBinaryReader = FakeWasmBinaryReader(),
    config: ComponentConfig = componentConfig(),
    sectionSize: SectionSize = SectionSize(0u),
    sectionType: ComponentSectionType = ComponentSectionType.Custom,
) = ComponentDecoderContext(
    config = config,
    reader = reader,
    sectionSize = sectionSize,
    sectionType = sectionType,
)
