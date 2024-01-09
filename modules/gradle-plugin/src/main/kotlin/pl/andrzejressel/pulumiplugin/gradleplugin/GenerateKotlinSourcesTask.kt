// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.gradleplugin

import kotlin.io.path.createDirectory
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import pl.andrzejressel.pulumiplugin.core.languages.KotlinLanguage

@CacheableTask
abstract class GenerateKotlinSourcesTask : DefaultTask() {

  @get:PathSensitive(PathSensitivity.NONE)
  @get:InputFile
  abstract val schemaFile: RegularFileProperty
  @get:OutputDirectory abstract val output: DirectoryProperty

  @TaskAction
  fun generate() {
    val language = KotlinLanguage()
    val outputDirectory = output.asFile.get()

    if (outputDirectory.exists()) {
      outputDirectory.deleteRecursively()
    }
    outputDirectory.toPath().createDirectory()
    //
    //        val schema = schemaJsonURI.get().toURL().readText()
    //        val schemaFile = Files.createTempFile("schema", ".json")
    //        schemaFile.writeText(schema)

    val path = language.generateSourceCode(schemaFile.get().asFile.toPath())

    path.toFile().copyRecursively(outputDirectory)
  }
}
