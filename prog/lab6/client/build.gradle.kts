plugins {
    kotlin("jvm")
    application
}

group = "dev.alllexey"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("dev.alllexey.client.MainKt")
}

dependencies {
    implementation(project(":common"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}