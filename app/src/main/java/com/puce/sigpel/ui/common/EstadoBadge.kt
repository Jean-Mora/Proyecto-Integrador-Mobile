package com.puce.sigpel.ui.common

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.puce.sigpel.R
import com.puce.sigpel.data.remote.dto.EstadoEquipo
import com.puce.sigpel.data.remote.dto.EstadoPrestamo
import com.puce.sigpel.data.remote.dto.TipoIncidencia

/** Colorea y etiqueta los badges de estado usados en catalogo, prestamos e incidencias. */
fun TextView.bindEstadoEquipo(estado: EstadoEquipo) {
    val (textRes, colorRes) = when (estado) {
        EstadoEquipo.DISPONIBLE -> R.string.estado_disponible to R.color.estado_disponible
        EstadoEquipo.PRESTADO -> R.string.estado_prestado to R.color.estado_prestado
        EstadoEquipo.MANTENIMIENTO -> R.string.estado_mantenimiento to R.color.estado_mantenimiento
    }
    text = context.getString(textRes)
    backgroundTintList = ContextCompat.getColorStateList(context, colorRes)
}

fun TextView.bindEstadoPrestamo(estado: EstadoPrestamo) {
    val (textRes, colorRes) = when (estado) {
        EstadoPrestamo.PENDIENTE -> R.string.estado_pendiente to R.color.estado_pendiente
        EstadoPrestamo.APROBADO -> R.string.estado_aprobado to R.color.estado_aprobado
        EstadoPrestamo.RECHAZADO -> R.string.estado_rechazado to R.color.estado_rechazado
        EstadoPrestamo.DEVUELTO -> R.string.estado_devuelto to R.color.estado_devuelto
    }
    text = context.getString(textRes)
    backgroundTintList = ContextCompat.getColorStateList(context, colorRes)
}

fun tipoIncidenciaLabel(tipo: TipoIncidencia): Int = when (tipo) {
    TipoIncidencia.DANIO -> R.string.tipo_danio
    TipoIncidencia.PERDIDA -> R.string.tipo_perdida
    TipoIncidencia.RETRASO -> R.string.tipo_retraso
}
