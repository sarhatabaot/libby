plugins {
    id("com.alessiodp.libby.java-conventions")
}
repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://libraries.minecraft.net/")
}

dependencies {
    api(project(":libby-core"))

    compileOnly(libs.bungee.api)
}