package com.puce.sigpel.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.puce.sigpel.R
import com.puce.sigpel.SigpelApp
import com.puce.sigpel.ui.main.MainActivity

/**
 * SplashActivity del flujo (ver docs/sigpel_pantallas_moviles.md #2). El catalogo
 * es publico, asi que aqui solo se restaura la sesion guardada (si existe y no
 * expiro) antes de entrar a MainActivity; no se fuerza el login.
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Toca isLoggedIn para descartar sesiones expiradas antes de abrir MainActivity.
        (application as SigpelApp).authRepository.isLoggedIn

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
