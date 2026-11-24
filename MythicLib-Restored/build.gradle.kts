plugins {
    java
    id("com.gradleup.shadow") version "9.6.1"
}

group = "io.lumine"
version = "1.7"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.254-stable")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("commons-lang:commons-lang:2.6")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")

    // Bundled and relocated into io.lumine.mythic.lib.* (see shadowJar below).
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("net.objecthunter:exp4j:0.4.8")
    implementation("com.zaxxer:HikariCP:4.0.3") {
        exclude(group = "org.slf4j")
    }
    compileOnly("org.slf4j:slf4j-api:2.0.16")
    compileOnly("io.lumine:Mythic-Dist:5.7.2") { isTransitive = false }
    compileOnly("com.mojang:authlib:6.0.54")
    compileOnly("io.netty:netty-all:4.1.115.Final")
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.2.043") { isTransitive = false }
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.10.2") { isTransitive = false }
}

// Compile-only API stubs of optional integrations and of the MMOProfiles API (never packaged).
sourceSets.main {
    java.setSrcDirs(listOf("src/main/java"))
}
val stubs by sourceSets.creating {
    java.setSrcDirs(listOf("stubs/integrations", "stubs/profiles-api"))
}
sourceSets.main { compileClasspath += stubs.output }
dependencies {
    add(stubs.compileOnlyConfigurationName, "io.papermc.paper:paper-api:26.3.build.254-stable")
    add(stubs.compileOnlyConfigurationName, "com.google.code.gson:gson:2.8.9")
    add(stubs.compileOnlyConfigurationName, "org.jetbrains:annotations:24.1.0")
    add(stubs.compileOnlyConfigurationName, "commons-lang:commons-lang:2.6")
    add(stubs.compileOnlyConfigurationName, "com.google.code.findbugs:jsr305:3.0.2")
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
    // NMS wrapper for Paper 26.x (paperweight module).
    from(project(":nms-v26").layout.buildDirectory.dir("classes/java/main"))
    dependsOn(":nms-v26:compileJava")
    archiveFileName.set("MythicLib-1.7-restored.jar")
    relocate("org.apache.commons.math3", "io.lumine.mythic.lib.math3")
    relocate("com.google.gson", "io.lumine.mythic.lib.gson")
    relocate("net.objecthunter.exp4j", "io.lumine.mythic.lib.exp4j")
    relocate("com.zaxxer.hikari", "io.lumine.mythic.lib.sql.hikari")
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/maven/**", "module-info.class")
}

tasks.build { dependsOn(tasks.shadowJar) }
