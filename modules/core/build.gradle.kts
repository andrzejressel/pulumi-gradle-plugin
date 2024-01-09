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
  testImplementation(libs.junit.jupiter)
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test { useJUnitPlatform() }

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
