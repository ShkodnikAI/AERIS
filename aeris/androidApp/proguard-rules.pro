# AERIS ProGuard Rules

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.* { *; }
-keepnames class * extends dagger.hilt.android.internal.managers.*

# Room
-keep class androidx.room.** { *; }
-keepnames class * extends androidx.room.RoomDatabase
-keep class com.aeris.android.data.local.entity.** { *; }
-keep class com.aeris.android.data.local.dao.** { *; }
-dontwarn androidx.room.paging.**

# Health Connect
-keep class androidx.health.connect.** { *; }
-dontwarn androidx.health.connect.**

# Koin
-keep class org.koin.** { *; }
-keepnames class * extends org.koin.core.module.Module

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.aeris.**$$serializer { *; }
-keepclassmembers class com.aeris.** {
    *** Companion;
}
-keepclasseswithmembers class com.aeris.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Timber - strip debug/verbose logs in release
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# EncryptedSharedPreferences
-keep class androidx.security.crypto.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Keep domain models
-keep class com.aeris.domain.model.** { *; }
-keep class com.aeris.ai.** { *; }

# Keep ViewModels
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
