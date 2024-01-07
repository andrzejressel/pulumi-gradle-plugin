// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.core.compiler;

import java.nio.file.Path;

public class Provider {
  public final String version;
  public final Path schemaJson;
  public final Path providerBinary;

  public Provider(String version, Path schemaJson, Path providerBinary) {
    this.version = version;
    this.schemaJson = schemaJson;
    this.providerBinary = providerBinary;
  }
}
