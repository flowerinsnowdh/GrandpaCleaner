pluginManagement {
    repositories {
        if (System.getenv("GRADLE_USE_MIRROR") == "true") {
            maven("https://repo.nju.edu.cn/maven/")
        } else {
            gradlePluginPortal()
        }
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "GrandpaCleaner"

