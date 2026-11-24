plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.24"
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(25)) } }

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("26.3.build.254-stable")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("commons-lang:commons-lang:2.6")
    compileOnly("org.apache.commons:commons-lang3:3.22.0")
    // Plugin classes (VersionWrapper, NBTItem, ...): the wrapper implements the plugin's interface.
    compileOnly(files(rootProject.layout.buildDirectory.dir("classes/java/main")))
    compileOnly("com.google.code.gson:gson:2.8.9")
}

tasks.compileJava { dependsOn(":compileJava") }

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
    options.compilerArgs.addAll(listOf("-proc:none", "-Xmaxerrs", "5000"))
}

configurations.matching { it.name.lowercase().endsWith("compileclasspath") }.configureEach {
    attributes.attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
}
