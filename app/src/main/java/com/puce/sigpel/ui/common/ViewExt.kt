package com.puce.sigpel.ui.common

import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.puce.sigpel.R
import com.puce.sigpel.SigpelApp

fun View.visible() { visibility = View.VISIBLE }
fun View.gone() { visibility = View.GONE }
fun View.setVisible(isVisible: Boolean) { visibility = if (isVisible) View.VISIBLE else View.GONE }

fun Fragment.toast(message: String) {
    context?.let { Toast.makeText(it, message, Toast.LENGTH_SHORT).show() }
}

fun EditText.textOrNull(): String? = text?.toString()?.trim()?.takeIf { it.isNotEmpty() }

/** Spinner de refresco con los colores de marca PUCE en vez del verde por defecto de Material. */
fun SwipeRefreshLayout.applyBrandColors() {
    setColorSchemeResources(R.color.puce_blue, R.color.puce_gold)
}

val Fragment.sigpelApp: SigpelApp get() = requireActivity().application as SigpelApp

/** Evita escribir una clase Factory por cada ViewModel: los repos vienen de SigpelApp. */
inline fun <reified VM : ViewModel> simpleViewModelFactory(crossinline create: () -> VM): ViewModelProvider.Factory =
    viewModelFactory {
        initializer { create() }
    }
