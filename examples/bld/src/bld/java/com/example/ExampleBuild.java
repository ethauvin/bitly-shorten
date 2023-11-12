package com.example;

import rife.bld.BaseProject;
import rife.bld.BuildCommand;
import rife.bld.extension.CompileKotlinOperation;
import rife.bld.extension.CompileKotlinOptions;
import rife.bld.operations.RunOperation;

import java.util.List;

import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.compile;

public class ExampleBuild extends BaseProject {
    public ExampleBuild() {
        pkg = "com.example";
        name = "Example";
        version = version(0, 1, 0);

        mainClass = "com.example.BitlyExampleKt";

        javaRelease = 11;
        downloadSources = true;
        autoDownloadPurge = true;
        repositories = List.of(MAVEN_LOCAL, MAVEN_CENTRAL, SONATYPE_SNAPSHOTS_LEGACY);

        scope(compile)
                .include(dependency("net.thauvin.erik:bitly-shorten:1.0.0"))
                .include(dependency("org.json:json:20231013"));
    }

    public static void main(String[] args) {
        new ExampleBuild().start(args);
    }

    @Override
    public void compile() throws Exception {
        new CompileKotlinOperation()
                .fromProject(this)
                .compileOptions(
                        new CompileKotlinOptions()
                                .jdkRelease(javaRelease)
                                .verbose(true)
                )
                .execute();

        // Also compile the Java source code
        super.compile();
    }

    @BuildCommand(value = "run-java", summary = "Runs the Java example")
    public void runJava() throws Exception {
        new RunOperation()
                .fromProject(this)
                .mainClass("com.example.BitlySample")
                .execute();
    }

    @BuildCommand(value = "run-retrieve", summary = "Runs the Retrieve example")
    public void runRetrieve() throws Exception {
        new RunOperation()
                .fromProject(this)
                .mainClass("com.example.BitlyRetrieveKt")
                .execute();
    }
}
