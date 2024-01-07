// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.gradleplugin

import com.autonomousapps.kit.GradleBuilder.build
import com.autonomousapps.kit.GradleBuilder.buildAndFail
import com.autonomousapps.kit.Source
import com.autonomousapps.kit.SourceType
import com.autonomousapps.kit.truth.TestKitTruth.Companion.assertThat
import org.junit.jupiter.api.Test

class PulumiGradlePluginTest {
  @Test
  fun javaTest() {
    // Given
    val project =
        AbstractPulumiGradlePluginFixture(
                """
            pulumi {
               pulumiJavaVersion = "0.9.8"
               providers {
                 create("cockroach") {
                    github = "lbrlabs/pulumi-cockroach"
                    version = "0.2.0"
                 }
               }
            }
            """
                    .trimIndent(),
                listOf(
                    Source(
                        SourceType.JAVA,
                        "Main",
                        "com/example/project",
                        """
        package com.example.project;

        import com.pulumi.cockroach.AllowList;

        public class Main {
          public static void main(String[] args) {
            System.out.println("Hello World");
          }
        }
      """
                            .trimIndent())))
            .gradleProject

    // When
    val result = build(project.rootDir, ":project:run")

    // Then
    assertThat(result).task(":project:run").succeeded()
    assertThat(result).output().contains("Hello World")
  }

  @Test
  fun kotlinTest() {
    // Given
    val project =
        AbstractPulumiGradlePluginFixture(
                """
            pulumi {
               pulumiJavaVersion = "0.9.8"
               enableKotlinSupport = true
               providers {
                 create("cockroach") {
                    github = "lbrlabs/pulumi-cockroach"
                    version = "0.2.0"
                 }
               }
            }
            """
                    .trimIndent(),
                listOf(
                    Source(
                        SourceType.KOTLIN,
                        "Main",
                        "com/example/project",
                        """
        package com.example.project;

        import com.pulumi.cockroach.kotlin.AllowList;

        object Main {
          @JvmStatic
          fun main(args: Array<String>): Unit {
            println("Hello World");
          }
        }
      """
                            .trimIndent())),
                kotlin = true)
            .gradleProject

    // When
    val result = build(project.rootDir, ":project:run")

    // Then
    assertThat(result).task(":project:run").succeeded()
    assertThat(result).output().contains("Hello World")
  }

  @Test
  fun `activating kotlin support in project without kotlin throws error`() {
    // Given
    val project =
        AbstractPulumiGradlePluginFixture(
                """
            pulumi {
               pulumiJavaVersion = "0.9.8"
               enableKotlinSupport = true
               providers {
                 create("cockroach") {
                    github = "lbrlabs/pulumi-cockroach"
                    version = "0.2.0"
                 }
               }
            }
            """
                    .trimIndent(),
                listOf())
            .gradleProject

    // When
    val result = buildAndFail(project.rootDir, ":project:run")

    // Then
    assertThat(result)
        .output()
        .contains("enableKotlinSupport set to true in project without kotlin plugin enabled")
  }
}
