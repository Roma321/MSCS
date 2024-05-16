plugins {
    kotlin("jvm") version "1.9.23"
    application

}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
}
application {
    mainClass.set("MainKt") // Assuming your main function is in the Main.kt file
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "MainKt")
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(if (file.isDirectory) file else zipTree(file))
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

