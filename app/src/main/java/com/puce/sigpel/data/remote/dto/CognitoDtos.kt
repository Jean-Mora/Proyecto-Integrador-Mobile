package com.puce.sigpel.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Peticion cruda contra Cognito (InitiateAuth, flujo USER_PASSWORD_AUTH).
 * Se envia directo al endpoint publico "https://cognito-idp.<region>.amazonaws.com/",
 * sin pasar por el backend SIGPEL: Cognito emite el JWT que luego se adjunta a cada
 * llamada al backend (ver AuthInterceptor).
 */
data class InitiateAuthRequest(
    @SerializedName("AuthFlow") val authFlow: String = "USER_PASSWORD_AUTH",
    @SerializedName("ClientId") val clientId: String,
    @SerializedName("AuthParameters") val authParameters: Map<String, String>
)

data class InitiateAuthResponse(
    @SerializedName("AuthenticationResult") val authenticationResult: AuthenticationResult?,
    @SerializedName("ChallengeName") val challengeName: String?
)

data class AuthenticationResult(
    @SerializedName("AccessToken") val accessToken: String,
    @SerializedName("IdToken") val idToken: String,
    @SerializedName("RefreshToken") val refreshToken: String?,
    @SerializedName("ExpiresIn") val expiresIn: Int,
    @SerializedName("TokenType") val tokenType: String
)

data class CognitoErrorResponse(
    @SerializedName("__type") val type: String?,
    @SerializedName("message") val message: String?
)
