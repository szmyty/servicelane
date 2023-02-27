@file:Suppress("UnstableApiUsage")

// https://docs.gradle.org/current/userguide/build_cache.html
buildCache {
    local {
        directory = File(rootDir, ".cache")
        removeUnusedEntriesAfterDays = 7
    }
}

pluginManagement {
    val springBootVersion: String by settings
    val jibPluginVersion: String by settings
    val gitPropertiesPluginVersion: String by settings
    val openapiPluginVersion: String by settings
    val gradleNodePluginVersion: String by settings
    val liquibasePluginVersion: String by settings
    val sonarqubePluginVersion: String by settings
    val noHttpCheckstyleVersion: String by settings
    val modernizerPluginVersion: String by settings

    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")

            // https://github.com/ben-manes/gradle-versions-plugin/issues/541#issuecomment-957483372
            content {
                excludeGroupByRegex("com/github/ben-manes/*")
            }
        }
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
        // jhipster-needle-gradle-plugin-management-repositories - JHipster will add additional entries here
    }
    plugins {
        id("org.springframework.boot") version springBootVersion
        id("com.google.cloud.tools.jib") version jibPluginVersion
        id("com.gorylenko.gradle-git-properties") version gitPropertiesPluginVersion
        id("org.openapi.generator") version openapiPluginVersion
        id("com.github.node-gradle.node") version gradleNodePluginVersion
        id("org.liquibase.gradle") version liquibasePluginVersion
        id("org.sonarqube") version sonarqubePluginVersion
        id("io.spring.nohttp") version noHttpCheckstyleVersion
        id("com.github.andygoossens.gradle-modernizer-plugin") version modernizerPluginVersion
        // jhipster-needle-gradle-plugin-management-plugins - JHipster will add additional entries here
    }
}

rootProject.name = "app"
