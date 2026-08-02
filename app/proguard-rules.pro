# Retrofit / OkHttp / Gson usan reflexion sobre los DTOs; se conservan al ofuscar en release.
-keep class com.puce.sigpel.data.remote.dto.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
