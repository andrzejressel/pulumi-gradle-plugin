plugins { id("com.gradle.enterprise") version ("3.17.4") }

if (!System.getenv("CI").isNullOrEmpty()) {
    gradleEnterprise {
        buildScan {
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}

dependencyResolutionManagement {
    versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
}

includeBuild("../") {
    dependencySubstitution {
        substitute(module("pl.andrzejressel.pulumiplugin:gradle-plugin"))
            .using(project(":modules:gradle-plugin"))
        substitute(module("pl.andrzejressel.pulumiplugin:core")).using(project(":modules:core"))
        substitute(module("pl.andrzejressel.pulumiplugin.gradleplugin:pl.andrzejressel.pulumiplugin.gradleplugin.gradle.plugin")).using(project(":modules:gradle-plugin"))
    }
}

includeBuild("../pulumi-java-fork/sdk/java") {
    dependencySubstitution {
        substitute(module("com.pulumi:pulumi")).using(project(":pulumi"))
    }
}

//include("modules:java")