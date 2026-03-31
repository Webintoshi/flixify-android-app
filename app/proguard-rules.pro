# ProGuard rules for Flixify Pro
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep serialized models
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Keep Hilt
-keepclassmembers @dagger.hilt.android.HiltAndroidApp class * {
    @dagger.hilt.android.HiltAndroidApp <init>(...);
}
