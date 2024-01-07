// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.core.languages;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class JavaLanguageTest {

  @Test
  public void shouldDownloadPulumiLanguageBinary() throws Exception {
    // Given
    var version = "0.9.8";
    var javaLanguage = new JavaLanguage(version);
    // When
    var binary = javaLanguage.downloadLanguageSupportBinary();
    // Then
    assertThat(binary).isNotEmptyFile();
  }

  @Test
  public void shouldGenerateJavaFiles() throws Exception {
    // Given
    var version = "0.9.8";
    var javaLanguage = new JavaLanguage(version);
    var schemaJson =
        Paths.get(Objects.requireNonNull(getClass().getResource("/cockroach-db-schema.json"))
            .toURI());

    // When
    var dest = javaLanguage.generateSourceCode(schemaJson);

    System.out.println(dest);

    // Then
    assertThat(dest).isNotEmptyDirectory();
  }
}
