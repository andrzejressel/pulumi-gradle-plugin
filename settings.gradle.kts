pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins {
  id("com.gradle.enterprise") version ("3.16.1")
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

if (!System.getenv("CI").isNullOrEmpty()) {
  gradleEnterprise {
    buildScan {
      termsOfServiceUrl = "https://gradle.com/terms-of-service"
      termsOfServiceAgree = "yes"
    }
  }
}

includeBuild("plugin")

rootProject.name = "pulumi-gradle-plugin"

include("modules:core", "modules:gradle-plugin", "modules:kotlin", "modules:makefile")
