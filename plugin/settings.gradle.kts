plugins { id("com.gradle.enterprise") version ("3.17") }

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
