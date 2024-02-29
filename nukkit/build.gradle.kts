plugins {
    id("com.alessiodp.libby.java-conventions")
}
repositories {
    maven("https://repo.opencollab.dev/maven-releases")
    maven("https://repo.opencollab.dev/maven-snapshots")
}

dependencies {
    api(project(":libby-core"))

    compileOnly(libs.nukkit.api)
}