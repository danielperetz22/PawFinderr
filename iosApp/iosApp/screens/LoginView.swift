import SwiftUI
import FirebaseAuth
import FirebaseFirestore


struct LoginView: View {
    @State private var email    = ""
    @State private var password = ""

    var body: some View {
        NavigationStack {
            ZStack {
                Color("BackgroundGray")
                    .ignoresSafeArea()

                VStack {
                    VStack(spacing: 128) {
                        Text("welcome back!")
                            .font(.custom("BalooBhaijaan2-ExtraBold", size: 32))
                            .foregroundColor(Color.darkGreen)

                        VStack(spacing: 16) {
                            FloatingLabelTextField(
                                text: $email,
                                label: "email",
                                placeholder: "enter email"
                            )
                            .keyboardType(.emailAddress)
                            .autocapitalization(.none)

                            FloatingLabelSecureField(
                                text: $password,
                                label: "password",
                                placeholder: "enter password"
                            )
                        }
                    }
                    .padding(.top, 80)

                    Spacer()

                    VStack(spacing: 8) {
                        Button {
                            Task {
                                do {
                                    // 1. מחוברים עם מייל וסיסמה
                                    let result = try await Auth.auth()
                                        .signIn(withEmail: email, password: password)
                                    let user = result.user
                                    print("logged in:", user.uid)
                                    // כאן אפשר להתריע ל‑ViewModel או לשמור ב‑@AppStorage
                                } catch {
                                    print("login error:", error.localizedDescription)
                                }
                            }
                        } label: {
                            Text("log in")
                                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                                .frame(maxWidth: .infinity, minHeight: 44)
                                .background(Color.primaryPink)
                                .foregroundColor(.white)
                                .cornerRadius(8)
                        }

                        HStack {
                            Text("Don't have an account?")
                                .font(.custom("BalooBhaijaan2-Regular", size: 16))
                            NavigationLink("Sign up", destination: RegisterView())
                                .font(.custom("BalooBhaijaan2-ExtraBold", size: 16))
                                .foregroundColor(Color.black)
                        }
                    }
                    .padding(.bottom, 40)
                }
                .padding(.horizontal, 24)
                .frame(maxHeight: .infinity)
            }
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}
