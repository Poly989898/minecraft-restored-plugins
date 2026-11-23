plugins {
    java
    id("com.gradleup.shadow") version "9.6.1"
}

group = "it.ytnoos"
version = "3.9.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

// NMS 1.8 stubs: compile-only, never packaged.
val stubs by sourceSets.creating {
    java.setSrcDirs(listOf("stubs"))
}

sourceSets {
    main {
        // packetevents-lpx: reconstructed PacketEvents fork classes. They shadow the same-named
        // classes of the upstream packetevents-spigot artifact (see tasks.shadowJar below).
        java.setSrcDirs(listOf("src", "packetevents-lpx/src/main/java"))
        resources.setSrcDirs(listOf("src/resources"))
        compileClasspath += stubs.output
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    // Paper provides netty at runtime; it is not bundled.
    compileOnly("io.netty:netty-all:4.1.72.Final")
    add(stubs.compileOnlyConfigurationName, "io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    add(stubs.compileOnlyConfigurationName, "io.netty:netty-all:4.1.72.Final")

    // Bundled at runtime (relocated below). Same PacketEvents line as the original jar (2.14.1 snapshot base).
    implementation("com.github.retrooper:packetevents-spigot:2.14.1-SNAPSHOT") {
        exclude(group = "io.netty")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
    options.compilerArgs.add("-proc:none")
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    archiveFileName.set("LPX-3.9.1-restored.jar")
    // Our packetevents-lpx classes and the upstream copies share names. Project output is added
    // first, so the first copy (ours) is kept; the upstream duplicates are dropped.
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    mergeServiceFiles()
    // No net.kyori relocation: Adventure is not bundled (Paper 26.3 provides it at runtime, including
    // MiniMessage). Relocating here would rename LPX's own references to classes that are absent.
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
