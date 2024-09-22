-ignorewarnings
-keepclassmembers enum * {
   public static **[] values();
   public static ** valueOf(java.lang.String);
}
-keep class kotlinx.coroutines.android.AndroidExceptionPreHandler { *; }