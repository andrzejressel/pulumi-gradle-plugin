// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.makefile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MakefileParser {

  public static Map<String, String> parseMakefile(Path path) throws Exception {

    Map<String, String> variables = new HashMap<>();

    try (var scanner = new Scanner(path)) {

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        if (line.contains(":=")) {
          String[] parts = line.split(":=");
          String name = parts[0].trim();
          String value = parts[1].trim();

          // Support variable substitution
          if (value.contains("$")) {
            value = substituteVariables(value, variables);
          }

          variables.put(name, value);
        }
      }
    }

    return variables;
  }

  // Simple variable substitution
  public static String substituteVariables(String value, Map<String, String> variables) {
    for (Map.Entry<String, String> entry : variables.entrySet()) {
      value = value
          .replace("${" + entry.getKey() + "}", entry.getValue())
          .replace("$(" + entry.getKey() + ")", entry.getValue());
    }
    return value;
  }
}
