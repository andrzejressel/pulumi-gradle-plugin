// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.gradleplugin

import java.net.URI
import kotlin.io.path.writeText
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class DownloadSchemaTask : DefaultTask(), SchemaTask {

  @get:Input abstract val schemaJsonURI: Property<URI>
  @get:OutputFile abstract override val schemaJson: RegularFileProperty

  @TaskAction
  fun run() {
    //        val outputDirectory = output.asFile.get()
    //        if (outputDirectory.exists()) {
    //            outputDirectory.deleteRecursively()
    //        }
    //        outputDirectory.toPath().createDirectory()

    val schema = schemaJsonURI.get().toURL().readText()
    //        val schemaFile = outputDirectory.toPath().resolve("schema.json")
    schemaJson.get().asFile.toPath().writeText(schema)
  }
}
