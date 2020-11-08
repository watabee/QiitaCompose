package dependencies

object Versions {
    const val androidCompileSdkVersion = 30
    const val androidMinSdkVersion = 21
    const val androidTargetSdkVersion = 30

    const val buildToolsVersion = "30.0.2"

    private const val versionMajor = 1
    private const val versionMinor = 0
    private const val versionPatch = 0
    private const val versionOffset = 0
    const val androidVersionCode = (versionMajor * 10000 + versionMinor * 100 + versionPatch) * 100 + versionOffset
    const val androidVersionName = "$versionMajor.$versionMinor.$versionPatch"
}

object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:4.2.0-alpha15"

    object Kotlin {
        const val version = "1.4.10"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10"

        object Coroutines {
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.1"
        }
    }

    object Google {
        const val material = "com.google.android.material:material:1.3.0-alpha03"
        const val composeThemeAdapter = "com.google.android.material:compose-theme-adapter:${AndroidX.Compose.version}"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0-alpha02"
        const val activity = "androidx.activity:activity-ktx:1.2.0-beta01"
        const val fragment = "androidx.fragment:fragment-ktx:1.3.0-beta01"
        const val core = "androidx.core:core-ktx:1.5.0-alpha04"

        object Test {
            const val core = "androidx.test:core:1.3.0"
            const val runner = "androidx.test:runner:1.3.0"
            const val rules = "androidx.test:rules:1.3.0"
            const val espressoCore = "androidx.test.espresso:espresso-core:3.3.0"
        }

        object Lifecycle {
            const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:2.3.0-beta01"
            const val compiler = "androidx.lifecycle:lifecycle-compiler:2.3.0-beta01"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0-beta01"
        }

        object Compose {
            const val version = "1.0.0-alpha06"
            const val runtime = "androidx.compose.runtime:runtime:$version"
            const val ui = "androidx.compose.ui:ui:$version"
            const val uiTooling = "androidx.ui:ui-tooling:$version"
            const val foundation = "androidx.compose.foundation:foundation:$version"
            const val foundationLayout = "androidx.compose.foundation:foundation-layout:$version"
            const val material = "androidx.compose.material:material:$version"
            const val runtimeLiveData = "androidx.compose.runtime:runtime-livedata:$version"
            const val themeAdapter = "androidx.compose.material:compose-theme-adapter:$version"
        }

        object Hilt {
            const val viewmodel = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha02"
            const val compiler = "androidx.hilt:hilt-compiler:1.0.0-alpha02"
        }
    }

    object OkHttp {
        const val okhttp = "com.squareup.okhttp3:okhttp:4.9.0"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.9.0"
    }

    object Dagger {
        object Hilt {
            const val android = "com.google.dagger:hilt-android:2.29.1-alpha"
            const val androidCompiler = "com.google.dagger:hilt-android-compiler:2.29.1-alpha"
            const val androidGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:2.29.1-alpha"
        }
    }

    object Coil {
        const val coil = "io.coil-kt:coil:1.0.0"
    }

    object Accompanist {
        const val coil = "dev.chrisbanes.accompanist:accompanist-coil:0.3.2"
    }

    const val timber = "com.jakewharton.timber:timber:4.7.1"

    const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:1.0.10"

    const val junit = "junit:junit:4.13"
    const val truth = "com.google.truth:truth:1.0.1"
}
