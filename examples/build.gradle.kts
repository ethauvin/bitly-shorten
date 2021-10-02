import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("application")
    id("com.github.ben-manes.versions") version "0.39.0"
    kotlin("jvm") version "1.5.31"
}

// ./gradlew run --args='https://erik.thauvin.net/ https://bit.ly/2PsNMAA'
// ./gradlew runJava --args='https://erik.thauvin.net/ https://bit.ly/2PsNMAA'
// ./gradlew runRetrieve

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("net.thauvin.erik:bitly-shorten:0.9.4-SNAPSHOT")
    implementation("org.json:json:20210307")
}

application {
    mainClass.set("com.example.BitlyExampleKt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = java.targetCompatibility.toString()
    }

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
