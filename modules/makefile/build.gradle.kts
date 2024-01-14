import pl.andrzejressel.deeplambdaserialization.buildplugin.License

plugins {
  `kotlin-dsl`
  alias(libs.plugins.testkit)
  id("child-plugin")
}

childPlugin { license = License.LGPL }

dependencies {
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
