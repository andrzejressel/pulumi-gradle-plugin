// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.core.languages;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class KotlinLanguageTest {

  @Test
  public void shouldGenerateKotlinFiles() throws Exception {
    // Given
    var kotlinLanguage = new KotlinLanguage();
    var schemaJson =
        Paths.get(Objects.requireNonNull(getClass().getResource("/cockroach-db-schema.json"))
            .toURI());

    // When
    var dest = kotlinLanguage.generateSourceCode(schemaJson);

    // Then
    assertThat(dest).isNotEmptyDirectory();
  }
}
