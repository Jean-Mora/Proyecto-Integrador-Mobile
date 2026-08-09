package com.puce.sigpel.ui.prestamos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
// Importa aquí tu modelo/DTO de préstamo según tu proyecto
// import com.puce.sigpel.data.remote.PrestamoDto

class MisPrestamosViewModel : ViewModel() {

    // private val repository = PrestamoRepository() // Ajusta según tu arquitectura de red

    private val _prestamos = MutableLiveData<List<Any>>() // Cambia 'Any' por tu DTO de Préstamo
    val prestamos: LiveData<List<Any>> get() = _prestamos

    fun cargarMisPrestamos() {
        // Aquí se realizará la llamada a Retrofit para consumir GET /prestamos/me
    }
}