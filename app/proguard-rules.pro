# Project-specific ProGuard/R8 rules.

# Room compiler rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.RoomDatabase

# Keep original line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable

