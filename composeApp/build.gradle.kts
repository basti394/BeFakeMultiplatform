import org.jetbrains.compose.ExperimentalComposeLibrary

apply(plugin = "dev.icerock.mobile.multiplatform-resources")

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.serialization)
    id("com.squareup.sqldelight")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_11.toString()
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        androidMain {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.lifecycle)

                implementation(libs.koin.core)
                implementation(libs.koin.android)

                implementation(libs.ktor.client.android)

                implementation(libs.sqldelight.android.driver)
            }
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            api(compose.foundation)
            api(compose.animation)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            api(libs.mvvm.core)
            api(libs.mvvm.compose)
            api(libs.mvvm.flow)
            api(libs.mvvm.flow.compose)

            implementation(libs.resources)
            implementation(libs.resources.compose)

            implementation(libs.kmm.locale)

            val precompose_version = "1.5.10"
            api("moe.tlaster:precompose:$precompose_version")
            api("moe.tlaster:precompose-molecule:$precompose_version") // For Molecule intergration
            api("moe.tlaster:precompose-viewmodel:$precompose_version") // For ViewModel intergration
            api("moe.tlaster:precompose-koin:$precompose_version") // For Koin intergration

            implementation(libs.converter.gson)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation("com.benasher44:uuid:0.8.2")
            implementation("media.kamel:kamel-image:0.9.1")

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
        }
        iosMain {
            creating { }
            dependsOn(commonMain.get())
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.sqldelight.native.driver)
                implementation("app.cash.molecule:molecule-runtime:1.3.2")
                implementation(libs.ktor.client.ios)
                implementation(libs.koin.core)
            }
        }
    }
}

android {
    namespace = "pizza.xyz.befake.multiplatform"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "pizza.xyz.befake.multiplatform"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}
dependencies {
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.resources)
    implementation(libs.resources.compose)
    implementation(libs.androidx.tools.core)
}

sqldelight {
    database("BeFakeDatabase") {
        packageName = "pizza.xyz.befake.db"
        sourceFolders = listOf("sqldelight")
    }
}
