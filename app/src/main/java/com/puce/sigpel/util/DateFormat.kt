package com.puce.sigpel.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Formatea los Instant (llegan como String ISO-8601 desde el backend) para mostrarlos en la UI. */
object DateFormat {
    private val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault())
    private val displayDateOnly = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault())

    fun formatDateTime(iso: String?): String {
        if (iso.isNullOrBlank()) return "-"
        return runCatching { displayFormatter.format(Instant.parse(iso)) }.getOrDefault(iso)
    }

    fun formatDate(iso: String?): String {
        if (iso.isNullOrBlank()) return "-"
        return runCatching { displayDateOnly.format(Instant.parse(iso)) }.getOrDefault(iso)
    }

    /** Convierte epoch millis (DatePicker) al Instant ISO-8601 que espera el backend. */
    fun epochMillisToIso(epochMillis: Long): String = Instant.ofEpochMilli(epochMillis).toString()
}
