// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.core.compiler;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.andrzejressel.pulumiplugin.core.compiler.PulumiCtlDownloader.VERSION;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.junit.jupiter.api.Test;

class PulumiCtlDownloaderTest {

  @Test
  void testDownloadPulumictl() throws Exception {
    var result = new PulumiCtlDownloader().downloadPulumiCtl();

    assertThat(result).exists().isRegularFile();

    var actualVersion = getPulumictlVersion(result);
    assertThat(actualVersion).isEqualTo(VERSION);
  }

  private String getPulumictlVersion(Path pulumictlPath) throws Exception {
    var cmd = new CommandLine(pulumictlPath.toString());
    cmd.addArgument("version");

    var stdout = new ByteArrayOutputStream();
    var streamHandler = new PumpStreamHandler(stdout);

    DefaultExecutor exec = new DefaultExecutor();
    exec.setStreamHandler(streamHandler);
    exec.execute(cmd);

    return stdout.toString().trim();
  }
}
