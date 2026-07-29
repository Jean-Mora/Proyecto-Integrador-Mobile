package com.puce.sigpel.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puce.sigpel.data.auth.Role
import com.puce.sigpel.data.repository.AuthRepository
import com.puce.sigpel.util.UiState
import com.puce.sigpel.util.toUserMessage
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState = MutableLiveData<UiState<Role>>()
    val loginState: LiveData<UiState<Role>> = _loginState

    fun login(username: String, password: String) {
        _loginState.value = UiState.Loading
        viewModelScope.launch {
            authRepository.login(username, password)
                .onSuccess { role -> _loginState.value = UiState.Success(role) }
                .onFailure { error -> _loginState.value = UiState.Error(error.toUserMessage()) }
        }
    }
}
