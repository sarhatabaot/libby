plugins {
    id("com.alessiodp.libby.java-conventions")
}
repositories {
    maven("https://maven.fabricmc.net")
    mavenCentral()
}

dependencies {
    api(project(":libby-core"))

    compileOnly(libs.fabric.loader)
    compileOnly(libs.slf4j.api) // Not api of fabric-loader
}