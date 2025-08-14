import SwiftUI
import FirebaseAuth
import FirebaseFirestore


struct RegisterView: View {
    @State private var email           = ""
    @State private var password        = ""
    @State private var confirmPassword = ""
    
    @State private var errorMessage = ""
    @State private var isLoading = false
    
    @State private var didRegister = false

    var body: some View {
        NavigationStack {
            NavigationLink(
                destination: FeedView() ,
                isActive: $didRegister
            ) {EmptyView()}
            ZStack {
                Color("BackgroundGray")
                    .ignoresSafeArea()

                VStack(spacing: 128) {
                    Text("join us!")
                        .font(.custom("BalooBhaijaan2-ExtraBold", size: 32))
                        .foregroundColor(Color.darkGreen)
                        .padding(.top, 80)
                    
                    
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
                        
                        FloatingLabelSecureField(
                            text: $confirmPassword,
                            label: "confirm password",
                            placeholder: "confirm password"
                        )
                    }
                    VStack(spacing: 8) {
                        
                        Button {
                            guard password == confirmPassword else {
                                errorMessage = "Passwords must match"
                            return
                            }
                            Task { @MainActor in
                                isLoading = true
                                errorMessage = ""
                                do {
                                    // 1. יוצרים משתמש ב‑FirebaseAuth
                                    isLoading = true
                                    let result = try await Auth.auth()
                                        .createUser(withEmail: email, password: password)
                                    let user = result.user
                                    // 2. (אופציונלי) שומרים פרופיל בפיירסטור
                                    let db = Firestore.firestore()
                                    try await db.collection("users")
                                        .document(user.uid)
                                        .setData([
                                            "uid": user.uid,
                                            "email": user.email ?? email
                                        ])
                                    // 3. יוזמים ניווט ל‑Home (למשל באמצעות @EnvironmentObject או State)
                                    print("registered:", user.uid)
                                    isLoading = false
                                    didRegister = true
                                } catch {
                                    print("registration error:", error.localizedDescription)
                                    isLoading = false
                                    errorMessage = error.localizedDescription
                                }
                            }
                        } label: {
                            Text("Register")
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
                            Text("Already have account?")
                                .font(.custom("BalooBhaijaan2-Regular", size: 16))
                            NavigationLink("Sign in", destination: LoginView())
                                .font(.custom("BalooBhaijaan2-ExtraBold", size: 16))
                                .foregroundColor(Color.black)
                        }
                    }
                }
                    .padding(24)
                    .frame(maxHeight: .infinity)
                
                if isLoading {
                    Color.black.opacity(0.3).ignoresSafeArea()
                    DogLoaderView()
                }
                
            }
        }
    }
}
