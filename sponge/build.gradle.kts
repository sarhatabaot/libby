plugins {
    id("com.alessiodp.libby.java-conventions")
}
repositories {
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    api(project(":libby-core"))

    compileOnly(libs.annotations)
    compileOnly(libs.sponge.api) // Higher version requires Java 17
}