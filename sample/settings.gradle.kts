@Suppress("UnstableApiUsage")
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

rootProject.name = "webrtc-kmp-sample"

// Include the library as a composite build so that `com.shepeliev:webrtc-kmp`
// is substituted with the local :webrtc-kmp project at build time.
includeBuild("../") {
    dependencySubstitution {
        substitute(module("com.shepeliev:webrtc-kmp")).using(project(":webrtc-kmp"))
    }
}

include(":composeApp")
