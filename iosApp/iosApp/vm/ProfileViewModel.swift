import SwiftUI
import FirebaseAuth

class ProfileViewModel: ObservableObject {
  @Published var isLoading = false
  @Published var errorMessage = ""

  func changePassword(to newPassword: String) {
    guard let user = Auth.auth().currentUser else {
      errorMessage = "User not signed in"
      return
    }
    isLoading = true
    errorMessage = ""
    user.updatePassword(to: newPassword) { [weak self] error in
      DispatchQueue.main.async {
        self?.isLoading = false
        if let error = error {
          self?.errorMessage = error.localizedDescription
        }
      }
    }
  }
}
