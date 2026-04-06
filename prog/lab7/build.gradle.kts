plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.0.0" apply false
}

group = "dev.alllexey"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}