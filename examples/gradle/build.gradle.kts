plugins {
    id("application")
    id("com.github.ben-manes.versions") version "0.51.0"
    kotlin("jvm") version "2.2.10"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") } // only needed for SNAPSHOT
}

dependencies {
    implementation("net.thauvin.erik:bitly-shorten:2.0.1-SNAPSHOT")
    implementation("org.json:json:20240303")
}

application {
    mainClass.set("com.example.BitlyExampleKt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
}

tasks {
    register("runJava", JavaExec::class) {
        group = "application"
        mainClass.set("com.example.BitlySample")
        classpath = sourceSets.main.get().runtimeClasspath
    }

    register("runRetrieve", JavaExec::class) {
        group = "application"
        mainClass.set("com.example.BitlyRetrieveKt")
        classpath = sourceSets.main.get().runtimeClasspath
    }
}
