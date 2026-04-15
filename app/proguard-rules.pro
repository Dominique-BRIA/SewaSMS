# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames

# For using SNAPSHOTS and main in "build-time constants"
-dontwarn com.google.errorprone.annotations.**

# For Kotlin
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-keepclasseswithmembers class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    native <methods>;
}

# Android
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep constructors with @Keep annotation
-keep class * {
    @androidx.annotation.Keep *;
}

# Keep all public classes and methods
-keepclasseswithmembers class com.sewasms.** {
    public <methods>;
    public <fields>;
}
