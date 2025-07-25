package org.example.project.ui.home

import androidx.lifecycle.ViewModel
import org.example.project.data.firebase.RemoteFirebaseRepository
import org.example.project.user.UserViewModel as SharedUserViewModel

class AndroidUserViewModel : ViewModel() {
    private val shared = SharedUserViewModel(RemoteFirebaseRepository())

    val currentUid   = shared.currentUid
    val isLoading    = shared.isLoading
    val errorMessage = shared.errorMessage
    val currentEmail = shared.currentEmail


    fun signUp(email: String, pwd: String) = shared.signUp(email, pwd)
    fun signIn(email: String, pwd: String) = shared.signIn(email, pwd)
    fun signOut() = shared.signOut()
    fun changePassword(newPassword: String) = shared.changePassword(newPassword)
}
