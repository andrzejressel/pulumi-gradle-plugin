import com.diffplug.gradle.spotless.SpotlessExtension
import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.*

configure<SpotlessExtension> {
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

tasks.named<Wrapper>("wrapper") { distributionType = Wrapper.DistributionType.ALL }
