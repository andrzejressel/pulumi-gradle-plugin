import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
  `kotlin-dsl`
  `kotlin-dsl-precompiled-script-plugins`
  alias(libs.plugins.kotlin)
  `java-gradle-plugin`
  alias(libs.plugins.spotless)
}

dependencies {
  implementation(libs.spotless.gradle.plugin)
  implementation(libs.git.version.gradle.plugin)
  implementation(libs.maven.publish.gradle.plugin)
  implementation(libs.kotlin.gradle.plugin)
  implementation(libs.kotlin.serialization.gradle.plugin)
}

repositories {
  mavenCentral()
  maven { url = uri("https://plugins.gradle.org/m2/") }
}

configure<SpotlessExtension> {
  kotlin {
    target("src/**/*.kt")
    ktfmt()
    endWithNewline()
  }
  kotlinGradle {
    target("*.gradle.kts", "src/**/*.gradle.kts") // default target for kotlinGradle
    ktfmt() // or ktfmt() or prettier()
    endWithNewline()
  }
  java {
    importOrder()
    removeUnusedImports()
    cleanthat()
    palantirJavaFormat().style("GOOGLE")
    target("src/**/*.java")
    endWithNewline()
  }
}
