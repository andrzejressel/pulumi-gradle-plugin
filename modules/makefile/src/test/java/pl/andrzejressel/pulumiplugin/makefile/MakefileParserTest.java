// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.makefile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MakefileParserTest {

  @Test
  void shouldParseSimpleMakefile() throws Exception {
    var resource = getClass().getResource("/simple.Makefile");
    assertThat(resource).isNotNull();
    var file = new File(resource.toURI());

    var variables = MakefileParser.parseMakefile(file.toPath());
    assertThat(variables)
        .containsExactlyInAnyOrderEntriesOf(
            Map.of("VAR1", "value1", "VAR2", "value2", "VAR_COMPOSITE", "value1 value2"));
  }
}
