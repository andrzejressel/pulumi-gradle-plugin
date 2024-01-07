// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.gradleplugin

import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory
import kotlin.io.path.writeText
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateJavaResourcesTask : DefaultTask() {

  @get:Input abstract val providerVersion: Property<String>
  @get:Input abstract val providerName: Property<String>
  @get:OutputDirectory abstract val output: DirectoryProperty

  @TaskAction
  fun generate() {
    //        val javaLanguage = JavaLanguage(pulumiJavaVersion.get())
    val outputDirectory = output.asFile.get()
    //
    if (outputDirectory.exists()) {
      outputDirectory.deleteRecursively()
    }
    outputDirectory.toPath().createDirectory()

    outputDirectory
        .toPath()
        .resolve("com/pulumi")
        .resolve(providerName.get())
        .createDirectories()
        .resolve("version.txt")
        .writeText(providerVersion.get())

    //
    //        val schema = schemaJsonURI.get().toURL().readText()
    //        val schemaFile = Files.createTempFile("schema", ".json")
    //        schemaFile.writeText(schema)
    //
    //        val path = javaLanguage.generateSourceCode(schemaFile)
    //
    //        path.toFile().copyRecursively(outputDirectory)
  }
}
