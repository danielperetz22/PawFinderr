package org.example.project.user

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.firebase.FirebaseRepository

class UserViewModel(
    private val repo: FirebaseRepository,
    // אפשר להחליף ל־Dispatchers.Default או לבנות scope חיצוני עם expect/actual
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {

    private val _currentUid = MutableStateFlow<String?>(repo.currentUserUid())
    val currentUid: StateFlow<String?> = _currentUid.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _error.asStateFlow()

    private val _currentEmail = MutableStateFlow<String?>(repo.currentUserEmail())
    val currentEmail: StateFlow<String?> = _currentEmail.asStateFlow()

    fun signUp(email: String, password: String) {
        scope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repo.signUp(email, password)
                val uid = repo.currentUserUid()!!
                repo.saveUserProfile(uid, email)
                _currentUid.value = uid
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signIn(email: String, password: String) {
        scope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repo.signIn(email, password)
                _currentUid.value = repo.currentUserUid()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        scope.launch {
            _isLoading.value = true
            _error.value     = null
            delay(2000)
            try {
                repo.signOut()
                _currentUid.value   = null
                _currentEmail.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changePassword(newPassword: String) {
        scope.launch {
            _isLoading.value = true
            _error.value     = null
            try {
                repo.updatePassword(newPassword)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
