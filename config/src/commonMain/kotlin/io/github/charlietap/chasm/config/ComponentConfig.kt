package io.github.charlietap.chasm.config

data class ComponentConfig(
    val moduleConfig: ModuleConfig = ModuleConfig(),
    val decodeNameSection: Boolean = false,
)
