// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.core.languages;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.virtuslab.pulumikotlin.codegen.Codegen;

public class KotlinLanguage implements Language {
  @Override
  public Path generateSourceCode(@NotNull Path schemaFile) throws IOException {
    try (var is = new FileInputStream(schemaFile.toFile())) {
      return Codegen.INSTANCE.codegen(is).toPath();
    }
  }
}
