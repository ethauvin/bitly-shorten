plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.0"
    id("com.github.ben-manes.versions") version "0.28.0"
    application
}

// ./gradlew run --args='https://erik.thauvin.net/ https://bit.ly/2PsNMAA'
// ./gradlew runJava --args='https://erik.thauvin.net/ https://bit.ly/2PsNMAA'
// ./gradlew runRetrieve

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("net.thauvin.erik:bitly-shorten:0.9.3")
    implementation("org.json:json:20200518")
}

application {
    mainClassName = "com.example.BitlyExampleKt"
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
        classpath = sourceSets["main"].runtimeClasspath
    }
}
