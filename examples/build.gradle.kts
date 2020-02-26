plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.61"
    id("com.github.ben-manes.versions") version "0.28.0"
    application
}

// ./gradlew run runJava

defaultTasks(ApplicationPlugin.TASK_RUN_NAME)

repositories {
    mavenLocal()
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("net.thauvin.erik:bitly-shorten:0.9.0-beta")
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
}
