package com.puce.sigpel.data.auth

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * Lectura local (sin verificar firma) del payload del ID Token de Cognito,
 * solo para poblar la UI (rol, nombre de usuario). La validacion real de firma
 * y expiracion la hace el backend (Spring Security oauth2ResourceServer, ver
 * Backend/.../config/SecurityConfig.kt).
 */
object JwtUtils {

    private data class CognitoClaims(
        @SerializedName("cognito:username") val cognitoUsername: String?,
        @SerializedName("cognito:groups") val cognitoGroups: List<String>?,
        @SerializedName("sub") val sub: String?,
        @SerializedName("exp") val exp: Long?
    )

    data class ParsedToken(
        val username: String,
        val groups: List<String>,
        val expiresAtEpochSeconds: Long?
    )

    fun parse(idToken: String): ParsedToken? {
        val parts = idToken.split(".")
        if (parts.size != 3) return null
        return runCatching {
            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING))
            val claims = Gson().fromJson(payloadJson, CognitoClaims::class.java)
            ParsedToken(
                username = claims.cognitoUsername ?: claims.sub.orEmpty(),
                groups = claims.cognitoGroups.orEmpty(),
                expiresAtEpochSeconds = claims.exp
            )
        }.getOrNull()
    }

    fun isExpired(expiresAtEpochSeconds: Long?): Boolean {
        if (expiresAtEpochSeconds == null) return false
        val nowSeconds = System.currentTimeMillis() / 1000
        return nowSeconds >= expiresAtEpochSeconds
    }
}
