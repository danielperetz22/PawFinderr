import SwiftUI
import FirebaseCore


class AppDelegate: NSObject, UIApplicationDelegate {
  func application(_ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
    FirebaseApp.configure()

    return true
  }
}

@main
struct YourApp: App {
    // register app delegate for Firebase setup
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @StateObject private var session = SessionStore()
    
    
    
    var body: some Scene {
        WindowGroup {
            if session.isSignedIn {
                NavigationStack {
                    TopBar(title: session.currentTitle, onSignOut: {
                        session.signOut()
                    }) {
                        BottomBar()
                            .environmentObject(session)
                    }
                    .environmentObject(session)
                }
            } else {
                NavigationStack {
                    HomeView()
                }
                .environmentObject(session)
            }
        }
    }
}
