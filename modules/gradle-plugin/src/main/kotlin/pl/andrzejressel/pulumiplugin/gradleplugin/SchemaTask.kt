// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.gradleplugin

import org.gradle.api.Task
import org.gradle.api.file.RegularFileProperty

interface SchemaTask : Task {
  val schemaJson: RegularFileProperty
}
