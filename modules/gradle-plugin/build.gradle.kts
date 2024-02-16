import pl.andrzejressel.deeplambdaserialization.buildplugin.License

plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  alias(libs.plugins.testkit)
  id("child-plugin-kotlin")
}

childPlugin { license = License.LGPL }

dependencies {
  implementation(project(":modules:core"))
  compileOnly(libs.kotlin.gradle.plugin)
  implementation(libs.commons.lang)
  functionalTestImplementation(libs.commons.io)
  functionalTestImplementation(libs.junit.jupiter)
  functionalTestRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

gradlePlugin {
  plugins {
    create("pulumi") {
      id = "pl.andrzejressel.pulumiplugin.gradleplugin"
      implementationClass = "pl.andrzejressel.pulumiplugin.gradleplugin.PulumiGradlePlugin"
    }
  }
}

tasks.named<Test>("functionalTest") { useJUnitPlatform() }

tasks.test { useJUnitPlatform() }

kotlin { jvmToolchain(11) }

gradleTestKitSupport {
  withSupportLibrary("0.14")
  withTruthLibrary("1.5")
}

tasks.jacocoTestReport {
  dependsOn("test", "functionalTest")

  executionData.setFrom(fileTree(layout.buildDirectory).include("/jacoco/*.exec"))
  reports {
    xml.required = true
    html.required = true
  }
}
