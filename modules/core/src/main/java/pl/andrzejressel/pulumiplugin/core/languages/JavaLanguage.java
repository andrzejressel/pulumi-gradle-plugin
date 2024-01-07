// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.core.languages;

import static org.apache.commons.lang3.SystemUtils.OS_ARCH;
import static org.apache.commons.lang3.SystemUtils.OS_NAME;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArchUtils;
import org.apache.commons.lang3.SystemUtils;
import pl.andrzejressel.pulumiplugin.core.utils.TarGzExtractor;

public class JavaLanguage implements Language {
  private final String pulumiJavaVersion;

  public JavaLanguage(String pulumiJavaVersion) {
    this.pulumiJavaVersion = pulumiJavaVersion;
  }

  @Override
  public Path generateSourceCode(@Nonnull Path schemaFile) throws IOException {
    var temp = Files.createTempDirectory("pulumi");
    var generatorBinary = downloadLanguageSupportBinary();
    var exec = new DefaultExecutor();
    exec.setWorkingDirectory(temp.toFile());
    var cl = new CommandLine(generatorBinary.toFile())
        .addArgument("generate")
        .addArgument("--schema")
        .addArgument(schemaFile.toAbsolutePath().toString())
        .addArgument("--build")
        .addArgument("none");
    var exit = exec.execute(cl);
    if (exit != 0) {
      throw new RuntimeException(String.format("Invoking command %s failed", cl));
    }

    return temp.resolve("java").resolve("src").resolve("main").resolve("java");
  }

  Path downloadLanguageSupportBinary() throws IOException {
    var system = getSystemSuffix();
    var arch = getArchitectureSuffix();
    var url = String.format(
        "https://github.com/pulumi/pulumi-java/releases/download/v%s/pulumi-language-java-v%s-%s-%s.tar.gz",
        pulumiJavaVersion, pulumiJavaVersion, system, arch);
    var temp = Files.createTempDirectory("pulumi");
    var targz = temp.resolve("pulumi.tar.gz");
    var directory = temp.resolve("pulumi");
    Files.createDirectories(directory);

    FileUtils.copyURLToFile(new URL(url), targz.toFile());
    TarGzExtractor.extractTarGz(targz, directory);

    var executableExtension = getExecutableFileExtensions();
    var executable = directory.resolve("pulumi-java-gen" + executableExtension);
    if (!Files.exists(executable)) {
      throw new IllegalStateException(
          String.format("Executable: [%s] cannot be found", executable));
    }
    return executable;
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

  private String getExecutableFileExtensions() {
    if (SystemUtils.IS_OS_WINDOWS) {
      return ".exe";
    } else if (SystemUtils.IS_OS_LINUX) {
      return "";
    } else if (SystemUtils.IS_OS_MAC_OSX) {
      return "";
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
