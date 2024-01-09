// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.gradleplugin

import com.autonomousapps.kit.AbstractGradleProject
import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.Source
import com.autonomousapps.kit.gradle.Plugin
import java.nio.file.Path

class AbstractPulumiGradlePluginFixture(
    val additions: String,
    val sources: List<Source>,
    val buildCacheDirectory: Path,
    val kotlin: Boolean = false
) : AbstractGradleProject() {

  // Injected into functionalTest JVM by the plugin
  // Also available via AbstractGradleProject.PLUGIN_UNDER_TEST_VERSION
  private val pluginVersion = System.getProperty("com.autonomousapps.plugin-under-test.version")

  val gradleProject: GradleProject = build()

  private fun build(): GradleProject {
    return newGradleProjectBuilder(GradleProject.DslKind.KOTLIN)
        .withRootProject {
          gradleProperties += "org.gradle.caching=true"
          settingsScript.additions =
              """
                    buildCache {
                        local {
                            directory = "${buildCacheDirectory.toUri()}"
                        }
                    }
          """
                  .trimIndent()
        }
        .withSubproject("project") {
          sources.addAll(this@AbstractPulumiGradlePluginFixture.sources)
          withBuildScript {
            plugins(
                listOfNotNull(
                    Plugin.javaLibrary,
                    Plugin.application,
                    if (this@AbstractPulumiGradlePluginFixture.kotlin) {
                      Plugin("org.jetbrains.kotlin.jvm", "1.9.20")
                    } else {
                      null
                    },
                    Plugin("pl.andrzejressel.pulumiplugin.gradleplugin", pluginVersion)))
            dependencies()
            additions =
                this@AbstractPulumiGradlePluginFixture.additions +
                    "\n" +
                    """
                        application {
                            mainClass = "com.example.project.Main"
                        }
                    """
                        .trimIndent()
          }
        }
        .write()
  }
}
