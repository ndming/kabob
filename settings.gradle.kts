rootProject.name = "kabob"

// Generate strongly-typed accessors for subprojects
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Where Gradle should look for plugins when they are declared in the project
pluginManagement {
    repositories {
        google {
            @Suppress("UnstableApiUsage")
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// How dependencies for all subprojects/modules in the build should be resolved
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":core")
include(":markdown")
include(":pages:Main")
include(":pages:FourierSeries")
include(":pages:Metaballs")
include(":pages:Pendulum")