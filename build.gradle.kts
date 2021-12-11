repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
    // TODO: configure multiplatform

    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.8.0"
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
