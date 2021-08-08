import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.20"
    application
}

group = "dev.cwenck.music-manager"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.30.6")
    implementation("com.google.apis:google-api-services-sheets:v4-rev581-1.25.0")
    implementation("com.google.guava:guava:30.1-jre")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-codec:commons-codec:1.15")
    // TODO : Replace this with a custom API implementation
    implementation("se.michaelthelin.spotify:spotify-web-api-java:6.5.4")

    // Jackson (Not sure this is neede)
    // implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")

    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-jackson:2.3.1")
}

tasks.test {
    useTestNG()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("core.MainKt")
}
