plugins {
    java
    id("com.gradleup.shadow") version "9.6.1"
}

group = "net.Indyuce"
version = "1.9.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(25)) } }

// Compile-only API stubs of optional integrations (never packaged).
val stubs by sourceSets.creating {
    java.setSrcDirs(listOf("stubs"))
}
sourceSets.main { compileClasspath += stubs.output }
tasks.named("compileStubsJava") { dependsOn("buildMythicLib") }
tasks.compileJava { dependsOn("buildMythicLib") }

// MythicLib is built from the sibling source tree before compiling; MMOItems and MMOCore
// are represented by compile-only stubs (stubs/net/Indyuce/mmoitems, stubs/net/Indyuce/mmocore).
val buildMythicLib = tasks.register<GradleBuild>("buildMythicLib") {
    dir = file("../MythicLib-Restored")
    tasks = listOf("shadowJar")
}
val mythicLib = file("../MythicLib-Restored/build/libs/MythicLib-1.7-restored.jar")

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.254-stable")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("commons-lang:commons-lang:2.6")
    compileOnly("org.apache.commons:commons-lang3:3.22.0")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.google.code.gson:gson:2.8.9")
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.2.043") { isTransitive = false }
    compileOnly(files(mythicLib))
    for (c in listOf(stubs.compileOnlyConfigurationName)) {
        add(c, "io.papermc.paper:paper-api:26.3.build.254-stable")
        add(c, "org.jetbrains:annotations:24.1.0")
        add(c, "com.google.code.findbugs:jsr305:3.0.2")
        add(c, "commons-lang:commons-lang:2.6")
        add(c, "com.google.code.gson:gson:2.8.9")
        add(c, files(mythicLib))
    }
}

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
    archiveFileName.set("MMOInventory-1.9.1-restored.jar")
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}
tasks.build { dependsOn(tasks.shadowJar) }
