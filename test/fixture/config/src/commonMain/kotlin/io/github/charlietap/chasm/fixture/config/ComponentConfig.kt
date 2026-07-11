package io.github.charlietap.chasm.fixture.config

import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.config.ModuleConfig

fun componentConfig(
    moduleConfig: ModuleConfig = moduleConfig(),
    decodeNameSection: Boolean = false,
) = ComponentConfig(
    moduleConfig = moduleConfig,
    decodeNameSection = decodeNameSection,
)
