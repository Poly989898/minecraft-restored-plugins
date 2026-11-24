plugins {
    java
    id("com.gradleup.shadow") version "9.6.1"
}

group = "net.Indyuce"
version = "1.13.1"

repositories {
    mavenCentral()
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

// MMOCore 1.13.1 is written against a MythicLib 1.7.x development build that lies between the restored
// MythicLib 1.7 and the public MythicLib-dist 1.7.1-SNAPSHOT. Compile-only classpath order:
//   1. a few classes taken from MythicLib-dist where MMOCore needs the newer shape (GUI framework, Skill, ...)
//   2. the restored MythicLib built from ../MythicLib-Restored
//   3. the rest of MythicLib-dist as a gap filler for classes the restored 1.7 does not have
val newApiClasses = layout.buildDirectory.dir("mythiclib-new-api")
val mlibApi by configurations.creating { isTransitive = false }
val extractNewApi by tasks.registering(Sync::class) {
    from({ zipTree(mlibApi.singleFile) }) {
        include("io/lumine/mythic/lib/gui/**", "io/lumine/mythic/lib/version/Attributes*.class",
            "io/lumine/mythic/lib/version/Sounds*.class", "io/lumine/mythic/lib/util/lang3/Validate*.class",
            "io/lumine/mythic/lib/skill/Skill.class", "io/lumine/mythic/lib/api/stat/StatMap*.class", "io/lumine/mythic/lib/version/VersionUtils*.class", "io/lumine/mythic/lib/player/modifier/PlayerModifier*.class")
    }
    into(newApiClasses)
    outputs.upToDateWhen { false }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.254-stable")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("commons-lang:commons-lang:2.6")
    compileOnly("org.apache.commons:commons-lang3:3.22.0")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    mlibApi("io.lumine:MythicLib-dist:1.7.1-SNAPSHOT") { isTransitive = false }
    compileOnly(mythicLib)
    compileOnly("io.lumine:Mythic-Dist:5.7.2") { isTransitive = false }
    compileOnly("me.clip:placeholderapi:2.11.6") { isTransitive = false }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9") { isTransitive = false }
    compileOnly("com.sk89q.worldguard:worldguard-core:7.0.9") { isTransitive = false }
    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.0") { isTransitive = false }
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0") { isTransitive = false }
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.2.043") { isTransitive = false }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") { isTransitive = false }
    compileOnly("net.citizensnpcs:citizensapi:2.0.35-SNAPSHOT") { isTransitive = false }

    add(stubs.compileOnlyConfigurationName, "io.papermc.paper:paper-api:26.3.build.254-stable")
    add(stubs.compileOnlyConfigurationName, "org.jetbrains:annotations:24.1.0")
    add(stubs.compileOnlyConfigurationName, "com.google.code.findbugs:jsr305:3.0.2")
    add(stubs.compileOnlyConfigurationName, "commons-lang:commons-lang:2.6")
    add(stubs.compileOnlyConfigurationName, "io.lumine:MythicLib-dist:1.7.1-SNAPSHOT") { isTransitive = false }
    add(stubs.compileOnlyConfigurationName, mythicLib)
}

// Bytecode is Java 21 (--release 21), but Paper 26.x artifacts carry JVM 25 metadata.
configurations.matching { it.name.lowercase().endsWith("compileclasspath") }.configureEach {
    attributes.attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
}

tasks.compileJava {
    dependsOn(extractNewApi)
    classpath = files(newApiClasses, mythicLib, mlibApi) + classpath
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
