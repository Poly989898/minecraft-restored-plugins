plugins {
    java
    id("com.gradleup.shadow") version "9.6.1"
}

group = "net.Indyuce"
version = "6.10"

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
}

// MythicLib is built from the sibling MythicLib-Restored sources before this project is compiled.
val mythicLibDir = file("../MythicLib-Restored")
val buildMythicLib by tasks.registering(GradleBuild::class) {
    dir = mythicLibDir
    tasks = listOf("shadowJar")
}
val mythicLibClasses = files(File(mythicLibDir, "build/libs/MythicLib-1.7-restored.jar")).builtBy(buildMythicLib)

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.254-stable")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("commons-lang:commons-lang:2.6")
    compileOnly("org.apache.commons:commons-lang3:3.22.0")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("com.mojang:authlib:6.0.54")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0")
    compileOnly("com.google.code.gson:gson:2.8.9")
    compileOnly("io.lumine:Mythic-Dist:5.7.2") { isTransitive = false }
    compileOnly(mythicLibClasses)
    compileOnly("net.citizensnpcs:citizensapi:2.0.35-20241020.091027-30") { isTransitive = false }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") { isTransitive = false }
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.2.043") { isTransitive = false }
}

// Compile-only API stubs of optional integrations (never packaged).
val stubs by sourceSets.creating {
    java.setSrcDirs(listOf("stubs"))
}
sourceSets.main { compileClasspath += stubs.output }
dependencies {
    add(stubs.compileOnlyConfigurationName, "io.papermc.paper:paper-api:26.3.build.254-stable")
    add(stubs.compileOnlyConfigurationName, "org.jetbrains:annotations:24.1.0")
    add(stubs.compileOnlyConfigurationName, "com.google.code.findbugs:jsr305:3.0.2")
    add(stubs.compileOnlyConfigurationName, "commons-lang:commons-lang:2.6")
    add(stubs.compileOnlyConfigurationName, "com.google.code.gson:gson:2.8.9")
    add(stubs.compileOnlyConfigurationName, "com.mojang:authlib:6.0.54")
    add(stubs.compileOnlyConfigurationName, mythicLibClasses)
    add(stubs.compileOnlyConfigurationName, "net.citizensnpcs:citizensapi:2.0.35-20241020.091027-30") { isTransitive = false }
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(25)) } }

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
    archiveFileName.set("MMOItems-6.10-restored.jar")
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
}

tasks.build { dependsOn(tasks.shadowJar) }
