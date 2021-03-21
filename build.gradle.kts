import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
    jacoco
    `java-library`
    `maven-publish`
    signing
    id("com.github.ben-manes.versions") version "0.38.0"
    id("io.gitlab.arturbosch.detekt") version "1.16.0"
    id("net.thauvin.erik.gradle.semver") version "1.0.4"
    id("org.jetbrains.dokka") version "1.4.30"
    id("org.jetbrains.kotlin.jvm") version "1.4.31"
    id("org.jetbrains.kotlin.kapt") version "1.4.31"
    id("org.sonarqube") version "3.1.1"
}

group = "net.thauvin.erik"
description = "Bitly Shortener for Kotlin/Java"

val gitHub = "ethauvin/$name"
val mavenUrl = "https://github.com/$gitHub"
val deployDir = "deploy"
var isRelease = "release" in gradle.startParameter.taskNames

var semverProcessor = "net.thauvin.erik:semver:1.2.0"

val publicationName = "mavenJava"

object VersionInfo {
    const val okhttp = "4.9.1"
}

val versions: VersionInfo by extra { VersionInfo }

repositories {
    mavenCentral()
    jcenter() // needed for dokka
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:${versions.okhttp}")
    implementation("com.squareup.okhttp3:logging-interceptor:${versions.okhttp}")
    implementation("org.json:json:20210307")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

kapt {
    arguments {
        arg("semver.project.dir", projectDir)
    }
}

detekt {
    baseline = project.rootDir.resolve("config/detekt/baseline.xml")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

sonarqube {
    properties {
        property("sonar.projectKey", "ethauvin_$name")
        property("sonar.sourceEncoding", "UTF-8")
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
    withType<JacocoReport> {
        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<GenerateMavenPom> {
        destination = file("$projectDir/pom.xml")
    }

    assemble {
        dependsOn(javadocJar)
    }

    clean {
        doLast {
            project.delete(fileTree(deployDir))
        }
    }

    dokkaHtml {
        outputDirectory.set(file("$projectDir/docs"))

        dokkaSourceSets {
            configureEach {
                jdkVersion.set(8)
                includes.from("config/dokka/packages.md")
                sourceLink {
                    localDirectory.set(file("/src/main/kotlin/"))
                    remoteUrl.set(URL("https://github.com/ethauvin/${project.name}/tree/master/src/main/kotlin/"))
                    remoteLineSuffix.set("#L")
                }

            }
        }
    }
    
    dokkaJavadoc {
         dokkaSourceSets {
            configureEach {
                jdkVersion.set(8)
                includes.from("config/dokka/packages.md")
            }
        }
        dependsOn(dokkaHtml)
    }

    val copyToDeploy by registering(Copy::class) {
        from(configurations.runtimeClasspath) {
            exclude("annotations-*.jar")
        }
        from(jar)
        into(deployDir)
    }

    register("deploy") {
        description = "Copies all needed files to the $deployDir directory."
        group = PublishingPlugin.PUBLISH_TASK_GROUP
        dependsOn("build", "jar")
        outputs.dir(deployDir)
        inputs.files(copyToDeploy)
        mustRunAfter("clean")
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
        dependsOn("wrapper", "deploy", "gitTag", "publishToMavenLocal")
    }

    "sonarqube" {
        dependsOn("jacocoTestReport")
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
                    connection.set("scm:git:git://github.com/$gitHub.git")
                    developerConnection.set("scm:git:git@github.com:$gitHub.git")
                    url.set("$mavenUrl")
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
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials(PasswordCredentials::class)
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications[publicationName])
}
