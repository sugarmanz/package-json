import Build_gradle.AuthDelegate.Companion.auth
import net.researchgate.release.GitAdapter.GitConfig

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
    id("org.jetbrains.dokka") version "1.6.0"

    id("net.researchgate.release") version "2.6.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
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

    java {
        withSourcesJar()
    }

    test {
        useJUnitPlatform()
    }

    register<Jar>("javadocJar") {
        dependsOn("dokkaJavadoc")
        archiveClassifier.set("javadoc")
        from("$buildDir/dokka/javadoc")
    }

    // TODO: Look into how nexus publishing plugin doesn't fail when publish is already defined
    val publish by getting {
        group = "publishing"
        val isSnapshot = version.let { it as String }.contains("-SNAPSHOT")
        if (!isSnapshot)
            finalizedBy("closeAndReleaseSonatypeStagingRepository")
    }

    val version by creating {
        doLast {
            println(version)
        }
    }
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
            artifact(tasks["javadocJar"])

            pom {
                name.set("package-json")
                description.set("Simple Kotlin MPP module declaring NPMs package.json")
                url.set("https://github.com/sugarmanz/package-json")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/sugarmanz/package-json/blob/main/LICENSE")
                    }
                }
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
    val signingKey by auth
    val signingPassword by auth
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(extensions.findByType(PublishingExtension::class.java)!!.publications)
}

release {
    failOnPublishNeeded = false
    failOnSnapshotDependencies = false

    getProperty("git").let { it as GitConfig }.apply {
        requireBranch = ""
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
