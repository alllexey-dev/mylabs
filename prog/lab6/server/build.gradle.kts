plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.0.0"
    application
}

group = "dev.alllexey"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation("de.brudaswen.kotlinx.serialization:kotlinx-serialization-csv:3.2.1")
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("dev.alllexey.server.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}