import org.gradle.kotlin.dsl.maven

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        maven("https://dl.bintray.com/rvalerio/maven")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven("https://dl.bintray.com/rvalerio/maven")
    }
}

rootProject.name = "TestMKO"
include(":app")
 