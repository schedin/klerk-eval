val klerk_version: String by project
val klerk_web_version: String by project
val ktor_version: String by project
val logback_version: String by project
val sqlite_jdbc_version: String by project
val coroutinesVersion: String by project
val jwt_version: String by project


plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "2.3.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("com.expediagroup.graphql") version "7.1.1"
}

group = "se.moshicon.klerkframework.todo_app"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("se.moshicon.klerkframework.todo_app.ApplicationKt")
}

dependencies {
    implementation("com.github.klerk-framework:klerk:$klerk_version")
    implementation("com.github.klerk-framework:klerk-web:$klerk_web_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.xerial:sqlite-jdbc:$sqlite_jdbc_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-html-builder-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    // Authentication
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("com.auth0:java-jwt:$jwt_version")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
