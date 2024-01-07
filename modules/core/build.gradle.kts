import pl.andrzejressel.deeplambdaserialization.buildplugin.ChildPlugin
import pl.andrzejressel.deeplambdaserialization.buildplugin.CommonExtension
import pl.andrzejressel.deeplambdaserialization.buildplugin.License

plugins {
  alias(libs.plugins.kotlin) apply false
  `kotlin-dsl` apply false
  alias(libs.plugins.testkit)
  `java-library`
  jacoco
}

apply<ChildPlugin>()

configure<CommonExtension> { license = License.LGPL }

repositories { mavenCentral() }

dependencies {
  implementation(project(":modules:kotlin"))
  implementation(project(":modules:makefile"))
  implementation(libs.commons.exec)
  implementation(libs.commons.io)
  implementation(libs.commons.lang)
  implementation(libs.commons.compress)
  implementation(libs.jsr305)
  implementation(libs.jetbrains.annotations)

  testImplementation(libs.assertj.core)
  testImplementation(libs.junit.jupiter.api)
  testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test { useJUnitPlatform() }

val generateBuildDirFile =
    tasks.register("generateBuildDirFile") {
      val buildDir = project.layout.buildDirectory.dir("builddir")

      outputs.dir(buildDir)

      doLast {
        file("${buildDir.get().asFile}/test").mkdirs()
        file("${buildDir.get().asFile}/test/BuildDir.java")
            .writeText(
                """
      package test;
      import java.nio.file.Path;
      public class BuildDir {
        public static final Path TMP_DIR = Path.of("${buildDir.get().asFile.toPath().parent.resolve("tmp").toString().replace("\\", "\\\\")}");  
      }
    """)
      }
    }

sourceSets { test { java { srcDir(generateBuildDirFile) } } }

java { toolchain { languageVersion = JavaLanguageVersion.of(11) } }

tasks.jacocoTestReport {
  dependsOn("test")

  executionData.setFrom(fileTree(layout.buildDirectory).include("/jacoco/*.exec"))
  reports {
    xml.required = true
    html.required = true
  }
}

tasks.named("check") { dependsOn("jacocoTestReport") }
