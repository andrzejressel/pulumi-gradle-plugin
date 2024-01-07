import pl.andrzejressel.deeplambdaserialization.buildplugin.ChildPlugin
import pl.andrzejressel.deeplambdaserialization.buildplugin.CommonExtension
import pl.andrzejressel.deeplambdaserialization.buildplugin.License

plugins {
  alias(libs.plugins.kotlin)
  `kotlin-dsl`
  `java-gradle-plugin`
  alias(libs.plugins.testkit)
  jacoco
}

apply<ChildPlugin>()

configure<CommonExtension> { license = License.LGPL }

repositories { mavenCentral() }

dependencies {
  implementation(project(":modules:core"))
  //    compileOnly(libs.plugins.kotlin)
  compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
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

tasks.named("check") { dependsOn("jacocoTestReport") }
