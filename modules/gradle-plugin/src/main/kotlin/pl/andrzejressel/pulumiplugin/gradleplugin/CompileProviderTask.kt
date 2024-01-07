// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.gradleplugin

import java.net.URL
import kotlin.io.path.copyTo
import org.apache.commons.lang3.SystemUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.com.google.common.io.Files
import pl.andrzejressel.pulumiplugin.core.compiler.ProviderCompilator

@CacheableTask
abstract class CompileProviderTask : DefaultTask(), SchemaTask {

  @Suppress("unused") @get:Input val operatingSystem: String = SystemUtils.OS_NAME
  @get:Input abstract val repositoryURL: Property<String>
  @get:Input abstract val revision: Property<String>
  @get:OutputFile abstract override val schemaJson: RegularFileProperty
  @get:OutputFile abstract val provider: RegularFileProperty
  @get:OutputFile abstract val version: RegularFileProperty

  @TaskAction
  fun run() {
    val temp = Files.createTempDir()
    val provider =
        ProviderCompilator()
            .compileProvider(URL(repositoryURL.get()), revision.get(), temp.toPath())

    provider.schemaJson.copyTo(schemaJson.get().asFile.toPath(), overwrite = true)
    provider.providerBinary.copyTo(this.provider.get().asFile.toPath(), overwrite = true)
    version.get().asFile.writeText(provider.version)
  }
}
