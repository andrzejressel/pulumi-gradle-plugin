// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.core.compiler;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.jetbrains.annotations.VisibleForTesting;
import pl.andrzejressel.pulumiplugin.makefile.MakefileParser;

public class ProviderCompilator {

  public static ProviderCompilator newInstance() {
    return new ProviderCompilator();
  }

  public Provider compileProvider(URL providerGitUrl, String revision, Path temporaryDir)
      throws Exception {
    var providerDir = downloadProvider(providerGitUrl, revision, temporaryDir);
    var pulumiCtl = new PulumiCtlDownloader().downloadPulumiCtl();
    var providerVersion = getProviderVersion(providerDir, pulumiCtl);
    var makeFileVariables = new ProviderVariables(
        MakefileParser.parseMakefile(providerDir.resolve("Makefile")), providerVersion);

    provider(providerDir, makeFileVariables);

    // TODO: Implement upstream patching

    return new Provider(
        providerVersion,
        providerDir
            .resolve("provider")
            .resolve("cmd")
            .resolve(makeFileVariables.provider)
            .resolve("schema.json"),
        providerDir.resolve("bin").resolve(makeFileVariables.provider));
  }

  @VisibleForTesting
  public Path downloadProvider(URL providerGitUrl, String revision, Path temporaryDir)
      throws Exception {
    var cloneDirectory = cloneRepository(providerGitUrl, temporaryDir);
    checkoutRevision(cloneDirectory, revision);
    return cloneDirectory;
  }

  private Path cloneRepository(URL providerGitUrl, Path temporaryDir) throws Exception {

    var directory = Files.createTempDirectory(temporaryDir, "pulumi");

    CommandLine cmdLine = new CommandLine("git")
        .addArgument("clone")
        .addArgument(providerGitUrl.toString())
        .addArgument(".");

    DefaultExecutor exec = new DefaultExecutor();
    exec.setWorkingDirectory(directory.toFile());

    int exitCode = exec.execute(cmdLine);

    if (exitCode != 0) {
      throw new Exception("Failed to clone repository");
    }

    return directory;
  }

  private void checkoutRevision(Path cloneDirectory, String revision) throws Exception {
    CommandLine cmdLine = new CommandLine("git").addArgument("checkout").addArgument(revision);

    DefaultExecutor exec = new DefaultExecutor();
    exec.setWorkingDirectory(cloneDirectory.toFile());

    int exitCode = exec.execute(cmdLine);

    if (exitCode != 0) {
      throw new Exception("Failed to checkout revision");
    }
  }

  @VisibleForTesting
  public String getProviderVersion(Path providerDir, Path pulumictlPath) throws Exception {
    var cmd = new CommandLine(pulumictlPath.toString()).addArgument("get").addArgument("version");

    var stdout = new ByteArrayOutputStream();
    var streamHandler = new PumpStreamHandler(stdout);

    DefaultExecutor exec = new DefaultExecutor();
    exec.setWorkingDirectory(providerDir.toFile());
    exec.setStreamHandler(streamHandler);
    exec.execute(cmd);

    return stdout.toString().trim();
  }

  private void provider(Path providerPath, ProviderVariables providerVariables) throws Exception {
    tfGet(providerPath, providerVariables);
    compileProvider(providerVariables, providerPath);
  }

  private void tfGet(Path providerPath, ProviderVariables providerVariables) throws Exception {
    compileTfGen(providerVariables, providerPath);
    regenerateSchema(providerVariables, providerPath);
    recreateMain(providerVariables, providerPath);
  }

