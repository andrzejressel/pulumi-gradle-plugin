package pl.andrzejressel.pulumiplugin.e2e

import Tests
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pulumi.Context
import com.pulumi.automation.LocalWorkspaceOptions
import com.pulumi.automation.ProjectSettings
import com.pulumi.automation.PulumiAuto
import com.pulumi.automation.StackSettings
import com.pulumi.automation.UpOptions
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldHaveMinLength
import io.kotest.matchers.types.shouldBeInstanceOf
import pl.andrzejressel.pulumiplugin.e2e.JavaExample
import test.BuildDir
import java.io.FileWriter
import java.nio.file.Path
import kotlin.io.path.absolutePathString


class MyTest : FunSpec() {

    private val tempDir = tempdir()

    init {
        test("javaTest") {
            runTest(JavaExample::run)
        }
        test("kotlinTest") {
            runTest(KotlinExample::run)
        }
    }

    private fun runTest(f: (Context) -> Unit) {
        val projectName = "myTest-${Tests.randomSuffix()}"
        val stackName = Tests.randomStackName()

        val projectConfig = mapOf(
            "name" to projectName,
            "runtime" to "java",
            "description" to "test",
            "backend" to mapOf(
                "url" to "file://~"
            ),
            "plugins" to mapOf(
                "providers" to listOf(
                    mapOf(
                        "name" to "random",
                        "path" to BuildDir.TMP_DIR.parent.resolve("generated/deep_serializator_plugin_jars/random_schema").absolutePathString()
                    )
                )
            )
        )

        val projectFile = Path.of(tempDir.toString(), "Pulumi.json").toFile()
        FileWriter(projectFile).use { writer ->
            val gson: Gson = GsonBuilder().create()
            gson.toJson(projectConfig, writer)
        }
        val stackConfig = mapOf<String, String>()
        val stackFile = Path.of(tempDir.toString(), String.format("Pulumi.%s.json", stackName)).toFile()
        FileWriter(stackFile).use { writer ->
            val gson: Gson = GsonBuilder().create()
            gson.toJson(stackConfig, writer)
        }


        val workspace = PulumiAuto.withProjectSettings(
            ProjectSettings.builder() // FIXME
                .name(projectName)
                .backend("file://~")
                .build()
        )
            .withEnvironmentVariables(
                mapOf(
                    "PULUMI_CONFIG_PASSPHRASE" to "test"
                )
            )
            .localWorkspace(
                LocalWorkspaceOptions.builder()
                    .workDir(tempDir.toPath())
                    .program(f)
                    .build()
            )

        val stack = workspace.upsertStack(
            StackSettings.builder()
                .name(stackName)
                .config(mapOf())
                .build()
        )

        stack.upAsync(UpOptions.builder().build()).join()

        val stackOutput = stack.output().join()

        stackOutput["string"]
            .shouldBeInstanceOf<String>()
            .shouldHaveLength(8)

        stackOutput["whoami"]
            .shouldBeInstanceOf<String>()
            .shouldHaveMinLength(1)

    }

}