package com.puce.sigpel.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Guarda el JWT de Cognito y el rol resuelto en EncryptedSharedPreferences
 * (ver docs/sigpel_pantallas_moviles.md, pantalla 3.2 Login).
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "sigpel_session",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    var idToken: String?
        get() = prefs.getString(KEY_ID_TOKEN, null)
        private set(value) = prefs.edit().putString(KEY_ID_TOKEN, value).apply()

    var accessToken: String?
        get() = prefs.getString(KEY_ACCESS_TOKEN, null)
        private set(value) = prefs.edit().putString(KEY_ACCESS_TOKEN, value).apply()

    var username: String?
        get() = prefs.getString(KEY_USERNAME, null)
        private set(value) = prefs.edit().putString(KEY_USERNAME, value).apply()

    var role: Role
        get() = prefs.getString(KEY_ROLE, null)?.let { Role.valueOf(it) } ?: Role.VISITANTE
        private set(value) = prefs.edit().putString(KEY_ROLE, value.name).apply()

    private var expiresAt: Long?
        get() = prefs.getLong(KEY_EXPIRES_AT, -1L).takeIf { it >= 0 }
        set(value) = prefs.edit().putLong(KEY_EXPIRES_AT, value ?: -1L).apply()

    val isLoggedIn: Boolean
        get() = idToken != null && !JwtUtils.isExpired(expiresAt)

    fun saveSession(idToken: String, accessToken: String) {
        val parsed = JwtUtils.parse(idToken)
        this.idToken = idToken
        this.accessToken = accessToken
        this.username = parsed?.username
        this.role = Role.fromCognitoGroups(parsed?.groups)
        this.expiresAt = parsed?.expiresAtEpochSeconds
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_ID_TOKEN = "id_token"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_USERNAME = "username"
        const val KEY_ROLE = "role"
        const val KEY_EXPIRES_AT = "expires_at"
    }
}
