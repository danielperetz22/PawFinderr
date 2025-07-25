import SwiftUI

struct ProfileView: View {
    @EnvironmentObject private var session: SessionStore
    @State private var isEditing = false

    var body: some View {
        // ── your main content ──
        VStack(spacing: 32) {
            Spacer()

            if session.email.isEmpty {
                Text("No email available")
                    .font(.caption)
                    .foregroundColor(.secondary)
            } else {
                FloatingLabelTextField(
                    text:        .constant(session.email),
                    label:       "Email",
                    placeholder: "Email"
                )
                .disabled(true)
                .opacity(0.6)
                .padding(.horizontal, 24)
            }

            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(white: 0.95))
        .onAppear { session.currentTitle = "Profile" }

        // ── floating buttons, *inside* the safe area ──
        .overlay(
            HStack {
                // logout pill
                Button("logout") {
                    session.signOut()
                }
                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                .frame(maxHeight: 44)
                .foregroundColor(.white)
                .padding(.horizontal, 24)
                .background(Color.primaryPink)
                .cornerRadius(8)

                Spacer()

                // edit FAB
                Button(action: { isEditing.toggle() }) {
                    Image(systemName: "pencil")
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(.white)
                        .frame(width: 44, height: 44)
                        .background(Color.primaryPink)
                        .cornerRadius(8)
                        .shadow(radius: 4, y: 2)
                }
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 16),     // 16pts *above* the bottom safe area
            alignment: .bottom
        )
    }
}


#if DEBUG
struct ProfileView_Previews: PreviewProvider {
    static var previews: some View {
        // Create & configure a dummy session for preview:
        let session = SessionStore()
        session.email = "test@example.com"
        session.currentTitle = "Profile"

        return ProfileView()
            .environmentObject(session)
    }
}
#endif
