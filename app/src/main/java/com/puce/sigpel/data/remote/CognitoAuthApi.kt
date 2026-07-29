package com.puce.sigpel.data.remote

import com.puce.sigpel.data.remote.dto.InitiateAuthRequest
import com.puce.sigpel.data.remote.dto.InitiateAuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Llamada directa (sin SDK) al endpoint publico de Cognito para el flujo
 * USER_PASSWORD_AUTH. Ver docs/sigpel_pantallas_moviles.md punto 5: pendiente
 * decidir si se migra a AWS Amplify Auth; mientras tanto esta llamada REST
 * evita agregar el SDK completo de AWS solo para InitiateAuth.
 */
interface CognitoAuthApi {
    @Headers(
        "Content-Type: application/x-amz-json-1.1",
        "X-Amz-Target: AWSCognitoIdentityProviderService.InitiateAuth"
    )
    @POST("/")
    suspend fun initiateAuth(@Body request: InitiateAuthRequest): Response<InitiateAuthResponse>
}
