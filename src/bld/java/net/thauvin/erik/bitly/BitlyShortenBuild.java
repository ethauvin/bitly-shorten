/*
 * BitlyShortenBuild.java
 *
 * Copyright 2020-2024 Erik C. Thauvin (erik@thauvin.net)
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *   Neither the name of this project nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.thauvin.erik.bitly;

import rife.bld.BuildCommand;
import rife.bld.Project;
import rife.bld.extension.CompileKotlinOperation;
import rife.bld.extension.DetektOperation;
import rife.bld.extension.DokkaOperation;
import rife.bld.extension.dokka.LoggingLevel;
import rife.bld.extension.dokka.OutputFormat;
import rife.bld.extension.dokka.SourceSet;
import rife.bld.extension.JacocoReportOperation;
import rife.bld.operations.exceptions.ExitStatusException;
import rife.bld.publish.PomBuilder;
import rife.bld.publish.PublishDeveloper;
import rife.bld.publish.PublishLicense;
import rife.bld.publish.PublishScm;
import rife.tools.exceptions.FileUtilsErrorException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.compile;
import static rife.bld.dependencies.Scope.test;

public class BitlyShortenBuild extends Project {
    final File srcMainKotlin = new File(srcMainDirectory(), "kotlin");

    public BitlyShortenBuild() {
        pkg = "net.thauvin.erik";
        name = "bitly-shorten";
        version = version(2, 0, 0);

        javaRelease = 11;
        downloadSources = true;
        autoDownloadPurge = true;
        repositories = List.of(MAVEN_LOCAL, MAVEN_CENTRAL);

        var okHttp = version(4, 12, 0);
        final var kotlin = version(2, 0, 20);
        scope(compile)
                // Kotlin
                .include(dependency("org.jetbrains.kotlin", "kotlin-stdlib", kotlin))
                .include(dependency("org.jetbrains.kotlin", "kotlin-stdlib-common", kotlin))
                .include(dependency("org.jetbrains.kotlin", "kotlin-stdlib-jdk8", kotlin))
                // OkHttp
                .include(dependency("com.squareup.okhttp3", "okhttp", okHttp))
                .include(dependency("com.squareup.okhttp3", "logging-interceptor", okHttp))
                // JSON
                .include(dependency("org.json", "json", "20240303"));
        scope(test)
                .include(dependency("org.jetbrains.kotlin", "kotlin-test-junit5", kotlin))
                .include(dependency("org.junit.jupiter", "junit-jupiter", version(5, 11, 0)))
                .include(dependency("org.junit.platform", "junit-platform-console-standalone", version(1, 11, 0)))
                .include(dependency("com.willowtreeapps.assertk", "assertk-jvm", version(0, 28, 1)));

        publishOperation()
                .repository(version.isSnapshot() ? repository(SONATYPE_SNAPSHOTS_LEGACY.location())
                        .withCredentials(property("sonatype.user"), property("sonatype.password"))
                        : repository(SONATYPE_RELEASES_LEGACY.location())
                        .withCredentials(property("sonatype.user"), property("sonatype.password")))
                .repository(repository("github"))
                .info()
                .groupId(pkg)
                .artifactId(name)
                .description("A simple implementation of the Bitly link shortening API v4")
                .url("https://github.com/ethauvin/" + name)
                .developer(new PublishDeveloper()
                        .id("ethauvin")
                        .name("Erik C. Thauvin")
                        .email("erik@thauvin.net")
                        .url("https://erik.thauvin.net/")
                )
                .license(new PublishLicense()
                        .name("BSD 3-Clause")
                        .url("https://opensource.org/licenses/BSD-3-Clause")
                )
                .scm(new PublishScm()
                        .connection("scm:git:https://github.com/ethauvin/" + name + ".git")
                        .developerConnection("scm:git:git@github.com:ethauvin/" + name + ".git")
                        .url("https://github.com/ethauvin/" + name)
                )
                .signKey(property("sign.key"))
                .signPassphrase(property("sign.passphrase"));

        jarSourcesOperation().sourceDirectories(srcMainKotlin);
    }

    public static void main(String[] args) {
        new BitlyShortenBuild().start(args);
    }

    @BuildCommand(summary = "Compiles the Kotlin project")
    @Override
    public void compile() throws Exception {
        new CompileKotlinOperation()
                .fromProject(this)
                .execute();
    }

    @BuildCommand(summary = "Checks source with Detekt")
    public void detekt() throws ExitStatusException, IOException, InterruptedException {
        new DetektOperation()
                .fromProject(this)
                .baseline("config/detekt/baseline.xml")
                .execute();
    }

    @BuildCommand(value = "detekt-baseline", summary = "Creates the Detekt baseline")
    public void detektBaseline() throws ExitStatusException, IOException, InterruptedException {
        new DetektOperation()
                .fromProject(this)
                .baseline("config/detekt/baseline.xml")
                .createBaseline(true)
                .execute();
    }

    @BuildCommand(summary = "Generates documentation in HTML format")
    public void docs() throws ExitStatusException, IOException, InterruptedException {
        new DokkaOperation()
                .fromProject(this)
                .loggingLevel(LoggingLevel.INFO)
                .moduleName("Bitly Shorten")
                .moduleVersion(version.toString())
                .outputDir("docs")
                .outputFormat(OutputFormat.HTML)
                .sourceSet(
                        new SourceSet()
                                .src(srcMainKotlin)
                                .srcLink(srcMainKotlin, "https://github.com/ethauvin/" + name +
                                        "/tree/master/src/main/kotlin/", "#L")
                                .includes(new File("config/dokka/packages.md"))
                                .jdkVersion(javaRelease)
                )
                .execute();
    }

    @BuildCommand(summary = "Generates JaCoCo Reports")
    public void jacoco() throws Exception {
        new JacocoReportOperation()
                .fromProject(this)
                .sourceFiles(srcMainKotlin)
                .execute();
    }

    @Override
    public void javadoc() throws ExitStatusException, IOException, InterruptedException {
        new DokkaOperation()
                .fromProject(this)
                .loggingLevel(LoggingLevel.INFO)
                .moduleName("Bitly Shorten")
                .moduleVersion(version.toString())
                .outputDir(new File(buildDirectory(), "javadoc"))
                .outputFormat(OutputFormat.JAVADOC)
                .execute();
    }

    @Override
    public void publish() throws Exception {
        super.publish();
        pomRoot();
    }

    @BuildCommand(value = "pom-root", summary = "Generates the POM file in the root directory")
    public void pomRoot() throws FileUtilsErrorException {
        PomBuilder.generateInto(publishOperation().fromProject(this).info(), dependencies(),
                new File(workDirectory, "pom.xml"));
    }
}
