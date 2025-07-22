import SwiftUI
import FirebaseAuth
import FirebaseFirestore


struct FloatingLabelTextField: View {
    @Binding var text: String
    let label: String
    let placeholder: String

    var body: some View {
        ZStack(alignment: .leading) {
            RoundedRectangle(cornerRadius: 8)
                .stroke(Color.black, lineWidth: 1)

            if text.isEmpty {
                Text(placeholder)
                    .foregroundColor(.gray)
                    .padding(.horizontal, 12)
                    .padding(.top, 16)
            }

            TextField("", text: $text)
                .padding(.horizontal, 12)
                .padding(.top, 12)

            Text(label)
                .font(.caption)
                .foregroundColor(.gray)
                .padding(.horizontal, 4)
                .background(Color("BackgroundGray"))
                .offset(x: 12, y: -10)
        }
        .frame(height: 56)
    }
}

struct FloatingLabelSecureField: View {
    @Binding var text: String
    let label: String
    let placeholder: String

    var body: some View {
        ZStack(alignment: .leading) {
            RoundedRectangle(cornerRadius: 8)
                .stroke(Color.black, lineWidth: 1)

            if text.isEmpty {
                Text(placeholder)
                    .foregroundColor(.gray)
                    .padding(.horizontal, 12)
                    .padding(.top, 16)
            }

            SecureField("", text: $text)
                .padding(.horizontal, 12)
                .padding(.top, 12)

            Text(label)
                .font(.caption)
                .foregroundColor(.gray)
                .padding(.horizontal, 4)
                .background(Color("BackgroundGray"))
                .offset(x: 12, y: -10)
        }
        .frame(height: 56)
    }
}

struct RegisterView: View {
    @State private var email           = ""
    @State private var password        = ""
    @State private var confirmPassword = ""

    var body: some View {
        NavigationStack {
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
                            Task {
                                do {
                                    // 1. יוצרים משתמש ב‑FirebaseAuth
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
                                    print("🙌 registered:", user.uid)
                                } catch {
                                    print("❌ registration error:", error.localizedDescription)
                                }
                            }
                        } label: {
                            Text("Register")
                                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                                .frame(maxWidth: .infinity, minHeight: 44)
                                .background(Color.primaryPink)
                                .foregroundColor(.white)
                                .cornerRadius(8)
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
                
            }
        }
    }
}
