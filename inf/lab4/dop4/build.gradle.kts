plugins {
    kotlin("jvm") version "2.1.21"
}

group = "dev.alllexey"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("org.ini4j:ini4j:0.5.4")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}