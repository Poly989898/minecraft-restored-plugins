plugins {
    java
    id("com.gradleup.shadow") version "9.6.1"
}

group = "net.Indyuce"
version = "1.13.1"

repositories {
    mavenCentral()
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    maven("https://repo.glaremasters.me/repository/towny/")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(25)) } }

// Compile-only API stubs of optional integrations (never packaged).
val stubs by sourceSets.creating {
    java.setSrcDirs(listOf("stubs"))
}
sourceSets.main { compileClasspath += stubs.output }

// MythicLib is built from the sibling sources (../MythicLib-Restored) before compiling.
val buildMythicLib by tasks.registering(GradleBuild::class) {
    dir = file("../MythicLib-Restored")
    tasks = listOf("shadowJar")
}
val mythicLib = files("../MythicLib-Restored/build/libs/MythicLib-1.7-restored.jar").builtBy(buildMythicLib)

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.254-stable")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("commons-lang:commons-lang:2.6")
    compileOnly("org.apache.commons:commons-lang3:3.22.0")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly(mythicLib)
    compileOnly("io.lumine:Mythic-Dist:5.7.2") { isTransitive = false }
    compileOnly("me.clip:placeholderapi:2.11.6") { isTransitive = false }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9") { isTransitive = false }
    compileOnly("com.sk89q.worldguard:worldguard-core:7.0.9") { isTransitive = false }
    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.0") { isTransitive = false }
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0") { isTransitive = false }
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.2.043") { isTransitive = false }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") { isTransitive = false }
    compileOnly("net.citizensnpcs:citizensapi:2.0.35-20241020.091027-30") { isTransitive = false }

    add(stubs.compileOnlyConfigurationName, "io.papermc.paper:paper-api:26.3.build.254-stable")
    add(stubs.compileOnlyConfigurationName, "org.jetbrains:annotations:24.1.0")
    add(stubs.compileOnlyConfigurationName, "com.google.code.findbugs:jsr305:3.0.2")
    add(stubs.compileOnlyConfigurationName, "commons-lang:commons-lang:2.6")
    add(stubs.compileOnlyConfigurationName, mythicLib)
}

// Bytecode is Java 21 (--release 21), but Paper 26.x artifacts carry JVM 25 metadata.
configurations.matching { it.name.lowercase().endsWith("compileclasspath") }.configureEach {
    attributes.attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
    options.compilerArgs.addAll(listOf("-proc:none", "-Xmaxerrs", "5000"))
}

tasks.jar { enabled = false }

tasks.shadowJar {
    archiveFileName.set("MMOCore-1.13.1-restored.jar")
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}

tasks.build { dependsOn(tasks.shadowJar) }
