plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-rc1"
    id("io.papermc.paperweight.userdev") version "2.0.0-SNAPSHOT"
}

group = "cn.flowerinsnow.grandpacleaner"
version = "1.1.0"

repositories {
    if (System.getenv("GRADLE_USE_MIRROR") == "true") {
        maven("https://repo.nju.edu.cn/maven/")
    } else {
        mavenCentral()
    }
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")
    paperweight.paperDevBundle("1.21.7-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:26.0.2")

    implementation("cc.carm.lib:mineconfiguration-bukkit:3.1.3")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.shadowJar {
    from("LICENSE")
    from("NOTICE")

    archiveClassifier = ""

    listOf(
        "cc.carm.lib",
        "com.cryptomorin.xseries"
    ).forEach {
        relocate(it, "cn.flowerinsnow.grandpacleaner.shaded.$it")
    }
}

tasks.processResources {
    val replaceProperties = mapOf(
            "version" to project.version
    )
    replaceProperties.forEach(inputs::property)
    filesMatching("plugin.yml") {
        expand(replaceProperties)
    }
}

tasks.build.get().dependsOn(tasks.shadowJar)