  // 	(cd provider && go build -a -o $(WORKING_DIR)/bin/${TFGEN} -ldflags "-X
  // ${PROJECT}/${VERSION_PATH}=${VERSION}" ${PROJECT}/${PROVIDER_PATH}/cmd/${TFGEN})
  private void compileTfGen(ProviderVariables providerVariables, Path providerPath)
      throws Exception {
    var cmd = new CommandLine("go")
        .addArgument("build")
        .addArgument("-a")
        .addArgument("-o")
        .addArgument(providerPath
            .resolve("bin")
            .resolve(providerVariables.tfGen)
            .toAbsolutePath()
            .toString())
        .addArgument("-ldflags")
        .addArgument(String.format(
            "-X %s/%s=%s",
            providerVariables.project, providerVariables.versionPath, providerVariables.version), false)
        .addArgument(String.format(
            "%s/%s/cmd/%s",
            providerVariables.project, providerVariables.providerPath, providerVariables.tfGen));

    DefaultExecutor exec = new DefaultExecutor();
    exec.setWorkingDirectory(providerPath.resolve("provider").toFile());
    exec.execute(cmd);
  }

  // 	$(WORKING_DIR)/bin/${TFGEN} schema --out provider/cmd/${PROVIDER}
  private void regenerateSchema(ProviderVariables providerVariables, Path providerPath)
      throws Exception {
    var cmd = new CommandLine(providerPath
            .resolve("bin")
            .resolve(providerVariables.tfGen)
            .toAbsolutePath()
            .toString())
        .addArgument("schema")
        .addArgument("--out")
        .addArgument(String.format("provider/cmd/%s", providerVariables.provider));

    DefaultExecutor exec = new DefaultExecutor();
    exec.setWorkingDirectory(providerPath.toFile());
    exec.execute(cmd);
  }

  // (cd provider && VERSION=$(VERSION) go generate cmd/${PROVIDER}/main.go)
  private void recreateMain(ProviderVariables providerVariables, Path providerPath)
      throws Exception {
    var cmd = new CommandLine("go")
        .addArgument("generate")
        .addArgument(String.format("cmd/%s/main.go", providerVariables.provider));

    Map<String, String> procEnv = EnvironmentUtils.getProcEnvironment();
    procEnv.put("VERSION", providerVariables.version);
    //        var currentEnv = new HashMap<>(System.getenv());
    //        currentEnv.put("VERSION", providerVariables.version);
    DefaultExecutor exec = new DefaultExecutor();
    exec.setWorkingDirectory(providerPath.resolve("provider").toFile());
    exec.execute(cmd, procEnv); // , currentEnv);
  }

  //	(cd provider && go build -a -o $(WORKING_DIR)/bin/${PROVIDER} -ldflags "-X
  // ${PROJECT}/${VERSION_PATH}=${VERSION}" ${PROJECT}/${PROVIDER_PATH}/cmd/${PROVIDER})
  private void compileProvider(ProviderVariables providerVariables, Path providerPath)
      throws Exception {
    var cmd = new CommandLine("go")
        .addArgument("build")
        .addArgument("-a")
        .addArgument("-o")
        .addArgument(providerPath
            .resolve("bin")
            .resolve(providerVariables.provider)
            .toAbsolutePath()
            .toString())
        .addArgument("-ldflags")
        .addArgument(String.format(
            "-X %s/%s=%s",
            providerVariables.project, providerVariables.versionPath, providerVariables.version), false)
        .addArgument(String.format(
            "%s/%s/cmd/%s",
            providerVariables.project, providerVariables.providerPath, providerVariables.provider));

    var exec = new DefaultExecutor();
    exec.setWorkingDirectory(providerPath.resolve("provider").toFile());
    exec.execute(cmd); // , currentEnv);
  }

  private static class ProviderVariables {
    public final String project;
    public final String versionPath;
    public final String providerPath;
    public final String tfGen;
    public final String provider;
    public final String version;

    ProviderVariables(Map<String, String> makeFileVariables, String providerVersion) {
      this.project = makeFileVariables.get("PROJECT");
      this.versionPath = makeFileVariables.get("VERSION_PATH");
      this.providerPath = makeFileVariables.get("PROVIDER_PATH");
      this.tfGen = makeFileVariables.get("TFGEN");
      this.provider = makeFileVariables.get("PROVIDER");
      this.version = providerVersion;
    }
  }
}
