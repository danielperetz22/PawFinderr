import Foundation
import FirebaseAuth

/// מנהל את מצב ההתחברות ב־Firebase ושומר עליו ב־@Published
class SessionStore: ObservableObject {
    @Published var isSignedIn: Bool = false
    @Published var currentTitle: String = ""

    private var handle: AuthStateDidChangeListenerHandle?

    init() {
        listen()
    }

    /// מאזין לשינויים במצב ה־Auth
    func listen() {
        handle = Auth.auth().addStateDidChangeListener { _, user in
            DispatchQueue.main.async {
                self.isSignedIn = (user != nil)
            }
        }
    }

    /// מבצע signOut ב־Firebase
    func signOut() {
        try? Auth.auth().signOut()
    }

    deinit {
        if let h = handle {
            Auth.auth().removeStateDidChangeListener(h)
        }
    }
}
