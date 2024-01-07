// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.core.languages;

import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public interface Language {
  Path generateSourceCode(@Nonnull Path schemaFile) throws IOException;
}
