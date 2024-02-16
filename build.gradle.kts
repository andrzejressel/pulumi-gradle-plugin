plugins {
  alias(libs.plugins.kotlin) apply false
  id("parent-plugin")
}

group = "pl.andrzejressel.pulumiplugin"

repositories {
  mavenCentral()
  google()
}
