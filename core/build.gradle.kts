plugins {
    id("net.kyori.blossom") version "2.0.1"
    id("com.alessiodp.libby.java-conventions")
}

dependencies {
    compileOnly(libs.annotations)
    compileOnly(libs.maven.resolver.provider)
    compileOnly(libs.maven.resolver.supplier)
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("version", project.version.toString())
            }
        }
    }
    test {
        blossom {
            javaSources {
                property("buildDir", layout.buildDirectory.asFile.get().absolutePath.replace("\\", "\\\\"))
            }
        }
    }
}