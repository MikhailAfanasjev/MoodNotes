plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)

    id("kotlin-kapt")
}

android {
    namespace = "com.example.moodnotes"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.moodnotes"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)  // Основная библиотека Android KTX
    implementation(libs.androidx.lifecycle.runtime.ktx)  // Жизненный цикл KTX
    implementation(libs.androidx.activity.compose)  // Поддержка компонентов Activity для Compose
    implementation(platform(libs.androidx.compose.bom))  // BOM для управления версиями Compose
    implementation(libs.androidx.ui)  // Основной модуль Compose UI
    implementation(libs.androidx.ui.graphics)  // Библиотека графики Compose
    implementation(libs.androidx.ui.tooling.preview)  // Поддержка предпросмотра в Android Studio
    implementation(libs.androidx.material3)  // Material Design 3 для Jetpack Compose
    implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")

    implementation(libs.androidx.room.runtime) // Основная библиотека Room
    kapt(libs.androidx.room.compiler) // Компилятор Room для аннотаций
    implementation(libs.androidx.room.ktx) // KTX версия Room для удобства использования
    implementation ("androidx.core:core-ktx:1.13.1")


    // Тестирование
    testImplementation(libs.junit)  // JUnit для юнит-тестирования
    androidTestImplementation(libs.androidx.junit)  // JUnit для UI тестирования
    androidTestImplementation(libs.androidx.espresso.core)  // Espresso для UI тестов
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)  // JUnit 4 для тестирования UI Compose

    // Дебаг зависимости
    debugImplementation(libs.androidx.ui.tooling)  // Предпросмотр инструментов
    debugImplementation(libs.androidx.ui.test.manifest)  // Манифест для тестирования
}
kapt {
    correctErrorTypes = true
}
