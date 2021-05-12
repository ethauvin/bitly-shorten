plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.0"
    id("com.github.ben-manes.versions") version "0.38.0"
    application
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

tasks {
    register("runJava", JavaExec::class) {
        group = "application"
        main = "com.example.BitlySample"
        classpath = sourceSets["main"].runtimeClasspath
    }

    register("runRetrieve", JavaExec::class) {
        group = "application"
        main = "com.example.BitlyRetrieveKt"
        classpath = sourceSets.main.get().runtimeClasspath
    }
}
