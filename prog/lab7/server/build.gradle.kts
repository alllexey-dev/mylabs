plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.0.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.alllexey"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation("de.brudaswen.kotlinx.serialization:kotlinx-serialization-csv:3.2.1")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("dev.alllexey.server.ServerMainKt")
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}