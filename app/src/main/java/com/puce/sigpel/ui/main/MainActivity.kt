package com.puce.sigpel.ui.main

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView
import com.puce.sigpel.R
import com.puce.sigpel.SigpelApp
import com.puce.sigpel.data.auth.Role

/**
 * Shell de la app (ver docs/sigpel_pantallas_moviles.md #4). El layout es
 * responsive: activity_main.xml usa BottomNavigationView en telefonos y
 * layout-sw600dp/activity_main.xml usa NavigationRailView en tablets; ambos
 * comparten el id nav_view y el menu bottom_nav_menu.xml, asi que el codigo
 * de aqui no necesita saber cual esta inflado (NavigationBarView es la clase
 * base comun de Material Components para ambos).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var navBarView: NavigationBarView
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val app get() = application as SigpelApp

    private val topLevelDestinations = setOf(
        R.id.catalogoFragment,
        R.id.misPrestamosFragment,
        R.id.gestionEquiposFragment,
        R.id.solicitudesPendientesFragment,
        R.id.nuevaIncidenciaFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        navBarView = findViewById(R.id.nav_view)

        // No usar findNavController(viewId) aqui: en onCreate() el NavHostFragment ya
        // esta agregado pero su vista todavia no se crea (llega a onCreateView recien en
        // onStart), asi que el tag de NavController sobre el FragmentContainerView aun no
        // existe. El NavController en si ya esta disponible desde el fragmento.
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        setSupportActionBar(toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        navBarView.setupWithNavController(navController)

        toolbar.inflateMenu(R.menu.top_app_bar_menu)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_session) {
                onSessionActionClicked(navController)
                true
            } else {
                false
            }
        }

        navController.addOnDestinationChangedListener { _, _, _ -> refreshRoleUi() }
        refreshRoleUi()
    }

    override fun onResume() {
        super.onResume()
        refreshRoleUi()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun onSessionActionClicked(navController: androidx.navigation.NavController) {
        if (app.authRepository.isLoggedIn) {
            AlertDialog.Builder(this)
                .setTitle(R.string.action_logout)
                .setMessage(R.string.action_logout_confirm_msg)
                .setPositiveButton(R.string.si) { _, _ ->
                    app.authRepository.logout()
                    refreshRoleUi()
                    if (navController.currentDestination?.id !in topLevelDestinations) {
                        navController.navigate(R.id.catalogoFragment)
                    }
                }
                .setNegativeButton(R.string.no, null)
                .show()
        } else if (navController.currentDestination?.id != R.id.loginFragment) {
            navController.navigate(R.id.loginFragment)
        }
    }

    private fun refreshRoleUi() {
        val role = app.authRepository.currentRole
        navBarView.menu.findItem(R.id.misPrestamosFragment)?.isVisible = role == Role.ESTUDIANTE
        navBarView.menu.findItem(R.id.gestionEquiposFragment)?.isVisible = role == Role.ENCARGADO
        navBarView.menu.findItem(R.id.solicitudesPendientesFragment)?.isVisible = role == Role.ENCARGADO
        navBarView.menu.findItem(R.id.nuevaIncidenciaFragment)?.isVisible = role == Role.ENCARGADO

        toolbar.menu.findItem(R.id.action_session)?.title =
            if (app.authRepository.isLoggedIn) getString(R.string.action_logout) else getString(R.string.action_login)
    }
}
