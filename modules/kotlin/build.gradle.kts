import pl.andrzejressel.deeplambdaserialization.buildplugin.License

plugins {
  `kotlin-dsl`
  alias(libs.plugins.testkit)
  id("child-plugin-kotlin")
}

childPlugin { license = License.LGPL }

dependencies {
  implementation("org.virtuslab:pulumi-kotlin:0.9.4.0")

  implementation("com.squareup:kotlinpoet:1.16.0")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.8.0")

  implementation("com.github.ajalt.clikt:clikt:4.2.2")

  implementation("com.squareup.tools.build:maven-archeologist:0.0.10")

  implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
  implementation("ch.qos.logback:logback-classic:1.5.2")

  implementation("com.google.code.gson:gson:2.10.1")
}

java { toolchain { languageVersion = JavaLanguageVersion.of(11) } }

sourceSets { main { kotlin { srcDir("pulumi-kotlin/src/main/kotlin") } } }
