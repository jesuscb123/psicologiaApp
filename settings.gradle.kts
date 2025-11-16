// settings.gradle.kts

pluginManagement {
    repositories {
        google()         // Sin restricciones
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()         // Sin restricciones
        mavenCentral()
    }
}

rootProject.name = "psicologiaApp"
include(":app")