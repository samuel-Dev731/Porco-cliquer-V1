plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.porcocliquer"
    compileSdk = 35 // Base de compilação estável atual

    defaultConfig {
        applicationId = "com.porcocliquer"
        minSdk = 21      // Garante compatibilidade a partir do Android 5.0 (Lollipop)
        targetSdk = 35   // Alvo otimizado para as diretrizes de segurança modernas
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 🚀 CONFIGURAÇÃO DE BITS: Faz o seu arquivo .so rodar em 32 e 64 bits de fábrica!
        ndk {
            abiFilters.addAll(setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true   // Compacta o código para gastar menos memória
            isShrinkResources = true // Remove imagens ou XMLs não utilizados no APK final
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Componentes base do Android
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    
    // Lifecycle e Coroutines para melhor performance
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // 👑 OTIMIZAÇÃO EXCLUSIVA PARA SAMSUNG ONE UI
    
    // 1. Conexão com a Galaxy Store (para itens extras e compras do jogo)
    implementation("com.samsung.android.store:iap:6.1.0")

    // 2. Suporte Avançado para Telas Dobráveis (Galaxy Z Fold, Z Flip e Tablets)
    implementation("androidx.window:window:1.3.0")
}
