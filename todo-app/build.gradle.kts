val klerk_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "2.3.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
    id("com.expediagroup.graphql") version "7.1.1"
}

group = "se.moshicon.klerkframework.todo_app"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("se.moshicon.klerkframework.todo_app.ApplicationKt")
}



dependencies {
    implementation("com.github.klerk-framework:klerk:$klerk_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
