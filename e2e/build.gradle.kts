import java.net.URL

plugins {
    alias(libs.plugins.kotlin)
    id("pl.andrzejressel.pulumiplugin.gradleplugin") version "non-applicable"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.8.1")
    testImplementation("io.kotest:kotest-assertions-core:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}

val generateBuildDirFile = tasks.register("generateBuildDirFile") {

    val buildDir = project.layout.buildDirectory.dir("builddir")

    outputs.dir(buildDir)

    doLast {
        file("${buildDir.get().asFile}/test").mkdirs()
        file("${buildDir.get().asFile}/test/BuildDir.java").writeText("""
      package test;
      import java.nio.file.Path;
      public class BuildDir {
        public static final Path TMP_DIR = Path.of("${buildDir.get().asFile.toPath().parent.resolve("tmp").toString().replace("\\", "\\\\")}");  
      }
    """)
    }
}

sourceSets {
    test {
        java {
            srcDir(generateBuildDirFile)
        }
    }
}


pulumi {
    pulumiJavaVersion = "0.9.9"
    enableKotlinSupport = true
    providers {
        create("command") {
            version = "0.9.2"
            github = "pulumi/pulumi-command"
        }
    }
    customProviders {
        create("random") {
            gitUrl = "https://github.com/andrzejressel/pulumi-random.git"
            revision = "3c45ecef04a1dd2b81ec2987d027dd6010a31d62"
        }
    }
}