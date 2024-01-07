import pl.andrzejressel.deeplambdaserialization.buildplugin.ChildPlugin
import pl.andrzejressel.deeplambdaserialization.buildplugin.CommonExtension
import pl.andrzejressel.deeplambdaserialization.buildplugin.License

plugins {
  alias(libs.plugins.kotlin)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.testkit)
}

apply<ChildPlugin>()

configure<CommonExtension> { license = License.LGPL }

repositories { mavenCentral() }

dependencies {
  implementation("org.virtuslab:pulumi-kotlin:0.9.4.0")

  implementation("com.squareup:kotlinpoet:1.14.2")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.2")

  implementation("com.github.ajalt.clikt:clikt:3.5.4")

  implementation("com.squareup.tools.build:maven-archeologist:0.0.10")

  implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
  implementation("ch.qos.logback:logback-classic:1.4.8")

  implementation("com.google.code.gson:gson:2.10.1")
}

tasks.test { useJUnitPlatform() }

java { toolchain { languageVersion = JavaLanguageVersion.of(11) } }

sourceSets { main { kotlin { srcDir("pulumi-kotlin/src/main/kotlin") } } }
