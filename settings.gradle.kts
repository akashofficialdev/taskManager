pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        jcenter()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") } // Add JitPack repository here
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        jcenter()
        maven { setUrl("https://jitpack.io") } // Add JitPack repository here
    }
}

rootProject.name = "TaskManager"
include(":app")
