package io.github.charlietap.chasm.validator

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.context.scope.ModuleValidationScope
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.module.ModuleValidator

typealias WasmModuleValidator = (ModuleConfig, Module) -> Result<Module, ModuleValidatorError>

fun WasmModuleValidator(
    config: ModuleConfig,
    module: Module,
): Result<Module, ModuleValidatorError> =
    WasmModuleValidator(
        config = config,
        module = module,
        moduleValidator = ::ModuleValidator,
    )

internal inline fun WasmModuleValidator(
    config: ModuleConfig,
    module: Module,
    crossinline moduleValidator: ModuleValidator<Module>,
): Result<Module, ModuleValidatorError> {
    val context = ModuleValidationContext(config, module)
    return WasmModuleValidator(context, config, module, moduleValidator)
}

internal inline fun WasmModuleValidator(
    context: ModuleValidationContext,
    config: ModuleConfig,
    module: Module,
    crossinline moduleValidator: ModuleValidator<Module>,
): Result<Module, ModuleValidatorError> {
    return ModuleValidationScope(context, config, module, moduleValidator)
}
