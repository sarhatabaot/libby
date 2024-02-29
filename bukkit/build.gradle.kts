plugins {
    id("com.alessiodp.libby.java-conventions")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    api(project(":libby-core"))

    compileOnly(libs.spigot.api)
}
