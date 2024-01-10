plugins {
  alias(libs.plugins.git.version)
  alias(libs.plugins.kotlin) apply false
  alias(libs.plugins.spotless)
  alias(libs.plugins.maven.publish) apply false
  id("parent-plugin-v2")
}

group = "pl.andrzejressel.pulumiplugin"

repositories {
  mavenCentral()
  google()
}
