repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
    // TODO: configure multiplatform
}

dependencies {
    implementation(kotlin("stdlib"))
    api("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.3.1")

    testImplementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
    }
}
