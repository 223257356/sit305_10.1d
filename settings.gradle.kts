pluginManagement {
    includeBuild("sjy-build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("core") {
            from(files("sjy-version-catalog-core/core.versions.toml"))
        }
        create("ui") {
            from(files("sjy-version-catalog-ui/ui.versions.toml"))
        }
        create("essentials") {
            from(files("sjy-version-catalog-essentials/essentials.versions.toml"))
        }
    }
}

rootProject.name = "SIT305_10.1D"
include(":app")
