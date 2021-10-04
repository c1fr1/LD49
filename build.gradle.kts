import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("java")
    application
}

dependencies {
    implementation(files("libs/enignets.jar"))
}

group = "c1fr1"
version = "1.0"

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}
application {
    mainClassName = "MainKt"
    mainClass.set("MainKt")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>() {
    manifest {
        attributes(mapOf("Main-Class" to "MainKt"))
    }
}


tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}