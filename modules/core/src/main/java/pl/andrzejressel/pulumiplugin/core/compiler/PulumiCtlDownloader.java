// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.core.compiler;

import static org.apache.commons.lang3.SystemUtils.OS_ARCH;
import static org.apache.commons.lang3.SystemUtils.OS_NAME;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArchUtils;
import org.apache.commons.lang3.SystemUtils;
import pl.andrzejressel.pulumiplugin.core.utils.TarGzExtractor;
import pl.andrzejressel.pulumiplugin.core.utils.ZipExtractor;

public class PulumiCtlDownloader {

  public static final String VERSION = "0.0.46";

  public Path downloadPulumiCtl() throws Exception {

    Path pulumiCtlBinary;

    if (SystemUtils.IS_OS_WINDOWS) {
      pulumiCtlBinary = downloadForWindows();
    } else {
      pulumiCtlBinary = downloadForUnix();
    }

    return pulumiCtlBinary;
  }

  private Path downloadForWindows() throws Exception {
    var tempDirectory = Files.createTempDirectory("pulumictl");
    var tempFile = Files.createTempFile("pulumictl", ".zip");
    URL url = new URL(String.format(
        "https://github.com/pulumi/pulumictl/releases/download/v%s/pulumictl-v%s-%s-%s.zip",
        VERSION, VERSION, getSystemSuffix(), getArchitectureSuffix()));
    FileUtils.copyURLToFile(url, tempFile.toFile());
    ZipExtractor.extractZip(tempFile, tempDirectory);
    return tempDirectory.resolve("pulumictl.exe");
  }

  private Path downloadForUnix() throws Exception {
    var tempDirectory = Files.createTempDirectory("pulumictl");
    var tempFile = Files.createTempFile("pulumictl", ".tar.gz");
    URL url = new URL(String.format(
        "https://github.com/pulumi/pulumictl/releases/download/v%s/pulumictl-v%s-%s-%s.tar.gz",
        VERSION, VERSION, getSystemSuffix(), getArchitectureSuffix()));
    FileUtils.copyURLToFile(url, tempFile.toFile());
    TarGzExtractor.extractTarGz(tempFile, tempDirectory);
    return tempDirectory.resolve("pulumictl");
  }

  private String getSystemSuffix() {
    if (SystemUtils.IS_OS_WINDOWS) {
      return "windows";
    } else if (SystemUtils.IS_OS_LINUX) {
      return "linux";
    } else if (SystemUtils.IS_OS_MAC_OSX) {
      return "darwin";
    } else {
      throw new IllegalStateException(String.format("Unknown operating system: [%s]", OS_NAME));
    }
  }

  private String getArchitectureSuffix() {
    if (ArchUtils.getProcessor().isAarch64()) {
      return "arm64";
    } else if (ArchUtils.getProcessor().isX86()) {
      return "amd64";
    } else {
      throw new IllegalStateException(
          String.format("Unknown processor architecture: [%s]", OS_ARCH));
    }
  }
}
