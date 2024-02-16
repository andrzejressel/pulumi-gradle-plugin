import com.palantir.gradle.gitversion.VersionDetails
import gradle.kotlin.dsl.accessors._9b9386f00615b901cfb9cbc14bc04c07.spotless
import groovy.lang.Closure
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.*

plugins {
  `java-library`
  jacoco
  com.palantir.`git-version`
  com.diffplug.spotless
  wrapper
}

spotless {
  kotlinGradle {
    target("*.gradle.kts") // default target for kotlinGradle
    ktfmt() // or ktfmt() or prettier()
  }
}

val versionDetails: Closure<VersionDetails> by extra
val details = versionDetails()

version =
    if (details.isCleanTag) {
      val lastTag = details.lastTag
      if (lastTag.startsWith("v")) {
        // Release
        details.lastTag.removePrefix("v")
      } else {
        // main
        "main-SNAPSHOT"
      }
    } else {
      "DEV-SNAPSHOT"
    }

tasks.wrapper { distributionType = Wrapper.DistributionType.ALL }
