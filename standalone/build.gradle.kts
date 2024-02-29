import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val mainClassPath = "com.alessiodp.libby.StandaloneTestMain"

plugins {
    id("net.kyori.blossom") version "2.0.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.alessiodp.libby.java-conventions")
}

dependencies {
    api(project(":libby-core"))
    testCompileOnly(libs.commons.lang3)
    testImplementation(libs.commons.io)
}

val shadowTestJar = tasks.register<ShadowJar>("shadowTestJar") {
    description = "Create test jar."
    group = "Test"
    archiveClassifier.set("tests")
    from(sourceSets.test.get().output, sourceSets.main.get().output)
    manifest {
        attributes["Main-Class"] = mainClassPath
    }
    configurations = listOf(project.configurations.compileClasspath.get())
}

val downloadDirectory = layout.buildDirectory.asFile.get().absolutePath.replace("\\", "\\\\")
val testJarFile = shadowTestJar.get().archiveFile.get().asFile.absolutePath.replace("\\", "\\\\")
repositories {
    mavenCentral()
}

tasks.test {
    dependsOn(shadowTestJar)
}

sourceSets {
    test {
        blossom {
            javaSources {
                property("buildDir", downloadDirectory)
                property("testJar", testJarFile)
            }
        }
    }
}
