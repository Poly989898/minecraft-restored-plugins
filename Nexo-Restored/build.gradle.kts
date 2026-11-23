plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.24"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.extendedclip.com/releases/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    maven("https://repo.enginehub.org/repo/")
    maven("https://jitpack.io")
    maven("https://repo.glaremasters.me/repository/towny/")
}

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

base { archivesName.set("nexo"); version = "1.26-restored" }

sourceSets { main { java.srcDir("src"); java.srcDir("stubs"); resources.srcDir("src"); resources.exclude("**/*.java"); java { exclude("com/google/**", "com/nexomc/nexo/nms/v1_21_R*/**", "com/nexomc/nexo/nms/v26_R1/**") } } }

dependencies {
    compileOnly("net.Indyuce:MMOItems-API:6.10.1-SNAPSHOT") { isTransitive = false }
    compileOnly("io.lumine:MythicLib-dist:1.7.1-SNAPSHOT") { isTransitive = false }
    compileOnly("software.amazon.awssdk:s3:2.29.0")
    paperweight.paperDevBundle("26.3.build.254-stable")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.2.20") // provided by the server at runtime (the original jar does not bundle it)
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("net.kyori:adventure-platform-bukkit:4.4.1")
    implementation("com.udojava:EvalEx:2.7")
    compileOnly("com.mojang:authlib:7.0.63")
    implementation("com.google.errorprone:error_prone_annotations:2.36.0")
    compileOnly("org.jetbrains:annotations:26.1.0")
    compileOnly("org.checkerframework:checker-qual:3.42.0")
    compileOnly("com.github.GriefPrevention:GriefPrevention:16.18.4") { isTransitive = false }
    compileOnly("com.github.Zrips:Residence:6.0.2.3") { isTransitive = false }
    compileOnly("com.github.angeschossen:LandsAPI:7.25.4") { isTransitive = false }
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core:7.6.0") { isTransitive = false }
    compileOnly("com.palmergames.bukkit.towny:towny:0.100.2.11@jar") { isTransitive = false }
    compileOnly("com.google.inject:guice:7.0.0") { isTransitive = false }
    for (dep in listOf(
        "com.ticxo.modelengine:ModelEngine:R4.0.9", "io.lumine:Mythic-Dist:5.14.0",
        "org.apache.httpcomponents.client5:httpclient5:5.4.1", "org.apache.httpcomponents.core5:httpcore5:5.3.1",
        "org.springframework:spring-expression:6.1.14", "org.springframework:spring-core:6.1.14",
        "me.clip:placeholderapi:2.11.6", "nl.rutgerkok:blocklocker:1.13",
        "com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.16.2", "org.enginehub.lin-bus:lin-bus-tree:0.2.0", "org.enginehub.lin-bus:lin-bus-common:0.2.0", "org.enginehub.lin-bus:lin-bus-stream:0.2.0", "com.sk89q.worldedit:worldedit-bukkit:7.3.9", "com.sk89q.worldedit:worldedit-core:7.3.9",
        "com.sk89q.worldguard:worldguard-bukkit:7.0.12", "com.sk89q.worldguard:worldguard-core:7.0.12")) {
        compileOnly(dep) { isTransitive = false }
    }
}

tasks.withType<JavaCompile> { options.encoding = "UTF-8"; options.compilerArgs.addAll(listOf("-Xmaxerrs", "100000", "-proc:none", "-XDshould-stop.ifError=FLOW")) }

tasks.withType<JavaCompile> { options.isFork = true; options.forkOptions.memoryMaximumSize = "6g" }

// The compile-only Duration stub (stubs/kotlin/time) cannot spell Kotlin's hyphenated inline-class JVM names,
// so it uses underscores. Rewrite those call sites to the real names (same length, byte-for-byte in the class files).
// Only classes that reference kotlin/time/Duration are touched; the stub itself is not packaged.
tasks.named<JavaCompile>("compileJava") {
    doLast {
        val mangled = Regex("(box|unbox|compareTo|isPositive|isNegative|isInfinite|isFinite|getInWhole[A-Za-z]+|plus|minus|times|div|hashCode|toString|equals|toDouble|toLong|toInt|getZERO|getINFINITE)_(impl0|impl|[A-Za-z0-9]{7})")
        destinationDirectory.get().asFile.walkTopDown().filter { it.isFile && it.extension == "class" }.forEach { f ->
            val text = String(f.readBytes(), Charsets.ISO_8859_1)
            if (text.contains("kotlin/time/Duration")) {
                val fixed = mangled.replace(text) { it.value.replace('_', '-') }
                if (fixed != text) f.writeBytes(fixed.toByteArray(Charsets.ISO_8859_1))
            }
        }
    }
}

tasks.jar {
    dependsOn(configurations.runtimeClasspath)
    from({ configurations.runtimeClasspath.get().filter { it.name.endsWith(".jar") }.map { zipTree(it) } }) {
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/versions/**/module-info.class", "module-info.class")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    // compile-only stubs (signatures of optional plugins/libraries that are not on the build classpath) must not ship:
    // every class compiled from stubs/ is dropped from the jar, the rest of the classes stay
    val stubClassNames = fileTree("stubs") { include("**/*.java") }.files.map { it.relativeTo(file("stubs")).invariantSeparatorsPath.removeSuffix(".java") }
    val mainClassesDir = sourceSets.main.get().output.classesDirs.files.map { it.invariantSeparatorsPath }
    exclude { el ->
        el.path.endsWith(".class") && stubClassNames.any { n -> el.path == "$n.class" || el.path.startsWith("$n$") } &&
            mainClassesDir.any { el.file.invariantSeparatorsPath.startsWith(it) }
    }
}


tasks.register("printCp") { doLast { println("CP=" + sourceSets.main.get().compileClasspath.asPath) } }
