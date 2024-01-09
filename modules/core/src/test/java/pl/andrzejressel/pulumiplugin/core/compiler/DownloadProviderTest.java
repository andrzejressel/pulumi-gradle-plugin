// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.core.compiler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.file.Path;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DownloadProviderTest {

  @TempDir
  Path tempDir;

  @Test
  public void testDownloadProvider() throws Exception {
    var providerCompilator = ProviderCompilator.newInstance();
    // Test case 1: Download provider from a valid URL with a valid revision
    URL providerGitUrl1 = new URL("https://github.com/github/gitignore.git");
    String revision1 = "main";
    Path result1 = providerCompilator.downloadProvider(providerGitUrl1, revision1, tempDir);
    assertNotNull(result1);
    // Add assertions to verify the downloaded provider matches the expected result

    // Test case 2: Download provider from a valid URL with an invalid revision
    URL providerGitUrl2 = new URL("https://github.com/github/gitignore.git");
    String revision2 = "invalid_revision";
    Exception exception2 = assertThrows(
        Exception.class,
        () -> providerCompilator.downloadProvider(providerGitUrl2, revision2, tempDir));
    // Add assertions to verify the exception message or type matches the expected result

    // Test case 3: Download provider from an invalid URL
    URL providerGitUrl3 = new URL("https://github.com/github/invalidrepothatdoesnotexist.git");
    String revision3 = "master";
    Exception exception3 = assertThrows(
        Exception.class,
        () -> providerCompilator.downloadProvider(providerGitUrl3, revision3, tempDir));
    // Add assertions to verify the exception message or type matches the expected result
  }

  @Test
  public void shouldExtractVersionFromProvider() throws Exception {
    var providerCompilator = ProviderCompilator.newInstance();
    URL providerGitUrl1 = new URL("https://github.com/pulumi/pulumi-cloudflare.git");
    String revision1 = "v5.14.0";
    Path result1 = providerCompilator.downloadProvider(providerGitUrl1, revision1, tempDir);
    var pulumiCtl = new PulumiCtlDownloader().downloadPulumiCtl();
    String version1 = providerCompilator.getProviderVersion(result1, pulumiCtl);
    assertEquals("5.14.0", version1);
    // Add assertions to verify the downloaded provider matches the expected result
  }

  @Test
  public void shouldCompileProvider() throws Exception {
    var providerCompilator = ProviderCompilator.newInstance();
    URL providerGitUrl1 = new URL("https://github.com/andrzejressel/pulumi-scaleway.git");
    String revision1 = "a980065adaf9b525f443862be13a3cde646e01e1";

    var provider = providerCompilator.compileProvider(providerGitUrl1, revision1, tempDir);
    assertThat(provider.schemaJson).exists();
    assertThat(provider.providerBinary).exists();
    assertThat(provider.version)
        .isEqualTo(getProviderVersion(provider.providerBinary))
        .isEqualTo("1.11.0-alpha.1704557719+a980065a");
  }

  private String getProviderVersion(Path providerBinary) throws Exception {
    var cmd = new CommandLine(providerBinary.toString());
    cmd.addArgument("--version");

    var stdout = new ByteArrayOutputStream();
    var streamHandler = new PumpStreamHandler(stdout);

    DefaultExecutor exec = new DefaultExecutor();
    exec.setStreamHandler(streamHandler);
    exec.execute(cmd);

    return stdout.toString().trim();
  }
}
