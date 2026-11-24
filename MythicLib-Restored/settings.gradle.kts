pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}
rootProject.name = "MythicLib"
// Paperweight module with the Mojang-mapped NMS wrapper for Paper 26.x.
include("nms-v26")
