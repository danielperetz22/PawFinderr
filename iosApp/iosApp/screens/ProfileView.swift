import SwiftUI
import FirebaseAuth


struct ProfileView: View {
    @EnvironmentObject private var session: SessionStore
    @StateObject private var vm = ProfileViewModel()


    @State private var isEditing      = false
    @State private var newPassword    = ""
    @State private var confirmPassword = ""

    var body: some View {
        VStack(spacing: 32) {
            Spacer()

            // — Read-only e-mail field —
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

            // — Password fields only in edit mode —
            if isEditing {
                VStack(spacing: 16) {
                    FloatingLabelTextField(
                        text:        $newPassword,
                        label:       "New Password",
                        placeholder: "New Password"
                    )
                    .padding(.horizontal, 24)

                    FloatingLabelTextField(
                        text:        $confirmPassword,
                        label:       "Confirm New Password",
                        placeholder: "Confirm New Password"
                    )
                    .padding(.horizontal, 24)
                    if !vm.errorMessage.isEmpty {
                        Text(vm.errorMessage)
                            .foregroundColor(.red)
                            .font(.caption)
                            .padding(.top, 8)
                    }
                }
            }

            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(white: 0.95))
        .onAppear { session.currentTitle = "Profile" }
        .overlay(bottomButtons, alignment: .bottom)
        .onReceive(vm.$isLoading.combineLatest(vm.$errorMessage)) { isLoading, error in
                    if !isLoading && error.isEmpty && isEditing {
                        withAnimation {
                            isEditing = false
                            newPassword = ""
                            confirmPassword = ""
                        }
                    }
                }
        .overlay {
                    if vm.isLoading || session.isLoading {
                        Color.black.opacity(0.3).ignoresSafeArea()
                        DogLoaderView()
                    }
                }
    }

    @ViewBuilder
    private var bottomButtons: some View {
        HStack {
            if isEditing {
                // — CANCEL —
                Button("Cancel") {
                    withAnimation {
                        isEditing = false
                        newPassword = ""
                        confirmPassword = ""
                    }
                }
                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                .foregroundColor(.white)
                .frame(maxHeight:44)
                .padding(.horizontal, 24)
                .background(Color.primaryPink)
                .cornerRadius(8)

                Spacer()

                Button("Save") {
                    guard !newPassword.isEmpty, newPassword == confirmPassword else {
                                            vm.errorMessage = "Passwords must match"
                                            return
                                        }
                    vm.changePassword(to: newPassword)

//                    withAnimation {
//                        isEditing = false
//                    }
                }
                .disabled(vm.isLoading)
                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                .foregroundColor(.white)
                .frame(maxHeight:44)
                .padding(.horizontal, 24)
                .background(Color.primaryPink)
                .cornerRadius(8)

            } else {
                Button("Logout") {
                    session.signOut()
                }
                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                .foregroundColor(.white)
                .frame(maxHeight:44)
                .padding(.horizontal, 24)
                .background(Color.primaryPink)
                .cornerRadius(8)

                Spacer()

                Button(action: {
                    withAnimation { isEditing = true }
                }) {
                    Image(systemName: "pencil")
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(.white)
                        .frame(width: 44, height: 44)
                        .background(Color.primaryPink)
                        .cornerRadius(8)
                        .shadow(color: Color.black.opacity(0.2),
                                radius: 4, x: 0, y: 2)
                }
            }
        }
        .padding(.horizontal, 24)
        .padding(.bottom, 16)   // sits 16pts above the tab bar
    }
}

#if DEBUG
struct ProfileView_Previews: PreviewProvider {
    static var previews: some View {
        let session = SessionStore()
        session.email = "test@example.com"
        session.currentTitle = "Profile"

        return ProfileView()
            .environmentObject(session)
    }
}
#endif
