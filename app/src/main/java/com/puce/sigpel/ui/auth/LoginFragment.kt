package com.puce.sigpel.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.puce.sigpel.R
import com.puce.sigpel.databinding.FragmentLoginBinding
import com.puce.sigpel.ui.common.gone
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.ui.common.sigpelApp
import com.puce.sigpel.ui.common.simpleViewModelFactory
import com.puce.sigpel.ui.common.textOrNull
import com.puce.sigpel.ui.common.toast
import com.puce.sigpel.ui.common.visible
import com.puce.sigpel.util.UiState

/** Pantalla 3.2 del md: login contra Cognito (HU-18). El destino post-login (catálogo) y el
 * botón de "continuar como visitante" se conectan en HU-19, cuando esa pantalla exista. */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        simpleViewModelFactory { AuthViewModel(sigpelApp.authRepository) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            val username = binding.inputUsername.textOrNull()
            val password = binding.inputPassword.textOrNull()
            if (username == null || password == null) {
                showError(getString(R.string.login_error_empty))
                return@setOnClickListener
            }
            viewModel.login(username, password)
        }

        binding.buttonVisitante.gone()

        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressLogin.visible()
                    binding.buttonLogin.isEnabled = false
                    binding.textError.gone()
                }
                is UiState.Success -> {
                    binding.progressLogin.gone()
                    binding.buttonLogin.isEnabled = true
                    toast(getString(R.string.action_login) + ": " + state.data.name)
                }
                is UiState.Error -> {
                    binding.progressLogin.gone()
                    binding.buttonLogin.isEnabled = true
                    showError(state.message)
                }
                null -> Unit
            }
        }
    }

    private fun showError(message: String) {
        binding.textError.text = message
        binding.textError.setVisible(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
