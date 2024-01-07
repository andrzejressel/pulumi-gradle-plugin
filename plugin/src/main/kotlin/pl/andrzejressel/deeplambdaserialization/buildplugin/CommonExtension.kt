package pl.andrzejressel.deeplambdaserialization.buildplugin

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

interface CommonExtension {
  @get:Input val license: Property<License>
}
