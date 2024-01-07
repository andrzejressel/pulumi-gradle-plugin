// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.gradleplugin

import java.net.URI
import kotlin.io.path.createDirectories
import org.apache.commons.lang3.SystemUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import pl.andrzejressel.pulumiplugin.gradleplugin.utils.cast

class PulumiGradlePlugin : Plugin<Project> {
  override fun apply(target: Project) {

    val javaPlugin = target.extensions.getByType<JavaPluginExtension>()

    val ext = target.extensions.create("pulumi", PulumiGradleExtension::class.java)

    val jarDirs = target.layout.buildDirectory.dir("pulumi")
    jarDirs.get().asFile.toPath().createDirectories()

    val javaClassesTasks = target.objects.domainObjectContainer(TaskProvider::class.java)
    val javaResourcesTasks = target.objects.domainObjectContainer(TaskProvider::class.java)
    val kotlinClassesTasks = target.objects.domainObjectContainer(TaskProvider::class.java)

    val kotlinExtension =
        try {
          target.kotlinExtension
        } catch (e: NoClassDefFoundError) {
          null
        }

    kotlinExtension?.sourceSets?.getByName("main") { kotlin { srcDirs(kotlinClassesTasks) } }

    javaPlugin.sourceSets.getByName("main") {
      resources { srcDirs(javaResourcesTasks) }
      java { srcDirs(javaClassesTasks) }
    }

    target.afterEvaluate {
      val kotlinSupportEnabled = ext.enableKotlinSupport.getOrElse(false)

      if (kotlinSupportEnabled && kotlinExtension == null) {
        throw RuntimeException(
            "enableKotlinSupport set to true in project without kotlin plugin enabled")
      }

      ext.providers.forEach { provider ->
        val schemaTask = downloadSchemaTask(target, provider)
        val javaSourcesTask = generateJavaSourcesTask(target, provider, ext, schemaTask.cast())
        val resourceTask = generateJavaResourcesTask(target, provider, provider.version)

        javaClassesTasks.add(javaSourcesTask)
        javaResourcesTasks.add(resourceTask)

        if (kotlinSupportEnabled) {
          val task = generateKotlinSourcesTaskTaskProvider(target, provider, schemaTask.cast())
          kotlinClassesTasks.add(task)
        }
      }

      ext.customProviders.forEach { provider ->
        val schemaTask = compileProviderTask(target, provider)
        val version = schemaTask.flatMap { it.version }.map { it.asFile.readText() }
        val javaSourcesTask = generateJavaSourcesTask(target, provider, ext, schemaTask.cast())
        val resourceTask = generateJavaResourcesTask(target, provider, version)

        javaClassesTasks.add(javaSourcesTask)
        javaResourcesTasks.add(resourceTask)

        if (kotlinSupportEnabled) {
          val task = generateKotlinSourcesTaskTaskProvider(target, provider, schemaTask.cast())
          kotlinClassesTasks.add(task)
        }
      }

      if (kotlinSupportEnabled) {
        dependencies { add("implementation", "org.virtuslab:pulumi-kotlin:0.9.4.0") }
      }

      if (ext.pulumiJavaVersion.isPresent) {
        dependencies {
          listOf(
                  "com.pulumi:pulumi:${ext.pulumiJavaVersion.get()}",
                  "com.google.code.findbugs:jsr305:3.0.2",
                  "com.google.code.gson:gson:2.8.9")
              .forEach { add("implementation", it) }
        }
      }
    }
  }

  private fun generateKotlinSourcesTaskTaskProvider(
      target: Project,
      provider: PulumiGradleExtension.AbstractProvider,
      schemaTask: Provider<SchemaTask>
  ): TaskProvider<GenerateKotlinSourcesTask> {
    val taskName = "generateKotlinSourcesFor${provider.getName().capitalized()}"
    return target.tasks.register<GenerateKotlinSourcesTask>(taskName) {
      //            schemaJsonURI =
      //
      // URI.create("https://raw.githubusercontent.com/${provider.github.get()}/v${provider.version.get()}/provider/cmd/pulumi-resource-${provider.getName()}/schema.json")
      schemaFile = schemaTask.flatMap { it.schemaJson }
      output = target.layout.buildDirectory.dir("pulumi/${provider.getName()}_kotlin")
    }
  }

  private fun downloadSchemaTask(
      target: Project,
      provider: PulumiGradleExtension.PulumiProvider
  ): TaskProvider<DownloadSchemaTask> {
    val taskName = "downloadSchemaFor${provider.getName().capitalized()}"
    return target.tasks.register<DownloadSchemaTask>(taskName) {
      schemaJsonURI =
          URI.create(
              "https://raw.githubusercontent.com/${provider.github.get()}/v${provider.version.get()}/provider/cmd/pulumi-resource-${provider.getName()}/schema.json")
      schemaJson =
          target.layout.buildDirectory.file("pulumi/${provider.getName()}_schema/schema.json")
    }
  }

  private fun compileProviderTask(
      target: Project,
      provider: PulumiGradleExtension.CustomPulumiProvider
  ): TaskProvider<CompileProviderTask> {
    val taskName = "compileProviderFor${provider.getName().capitalized()}"
    val extension =
        if (SystemUtils.IS_OS_WINDOWS) {
          ".exe"
        } else {
          ""
        }
    return target.tasks.register<CompileProviderTask>(taskName) {
      repositoryURL = provider.gitUrl
      revision = provider.revision
      schemaJson =
          target.layout.buildDirectory.file("pulumi/${provider.getName()}_schema/schema.json")
      this.provider =
          target.layout.buildDirectory.file(
              "pulumi/${provider.getName()}_schema/pulumi-resource-${provider.getName()}${extension}")
      version = target.layout.buildDirectory.file("pulumi/${provider.getName()}_schema/version.txt")
    }
  }

  private fun generateJavaSourcesTask(
      target: Project,
      provider: PulumiGradleExtension.AbstractProvider,
      ext: PulumiGradleExtension,
      schemaTask: Provider<SchemaTask>
  ): TaskProvider<GenerateJavaSourcesTask> {
    val taskName = "generateJavaSourcesFor${provider.getName().capitalized()}"
    return target.tasks.register<GenerateJavaSourcesTask>(taskName) {
      pulumiJavaVersion = ext.pulumiJavaVersion
      schemaFile = schemaTask.flatMap { it.schemaJson }
      output = target.layout.buildDirectory.dir("pulumi/${provider.getName()}_java")
    }
  }

  private fun generateJavaResourcesTask(
      target: Project,
      provider: PulumiGradleExtension.AbstractProvider,
      version: Provider<String>
  ): TaskProvider<GenerateJavaResourcesTask> {
    val taskName = "generateJavaResourcesFor${provider.getName().capitalized()}"
    return target.tasks.register<GenerateJavaResourcesTask>(taskName) {
      providerName = provider.getName()
      providerVersion = version
      output = target.layout.buildDirectory.dir("pulumi/${provider.getName()}_resources")
    }
  }
}
