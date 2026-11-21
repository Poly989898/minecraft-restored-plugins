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
    maven("https://repo.unnamed.team/repository/unnamed-public/")
}

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

sourceSets { main { java.srcDir("src"); java.srcDir("stubs"); resources.srcDir("src") ; java { exclude("com/google/**", "team/**", "com/nexomc/libs/**", "com/nexomc/customblockdata/**", "com/nexomc/protectionlib/**", "com/nexomc/nexo/nms/v1_21_R*/**", "com/nexomc/nexo/nms/v26_R1/**") } } }

dependencies {
    compileOnly(files("libs/nexo-1.26-patched.jar"))
    compileOnly(files("../MythicMobs-Restored/dist/MythicMobs-5.14.0-rebuilt.jar"))
    compileOnly(files("libs/MMOItems-API-6.10.1-SNAPSHOT.jar", "libs/MythicLib-dist-1.7.1-SNAPSHOT.jar"))
    compileOnly("software.amazon.awssdk:s3:2.29.0")
    paperweight.paperDevBundle("26.3.build.254-stable")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.google.errorprone:error_prone_annotations:2.36.0")
    for (dep in listOf(
        "com.ticxo.modelengine:ModelEngine:R4.0.9", "io.lumine:Mythic-Dist:5.6.1",
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

tasks.withType<JavaCompile> { options.isFork = true; options.forkOptions.memoryMaximumSize = "6g" }

val extraExcludes: List<String> = file("excludes.txt").takeIf { it.exists() }?.readLines()?.filter { it.isNotBlank() } ?: emptyList()
if (System.getenv("NOEXCL") == null) sourceSets.main { java.exclude(extraExcludes) }

tasks.register("printCp") { doLast { println("CP=" + sourceSets.main.get().compileClasspath.asPath) } }
