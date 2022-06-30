# Remove log messages except warnings and errors
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Don't do anything except stripping log messages
-dontobfuscate
-dontwarn **.**
-target 1.7
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!code/allocation/variable
-keep class *{*;}
-keepclassmembers class *{*;}
-keepattributes *