plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("reproducible-builds") {
            id = "reproducible-builds"
            implementationClass = "ReproducibleBuildsPlugin"
        }
    }
}
