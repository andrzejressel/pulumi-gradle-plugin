package pl.andrzejressel.deeplambdaserialization.buildplugin

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import pl.andrzejressel.deeplambdaserialization.buildplugin.License.GPL
import pl.andrzejressel.deeplambdaserialization.buildplugin.License.LGPL

class ChildPlugin : Plugin<Project> {

  override fun apply(target: Project) {
    target.apply<SpotlessPlugin>()
    target.apply<MavenPublishPlugin>()

    val ext = target.extensions.create("license", CommonExtension::class.java)

    val mvnGroupId = target.parent!!.group.toString()
    val mvnArtifactId = target.name
    val mvnVersion = target.parent!!.version.toString()

    target.afterEvaluate {
      val license = ext.license.get()!!

      configure<SpotlessExtension> {
        kotlin {
          target("src/**/*.kt")
          ktfmt()
          licenseHeader("// SPDX-License-Identifier: ${license.spdxId}")
          endWithNewline()
        }
        groovyGradle {
          target("*.gradle") // default target of groovyGradle
          greclipse()
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
          licenseHeader("// SPDX-License-Identifier: ${license.spdxId}")
          endWithNewline()
        }
      }

      version = mvnVersion

      configure<MavenPublishBaseExtension> {
        coordinates(mvnGroupId, mvnArtifactId, mvnVersion)

        pom {
          licenses {
            when (license) {
              GPL ->
                  license {
                    name = "The GNU General Public License"
                    url = "https://www.gnu.org/licenses/gpl-3.0.txt"
                    distribution = "https://www.gnu.org/licenses/gpl-3.0.txt"
                  }
              LGPL ->
                  license {
                    name = "Gnu Lesser General Public License"
                    url = "https://www.gnu.org/licenses/lgpl-3.0.txt"
                    distribution = "https://www.gnu.org/licenses/lgpl-3.0.txt"
                  }
            }
          }
        }
      }
    }
  }
}
