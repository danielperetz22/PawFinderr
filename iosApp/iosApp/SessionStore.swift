import Foundation
import FirebaseAuth

/// מנהל את מצב ההתחברות ב־Firebase ושומר עליו ב־@Published
class SessionStore: ObservableObject {
    @Published var isSignedIn: Bool = false
    @Published var currentTitle: String = ""
    @Published var email: String = ""
    
    @Published var isLoading = false
    @Published var errorMessage = ""

    private var handle: AuthStateDidChangeListenerHandle?

    init() {
        listen()
    }

    /// מאזין לשינויים במצב ה־Auth
    func listen() {
            handle = Auth.auth().addStateDidChangeListener { [weak self] _, user in
                if let user = user {
                    self?.isSignedIn = true
                    self?.email = user.email ?? ""
                } else {
                    self?.isSignedIn = false
                    self?.email = ""
                }
            }
        }

    /// מבצע signOut ב־Firebase
    func signOut() {
        isLoading = true
        errorMessage = ""
        
        Task { @MainActor in
            try? await Task.sleep(for: .seconds(1))
        do{
            try Auth.auth().signOut()

        } catch {
            self.errorMessage = error.localizedDescription
        }
            isLoading = false

        }
    }

    deinit {
        if let h = handle {
            Auth.auth().removeStateDidChangeListener(h)
        }
    }
}
