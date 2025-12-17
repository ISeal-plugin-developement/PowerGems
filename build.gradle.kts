import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    java
    id("pmd")
    id("com.gradleup.shadow") version "9.3.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "dev.iseal"
version = property("version") as String

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of((property("javaVersion") as String).toInt()))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.iseal.dev/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://nexus.sirblobman.xyz/public/")
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("dev.iseal:SealLib:1.2.0.1")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.github.sirblobman.api:core:2.9-SNAPSHOT")
    compileOnly("com.github.sirblobman.combatlogx:api:11.4-SNAPSHOT")

    // test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

// replace literal ${project.version} in plugin.yml with project.version
tasks.processResources {
    filesMatching("plugin.yml") {
        filter { line -> line.replace("\${project.version}", project.version.toString()) }
        expand("version" to project.version.toString())
    }
}

// Configure jar names and shadow
tasks.withType<ShadowJar>().configureEach {
    archiveBaseName.set("${project.name}")
    archiveClassifier.set("all")
    archiveVersion.set(project.version.toString())
    mergeServiceFiles()
}

// Make the build task produce the shadow jar
tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

pmd {
    toolVersion = "6.52.0"
    isConsoleOutput = true
}

// Configure tests
tasks.test {
    useJUnitPlatform()
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.10")
    }
}