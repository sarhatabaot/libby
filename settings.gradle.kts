rootProject.name = "libby"

setOf(
    "bukkit",
    "bungee",
    "core",
    "fabric",
    "nukkit",
    "paper",
    "sponge",
    "standalone",
    "velocity"
).forEach {
    subProject(it)
}

fun subProject(name: String) {
    include(":libby-$name")
    project(":libby-$name").projectDir = file(name)
}
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("annotations", "org.jetbrains:annotations:24.0.1")

            library("maven-resolver-supplier", "org.apache.maven.resolver:maven-resolver-supplier:1.9.18")
            library("maven-resolver-provider", "org.apache.maven:maven-resolver-provider:3.9.6")

            library("spigot-api", "org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
            library("bungee-api", "net.md-5:bungeecord-api:1.20-R0.2-SNAPSHOT")
            library("paper-api", "io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
            library("nukkit-api","cn.nukkit:nukkit:1.0-SNAPSHOT")
            library("sponge-api", "org.spongepowered:spongeapi:8.1.0")  // Higher version requires Java 17
            library("velocity-api","com.velocitypowered:velocity-api:3.1.1") // Higher version requires Java 17
            library("fabric-loader", "net.fabricmc:fabric-loader:0.14.21")


            library("slf4j-api", "org.slf4j:slf4j-api:2.0.5")
            library("commons-lang3", "org.apache.commons:commons-lang3:3.14.0")
            library("commons-io", "commons-io:commons-io:2.15.1")

            plugin("shadow", "com.github.johnrengelman.shadow").version("8.1.1")
        }
    }
}