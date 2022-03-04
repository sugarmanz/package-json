import Build_gradle.AuthDelegate.Companion.auth

repositories {
    mavenCentral()
}

plugins {
    // build toolchain
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
    // TODO: configure multiplatform

    // validation
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.8.0"

    // publishing
    `maven-publish`
    signing
    // plugin("org.jetbrains.dokka")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    api("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.3.2")

    testImplementation(kotlin("test"))
}

tasks {
    kotlin {
//        explicitApi()
    }

    test {
        useJUnitPlatform()
    }

//    register<Jar>("javadocJar") {
//        dependsOn("dokkaJavadoc")
//        archiveClassifier.set("javadoc")
//        from("$buildDir/dokka/javadoc")
//    }
}

// TODO: Should probably pull from a common publishing helpers module
class AuthDelegate private constructor(private val delegate: Project, private val name: String? = null, private val transform: (String?) -> String? = { it }) {

    operator fun getValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>) =
        transform(delegate.findProperty(name ?: property.name) as? String)

    companion object {
        val Project.auth get() = AuthDelegate(this)
        fun Project.auth(name: String? = null, transform: (String?) -> String? = { it }) = AuthDelegate(this, name, transform)
    }

}

publishing {
    publications {
        register<MavenPublication>("jar") {
            from(components.getByName("java"))
            // artifact(tasks["javadocJar"])

            pom {
                name.set(name)
                description.set("Simple Kotlin MPP module declaring NPMs package.json")
                url.set("https://github.com/sugarmanz/package-json")

                // TODO: Figure out license
//                licenses {
//                    license {
//                        name.set("MIT")
//                        url.set("https://github.com/intuit/hooks/blob/master/LICENSE")
//                    }
//                }
                developers {
                    developer {
                        id.set("sugarmanz")
                        name.set("Jeremiah Zucker")
                        email.set("zucker.jeremiah@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/sugarmanz/package-json.git")
                    developerConnection.set("scm:git:ssh://github.com/sugarmanz/package-json.git")
                    url.set("https://github.com/intuit/sugarmanz/package-json/main")
                }
            }
        }
    }
}

configure<SigningExtension> {
    val signingKey by auth {
        it?.replace("\\n", "\n")
    }
    val signingPassword by auth
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(extensions.findByType(PublishingExtension::class.java)!!.publications)
}
