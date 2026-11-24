plugins {
    java
    id("com.gradleup.shadow") version "9.6.1"
}

group = "fr.phoenixdevt"
version = "1.1.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(25)) } }

// MythicLib is built from the sibling folder ../MythicLib-Restored before anything is compiled here.
val buildMythicLib = tasks.register<GradleBuild>("buildMythicLib") {
    dir = file("../MythicLib-Restored")
    tasks = listOf("shadowJar")
}
val mythicLibJar = files("../MythicLib-Restored/build/libs/MythicLib-1.7-restored.jar").builtBy(buildMythicLib)

// Compile-only API stubs of ProtocolLib (not available from a public repository here). Never packaged.
val stubs by sourceSets.creating {
    java.setSrcDirs(listOf("stubs/protocollib"))
}
sourceSets.main { compileClasspath += stubs.output }

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.254-stable")
    compileOnly("com.velocitypowered:velocity-api:3.4.0")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("commons-lang:commons-lang:2.6")
    compileOnly("org.apache.commons:commons-lang3:3.17.0")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") { isTransitive = false }
    compileOnly(mythicLibJar)

    add(stubs.compileOnlyConfigurationName, "io.papermc.paper:paper-api:26.3.build.254-stable")
    add(stubs.compileOnlyConfigurationName, "com.google.guava:guava:33.4.0-jre")
    add(stubs.compileOnlyConfigurationName, "org.jetbrains:annotations:24.1.0")
    add(stubs.compileOnlyConfigurationName, mythicLibJar)
}

// Bytecode is Java 21 (--release 21), Paper 26.x artifacts carry JVM 25 metadata.
configurations.matching { it.name.lowercase().endsWith("compileclasspath") }.configureEach {
    attributes.attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
    options.compilerArgs.addAll(listOf("-proc:none", "-Xmaxerrs", "5000"))
}

tasks.jar { enabled = false }

// Guava, Gson, commons-lang3 and the annotations are provided by Paper and Velocity at runtime: nothing is bundled.
tasks.shadowJar {
    archiveFileName.set("MMOProfiles-1.1.1-restored.jar")
}

tasks.build { dependsOn(tasks.shadowJar) }
