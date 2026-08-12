package com.puce.sigpel.ui.encargado

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puce.sigpel.R
import com.puce.sigpel.data.remote.dto.CategoriaEquipoResponse
import com.puce.sigpel.databinding.FragmentRegistrarEquipoBinding
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.ui.common.sigpelApp
import com.puce.sigpel.ui.common.simpleViewModelFactory
import com.puce.sigpel.ui.common.textOrNull
import com.puce.sigpel.util.UiState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/** Formulario de alta de equipo (HU-31), rol ENCARGADO. */
class RegistrarEquipoFragment : Fragment() {

    private var _binding: FragmentRegistrarEquipoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrarEquipoViewModel by viewModels {
        simpleViewModelFactory { RegistrarEquipoViewModel(sigpelApp.equipoRepository, sigpelApp.categoriaRepository) }
    }

    private var categorias: List<CategoriaEquipoResponse> = emptyList()
    private var categoriaSeleccionadaId: Long? = null
    private var imagenSeleccionadaUri: Uri? = null

    // Photo Picker del sistema: no requiere permisos de galeria en ninguna version de Android.
    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imagenSeleccionadaUri = uri
            binding.imageEquipo.setPadding(0, 0, 0, 0)
            binding.imageEquipo.imageTintList = null
            binding.imageEquipo.setImageURI(uri)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistrarEquipoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCategorias.setOnClickListener {
            findNavController().navigate(R.id.gestionCategoriasFragment)
        }

        val elegirImagen = {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.layoutImagen.setOnClickListener { elegirImagen() }
        binding.textSeleccionarImagen.setOnClickListener { elegirImagen() }

        binding.inputCategoria.setOnItemClickListener { _, _, position, _ ->
            categoriaSeleccionadaId = categorias.getOrNull(position)?.id
        }

        binding.buttonGuardar.setOnClickListener {
            val categoriaId = categoriaSeleccionadaId
            val nombre = binding.inputNombre.textOrNull()
            val serial = binding.inputSerial.textOrNull()
            when {
                categoriaId == null -> showFeedback(getString(R.string.form_categoria_hint) + " requerida", isError = true)
                nombre == null -> showFeedback(getString(R.string.form_error_nombre_requerido), isError = true)
                serial == null -> showFeedback(getString(R.string.form_serial_hint) + " requerido", isError = true)
                else -> viewModel.registrar(
                    categoriaId, nombre, serial, binding.inputDescripcion.textOrNull(), imagenAMultipart()
                )
            }
        }

        viewModel.categorias.observe(viewLifecycleOwner) { lista ->
            categorias = lista
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, lista.map { it.nombre })
            binding.inputCategoria.setAdapter(adapter)
        }

        viewModel.registrarState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progress.setVisible(true)
                    binding.buttonGuardar.isEnabled = false
                    binding.textFeedback.setVisible(false)
                }
                is UiState.Success -> {
                    binding.progress.setVisible(false)
                    binding.buttonGuardar.isEnabled = true
                    showFeedback(getString(R.string.registrar_equipo_exito), isError = false)
                    limpiarFormulario()
                }
                is UiState.Error -> {
                    binding.progress.setVisible(false)
                    binding.buttonGuardar.isEnabled = true
                    showFeedback(state.message, isError = true)
                }
                null -> Unit
            }
        }

        viewModel.cargarCategorias()
    }

    private fun imagenAMultipart(): MultipartBody.Part? {
        val uri = imagenSeleccionadaUri ?: return null
        val resolver = requireContext().contentResolver
        val mimeType = resolver.getType(uri) ?: "image/*"
        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val extension = mimeType.substringAfterLast('/', "jpg")
        return MultipartBody.Part.createFormData("file", "equipo.$extension", requestBody)
    }

    private fun limpiarFormulario() {
        binding.inputNombre.text?.clear()
        binding.inputSerial.text?.clear()
        binding.inputDescripcion.text?.clear()
        binding.inputCategoria.text?.clear()
        categoriaSeleccionadaId = null
        imagenSeleccionadaUri = null
        binding.imageEquipo.setPadding(28, 28, 28, 28)
        binding.imageEquipo.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        binding.imageEquipo.setImageResource(R.drawable.ic_devices)
    }

    private fun showFeedback(message: String, isError: Boolean) {
        binding.textFeedback.text = message
        binding.textFeedback.setTextColor(
            ContextCompat.getColor(requireContext(), if (isError) R.color.error else R.color.estado_disponible)
        )
        binding.textFeedback.setVisible(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
