# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Retrofit 2.X
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Application classes that will be serialized/deserialized over Gson
#-keep class io.loqee.kairos.network.** { *; }
#-keep class io.loqee.kairos.network.controller.** { *; }
#-keep class io.loqee.kairos.network.viewModel.** { *; }

-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes EnclosingMethod

# Gson specific classes
-keep class com.google.gson.stream.** { *; }

# Keep model classes used with Retrofit
#-keep class io.loqee.kairos.network.viewModel.** { *; }

# Keep interface methods used with Retrofit
#-keep interface io.loqee.kairos.network.** { *; }

# Keep class members that are accessed via reflection
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep XML-related classes
-dontwarn org.kobjects.**
-dontwarn org.ksoap2.**
-dontwarn org.kxml2.**
-dontwarn org.xmlpull.v1.**

-keep class org.kobjects.** { *; }
-keep class org.ksoap2.** { *; }
-keep class org.kxml2.** { *; }
-keep class org.xmlpull.** { *; }

-keep interface org.kobjects.** { *; }
-keep interface org.ksoap2.** { *; }
-keep interface org.kxml2.** { *; }
-keep interface org.xmlpull.** { *; }

-dontwarn org.xmlpull.v1.**
-dontwarn org.kxml2.io.**
-dontwarn android.content.res.**
-dontwarn org.slf4j.impl.StaticLoggerBinder

-keep class org.xmlpull.** { *; }
-keepclassmembers class org.xmlpull.** { *; }

-dontwarn org.xmlpull.v1.**
-dontnote org.xmlpull.v1.**
-keep class org.xmlpull.** { *; }
-keepclassmembers class org.xmlpull.** { *; }

-keep class org.simpleframework.xml.** { *; }
-keep interface org.simpleframework.xml.** { *; }
-keepclassmembers class org.simpleframework.xml.** { *; }
-dontwarn org.simpleframework.xml.**
-dontnote org.simpleframework.xml.**

-dontwarn javax.xml.**

-keep public class org.simpleframework.** { *; }
-keep class org.simpleframework.xml.core.** { *; }
-keep class org.simpleframework.xml.util.** { *; }

-keepattributes Signature
-keepattributes *Annotation*

# Ignore our XML Serialization classes
-keep public class io.loqee.kairos.network.viewModel.horoscopes.* {
  public protected private *;
}