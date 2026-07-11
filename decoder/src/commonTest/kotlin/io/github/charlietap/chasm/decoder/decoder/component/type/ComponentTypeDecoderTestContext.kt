package io.github.charlietap.chasm.decoder.decoder.component.type

import io.github.charlietap.chasm.decoder.context.ComponentDecoderContext
import io.github.charlietap.chasm.decoder.fixture.componentDecoderContext
import io.github.charlietap.chasm.decoder.reader.BinaryReader

internal fun componentTypeDecoderContext(bytes: ByteArray): ComponentDecoderContext = componentDecoderContext(
    reader = BinaryReader(bytes),
)
