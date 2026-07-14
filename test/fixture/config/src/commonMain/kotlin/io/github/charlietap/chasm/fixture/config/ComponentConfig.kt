package io.github.charlietap.chasm.fixture.config

import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.config.ComponentFeature
import io.github.charlietap.chasm.config.ModuleConfig

fun componentConfig(
    moduleConfig: ModuleConfig = moduleConfig(),
    features: Set<ComponentFeature> = ComponentFeature.entries.toSet(),
    decodeNameSection: Boolean = false,
) = ComponentConfig(
    moduleConfig = moduleConfig,
    features = features,
    decodeNameSection = decodeNameSection,
)
