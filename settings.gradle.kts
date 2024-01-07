pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0" }

includeBuild("plugin")

rootProject.name = "pulumi-gradle-plugin"

include("modules:core", "modules:gradle-plugin", "modules:kotlin", "modules:makefile")
