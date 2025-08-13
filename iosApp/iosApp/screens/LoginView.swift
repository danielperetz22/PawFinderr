import SwiftUI
import FirebaseAuth
import FirebaseFirestore


struct LoginView: View {
    @State private var email    = ""
    @State private var password = ""
    
    @State private var errorMessage = ""
    @State private var isLoading = false
    

    @State private var isLoggedIn = false


    var body: some View {
        NavigationStack {
            NavigationLink(
                destination: FeedView(),
                            isActive: $isLoggedIn
                        ) {
                            EmptyView()
                        }
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
                                    isLoading = true
                                    let result = try await Auth.auth()
                                        .signIn(withEmail: email, password: password)
                                    let user = result.user
                                    print("logged in:", user.uid)
                                    isLoading = false
                                    isLoggedIn = true
                                    // כאן אפשר להתריע ל‑ViewModel או לשמור ב‑@AppStorage
                                } catch {
                                    print("login error:", error.localizedDescription)
                                    isLoading = false
                                    errorMessage = error.localizedDescription
                                }
                            }
                        } label: {
                            Text("log in")
                                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                                .frame(maxWidth: .infinity, minHeight: 44)
                                .background(Color.primaryPink)
                                .foregroundColor(.white)
                                .cornerRadius(8)
                        }.disabled(isLoading || password.isEmpty || email.isEmpty)
                        
                        if !errorMessage.isEmpty {
                            Text(errorMessage)
                                .foregroundColor(.red)
                                .font(.caption)
                                .padding(.top, 4)
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
                
                if isLoading {
                    Color.black.opacity(0.3).ignoresSafeArea()
                    DogLoaderView()
                }
            }
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}
