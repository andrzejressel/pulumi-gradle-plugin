// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.gradleplugin

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

interface PulumiGradleExtension {
  @get:Input val pulumiJavaVersion: Property<String>
  @get:Input @get:Optional val enableKotlinSupport: Property<Boolean>
  @get:Input @get:Optional val enableScalaSupport: Property<Boolean>
  @get:Input val providers: NamedDomainObjectContainer<PulumiProvider>
  @get:Input val customProviders: NamedDomainObjectContainer<CustomPulumiProvider>

  interface AbstractProvider {
    fun getName(): String
  }

  interface PulumiProvider : AbstractProvider {
    override fun getName(): String

    @get:Input val version: Property<String>
    @get:Input val github: Property<String>
  }

  interface CustomPulumiProvider : AbstractProvider {
    override fun getName(): String

    @get:Input val gitUrl: Property<String>
    @get:Input val revision: Property<String>
  }

  fun NamedDomainObjectContainer<PulumiProvider>.cloudflare(version: String) {
    create("cloudflare") {
      github.set("pulumi/pulumi-cloudflare")
      this.version.set(version)
    }
  }

  fun NamedDomainObjectContainer<PulumiProvider>.random(version: String) {
    create("random") {
      github.set("pulumi/pulumi-random")
      this.version.set(version)
    }
  }
}
