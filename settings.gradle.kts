plugins {
  id("com.gradle.enterprise").version("3.1.1")
}

rootProject.name = "bitly-shorten"

gradleEnterprise {
    buildScan {
        // plugin configuration
    }
}
