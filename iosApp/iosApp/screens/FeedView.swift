import SwiftUI
import FirebaseAuth
import FirebaseFirestore

struct FeedView: View {
    // Allows this view to pop itself off the NavigationStack
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var session: SessionStore

    // Callback to perform additional cleanup after sign-out (e.g. clear your session state)
    var onSignOut: () -> Void

    var body: some View {
        ZStack {
            Color("BackgroundGray")
                .ignoresSafeArea()
            
            VStack {
                Spacer()
                
                Text("Feed")
                    .font(.system(size: 32, weight: .bold))
                    .padding(.bottom, 24)
                
                Button(action: signOut) {
                    Text("Sign Out")
                        .frame(maxWidth: .infinity, minHeight: 44)
                        .background(Color.accentColor)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
                .padding(.horizontal)
                
                Spacer()
            }
            .padding(16)
        }
        .onAppear {session.currentTitle = "Feed"}
        .navigationBarTitleDisplayMode(.inline)
    }

    private func signOut() {
        do {
            try Auth.auth().signOut()

            // Verify currentUser is nil
            if Auth.auth().currentUser == nil {
                print("✅ Successfully signed out from Firebase")
                
                // First call your cleanup callback
                onSignOut()
                
                // Then dismiss this view (pop back to Home)
                dismiss()
            } else {
                print("⚠️ Still signed in to Firebase!")
            }
        } catch let error as NSError {
            print("❌ Error signing out: \(error.localizedDescription)")
        }
    }
}

struct FeedView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            FeedView(onSignOut: {
                // no-op
            })
        }
    }
}
