plugins {
    id("com.alessiodp.libby.java-conventions")
}
repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":libby-core"))

    compileOnly(libs.velocity.api) // Higher version requires Java 17
}