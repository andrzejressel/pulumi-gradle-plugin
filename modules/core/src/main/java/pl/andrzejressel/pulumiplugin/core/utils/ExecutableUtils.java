// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.core.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import org.apache.commons.lang3.SystemUtils;

public class ExecutableUtils {
  public static Path makeExecuable(Path exec) throws IOException {
    if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC_OSX) {
      Set<PosixFilePermission> perms = Set.of(
          PosixFilePermission.OWNER_EXECUTE,
          PosixFilePermission.GROUP_EXECUTE,
          PosixFilePermission.OTHERS_EXECUTE);
      Files.setPosixFilePermissions(exec, perms);
    }
    return exec;
  }
}
