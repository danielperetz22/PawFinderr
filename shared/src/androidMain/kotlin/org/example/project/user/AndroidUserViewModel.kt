package org.example.project.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.data.firebase.FirebaseRepository
import org.example.project.data.firebase.RemoteFirebaseRepository

class AndroidUserViewModel(
    repo: FirebaseRepository = RemoteFirebaseRepository()
) : ViewModel() {

    private val sharedVm = UserViewModel(repo, viewModelScope)

    val currentUser: StateFlow<String?> = sharedVm.currentUid
    val isLoading:    StateFlow<Boolean> = sharedVm.isLoading
    val errorMessage: StateFlow<String?> = sharedVm.errorMessage

    fun signUp(email: String, password: String) = sharedVm.signUp(email, password)
    fun signIn(email: String, password: String) = sharedVm.signIn(email, password)
    fun signOut()                                = sharedVm.signOut()
}
