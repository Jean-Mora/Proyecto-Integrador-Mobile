package com.puce.sigpel

import android.app.Application
import com.puce.sigpel.data.auth.SessionManager
import com.puce.sigpel.data.remote.NetworkModule
import com.puce.sigpel.data.repository.AuthRepository
import com.puce.sigpel.data.repository.CategoriaRepository
import com.puce.sigpel.data.repository.EquipoRepository
import com.puce.sigpel.data.repository.IncidenciaRepository
import com.puce.sigpel.data.repository.PrestamoRepository

/**
 * Contenedor de dependencias manual (sin Hilt/Koin) para mantener el proyecto
 * simple: cada Repository/Manager es un singleton expuesto por la Application.
 */
class SigpelApp : Application() {

    lateinit var sessionManager: SessionManager
        private set
    lateinit var authRepository: AuthRepository
        private set
    lateinit var equipoRepository: EquipoRepository
        private set
    lateinit var categoriaRepository: CategoriaRepository
        private set
    lateinit var prestamoRepository: PrestamoRepository
        private set
    lateinit var incidenciaRepository: IncidenciaRepository
        private set

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)
        val apiService = NetworkModule.provideApiService(sessionManager)
        val cognitoAuthApi = NetworkModule.provideCognitoAuthApi()

        authRepository = AuthRepository(cognitoAuthApi, sessionManager)
        equipoRepository = EquipoRepository(apiService)
        categoriaRepository = CategoriaRepository(apiService)
        prestamoRepository = PrestamoRepository(apiService, sessionManager)
        incidenciaRepository = IncidenciaRepository(apiService)
    }
}
