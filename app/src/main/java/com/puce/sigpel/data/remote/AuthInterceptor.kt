package com.puce.sigpel.data.remote

import com.puce.sigpel.data.auth.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adjunta el ID Token de Cognito como Bearer en cada llamada al backend.
 * Los GET publicos (/equipos, /categorias) funcionan igual sin sesion; el
 * resto responde 401/403 y cada Repository lo traduce a un mensaje de UI.
 */
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = sessionManager.idToken
        val request = if (token != null) {
            original.newBuilder().addHeader("Authorization", "Bearer $token").build()
        } else {
            original
        }
        return chain.proceed(request)
    }
}
