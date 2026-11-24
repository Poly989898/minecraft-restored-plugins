plugins {
    java
    id("com.gradleup.shadow") version "9.6.1"
}

group = "me.TechsCode"
version = "5.6.8"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(25)) } }

// Compile-only API stubs of optional integrations (never packaged).
val stubs by sourceSets.creating {
    java.setSrcDirs(listOf("stubs"))
}
sourceSets.main { compileClasspath += stubs.output }

val shaded by configurations.creating
configurations.implementation { extendsFrom(shaded) }

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.254-stable")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.mojang:authlib:3.11.50")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("net.md-5:bungeecord-api:1.21-R0.2")
    compileOnly("org.projectlombok:lombok:1.18.36")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") { isTransitive = false }

    shaded("com.google.code.gson:gson:2.13.2")
    shaded("commons-io:commons-io:2.20.0")
    shaded("commons-codec:commons-codec:1.19.0")
    shaded("org.apache.commons:commons-lang3:3.19.0")
    shaded("com.zaxxer:HikariCP:6.3.3")
    shaded("de.tr7zw:item-nbt-api:2.15.3")
    shaded("org.slf4j:slf4j-api:2.0.17")
    shaded("org.slf4j:slf4j-nop:2.0.17")
    shaded("org.bstats:bstats-bukkit:3.1.0")
    shaded("org.bstats:bstats-bungeecord:3.1.0")
    shaded("org.apache.httpcomponents:httpclient:4.5.14")
    shaded("org.jetbrains:annotations:24.1.0")

    add(stubs.compileOnlyConfigurationName, "io.papermc.paper:paper-api:26.3.build.254-stable")
    add(stubs.compileOnlyConfigurationName, "org.jetbrains:annotations:24.1.0")
}

configurations.matching { it.name.lowercase().endsWith("classpath") }.configureEach {
    attributes.attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
    options.compilerArgs.addAll(listOf("-proc:none", "-Xmaxerrs", "5000"))
}

tasks.jar { enabled = false }
tasks.shadowJar {
    archiveFileName.set("UltraPermissions-5.6.8-restored.jar")
    configurations = listOf(shaded)
    val base = "me.TechsCode.UltraPermissions.dependencies"
    relocate("com.google.gson", "$base.gson")
    relocate("org.apache.commons.io", "$base.commons.io")
    relocate("org.apache.commons.codec", "$base.commons.codec")
    relocate("org.apache.commons.lang3", "$base.commons.lang3")
    relocate("org.apache.commons.logging", "$base.commons.logging")
    relocate("com.zaxxer.hikari", "$base.hikari")
    relocate("de.tr7zw.changeme.nbtapi", "$base.nbt")
    relocate("org.slf4j", "$base.slf4j")
    relocate("org.bstats", "$base.bstats")
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/versions/**/module-info.class", "module-info.class")
    mergeServiceFiles()
}
tasks.build { dependsOn(tasks.shadowJar) }
