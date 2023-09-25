import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
    id("com.github.ben-manes.versions") version "0.48.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    id("java")
    id("java-library")
    id("maven-publish")
    id("net.thauvin.erik.gradle.semver") version "1.0.4"
    id("org.jetbrains.dokka") version "1.9.0"
    id("org.jetbrains.kotlinx.kover") version "0.7.3"
    id("org.sonarqube") version "4.3.1.3277"
    id("signing")
    kotlin("jvm") version "1.9.10"
    kotlin("kapt") version "1.9.10"
}

group = "net.thauvin.erik"
description = "A simple implementation of the Bitly link shortening API v4"

val gitHub = "ethauvin/$name"
val mavenUrl = "https://github.com/$gitHub"
val deployDir = "deploy"
val docsDir = "docs"
var isRelease = "release" in gradle.startParameter.taskNames

var semverProcessor = "net.thauvin.erik:semver:1.2.0"

val publicationName = "mavenJava"

object Versions {
    const val OKHTTP = "4.11.0"
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    implementation(platform(kotlin("bom")))

    implementation("com.squareup.okhttp3:okhttp:${Versions.OKHTTP}")
    implementation("com.squareup.okio:okio:3.5.0")
    implementation("com.squareup.okhttp3:logging-interceptor:${Versions.OKHTTP}")
    implementation("org.json:json:20230618")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.27.0")
}

kapt {
    arguments {
        arg("semver.project.dir", projectDir)
    }
}

detekt {
    //toolVersion = "main-SNAPSHOT"
    baseline = project.rootDir.resolve("config/detekt/baseline.xml")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
}

koverReport {
    defaults {
        xml {
            onCheck = true
        }
        html {
            onCheck = true
        }
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", "ethauvin_$name")
        property("sonar.organization", "ethauvin-github")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.coverage.jacoco.xmlReportPaths", "${layout.buildDirectory.get()}/reports/kover/report.xml")
    }
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc)
    archiveClassifier.set("javadoc")
    description = "Assembles a JAR of the generated Javadoc."
    group = JavaBasePlugin.DOCUMENTATION_GROUP
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = java.targetCompatibility.toString()
    }

    withType<DependencyUpdatesTask> {
        rejectVersionIf {
            isNonStable(candidate.version)
        }
    }

    withType<Test> {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        }
    }

    withType<GenerateMavenPom> {
        destination = file("$projectDir/pom.xml")
    }


    clean {
        doLast {
            project.delete(fileTree(deployDir))
            project.delete(files(docsDir))
        }
    }

    dokkaHtml {
        outputDirectory.set(file(docsDir))

        dokkaSourceSets {
            configureEach {
                includes.from("config/dokka/packages.md")
                sourceLink {
                    localDirectory.set(file("src/main/kotlin/"))
                    remoteUrl.set(URL("https://github.com/ethauvin/${project.name}/tree/master/src/main/kotlin/"))
                    remoteLineSuffix.set("#L")
                }

            }
        }
        mustRunAfter("kaptKotlin")
    }

    dokkaJavadoc {
        dokkaSourceSets {
            configureEach {
                includes.from("config/dokka/packages.md")
            }
        }
        dependsOn(dokkaHtml)
    }

    val copyToDeploy by registering(Copy::class) {
        from(configurations.runtimeClasspath) {
            exclude("annotations-*.jar")
            exclude("kotlin-*.jar")
            exclude("json-*.jar")
        }
        from(jar)
        into(deployDir)
    }

    register("deploy") {
        description = "Copies all needed files to the $deployDir directory."
        group = PublishingPlugin.PUBLISH_TASK_GROUP
        dependsOn(clean, build, jar)
        outputs.dir(deployDir)
        inputs.files(copyToDeploy)
        mustRunAfter(clean)
    }

    val gitIsDirty by registering(Exec::class) {
        description = "Fails if git has uncommitted changes."
        group = "verification"
        commandLine("git", "diff", "--quiet", "--exit-code")
    }

    val gitTag by registering(Exec::class) {
        description = "Tags the local repository with version ${project.version}"
        group = PublishingPlugin.PUBLISH_TASK_GROUP
        dependsOn(gitIsDirty)
        if (isRelease) {
            commandLine("git", "tag", "-a", project.version, "-m", "Version ${project.version}")
        }
    }

    register("release") {
        description = "Publishes version ${project.version} to local repository."
        group = PublishingPlugin.PUBLISH_TASK_GROUP
        dependsOn("deploy", gitTag, publishToMavenLocal)
    }
}

publishing {
    publications {
        create<MavenPublication>(publicationName) {
            from(components["java"])
            artifact(javadocJar)
            pom {
                name.set(project.name)
                description.set(project.description)
                url.set(mavenUrl)
                licenses {
                    license {
                        name.set("BSD 3-Clause")
                        url.set("https://opensource.org/licenses/BSD-3-Clause")
                    }
                }
                developers {
                    developer {
                        id.set("ethauvin")
                        name.set("Erik C. Thauvin")
                        email.set("erik@thauvin.net")
                        url.set("https://erik.thauvin.net/")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/$gitHub.git")
                    developerConnection.set("scm:git:git@github.com:$gitHub.git")
                    url.set(mavenUrl)
                }
                issueManagement {
                    system.set("GitHub")
                    url.set("$mavenUrl/issues")
                }
            }
        }
    }
    repositories {
        maven {
            name = "ossrh"
            project.afterEvaluate {
                url = if (version.toString().contains("SNAPSHOT"))
                    uri("https://oss.sonatype.org/content/repositories/snapshots/")
                else
                    uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            }
            credentials(PasswordCredentials::class)
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications[publicationName])
}
