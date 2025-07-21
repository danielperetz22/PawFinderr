package org.example.project.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.data.firebase.FirebaseRepository
import org.example.project.data.firebase.RemoteFirebaseRepository

/**
 * זהו ה‑AndroidX ViewModel שלך שמפנה ל‑common UserViewModel
 */
class AndroidUserViewModel(
    // בברירת־מחדל מזריקים את המימוש של ה‑repo
    repo: FirebaseRepository = RemoteFirebaseRepository()
) : ViewModel() {

    // בונים את ה־shared ViewModel ומשתמשים ב־viewModelScope
    private val sharedVm = UserViewModel(repo, viewModelScope)

    // חושפים ל‑UI את ה‑StateFlow משותף
    val currentUser: StateFlow<String?> = sharedVm.currentUid
    val isLoading:    StateFlow<Boolean> = sharedVm.isLoading
    val errorMessage: StateFlow<String?> = sharedVm.errorMessage

    // רק proxy לפעולות
    fun signUp(email: String, password: String) = sharedVm.signUp(email, password)
    fun signIn(email: String, password: String) = sharedVm.signIn(email, password)
    fun signOut()                                = sharedVm.signOut()
}
