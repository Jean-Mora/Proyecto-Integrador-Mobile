package com.puce.sigpel.data.repository

import com.google.gson.Gson
import com.puce.sigpel.BuildConfig
import com.puce.sigpel.data.auth.Role
import com.puce.sigpel.data.auth.SessionManager
import com.puce.sigpel.data.remote.CognitoAuthApi
import com.puce.sigpel.data.remote.dto.CognitoErrorResponse
import com.puce.sigpel.data.remote.dto.InitiateAuthRequest

/** Login contra Cognito (ver pantalla 3.2 del md) y estado de sesion local. */
class AuthRepository(
    private val cognitoAuthApi: CognitoAuthApi,
    private val sessionManager: SessionManager
) {
    val isLoggedIn: Boolean get() = sessionManager.isLoggedIn
    val currentRole: Role get() = if (sessionManager.isLoggedIn) sessionManager.role else Role.VISITANTE
    val currentUsername: String? get() = sessionManager.username

    suspend fun login(username: String, password: String): Result<Role> = runCatching {
        val request = InitiateAuthRequest(
            clientId = BuildConfig.COGNITO_CLIENT_ID,
            authParameters = mapOf("USERNAME" to username, "PASSWORD" to password)
        )
        val response = cognitoAuthApi.initiateAuth(request)
        val result = response.body()?.authenticationResult

        if (!response.isSuccessful || result == null) {
            val errorJson = response.errorBody()?.string()
            val parsedError = errorJson?.let { runCatching { Gson().fromJson(it, CognitoErrorResponse::class.java) }.getOrNull() }
            error(parsedError?.message ?: "No se pudo iniciar sesión. Verifica tus credenciales.")
        }

        sessionManager.saveSession(idToken = result.idToken, accessToken = result.accessToken)
        sessionManager.role
    }

    fun logout() {
        sessionManager.clear()
    }
}
