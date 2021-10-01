import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

dependencies {
    implementation(files("libs/enignets.jar"))
}

group = "c1fr1"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
application {
    mainClass.set("MainKt")
    mainClassName = "MainKt"
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}