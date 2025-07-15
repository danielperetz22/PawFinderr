import SwiftUI

struct RegisterView: View {
    @State private var email = ""
    @State private var password = ""
    @State private var confirmPassword = ""

    var body: some View {
        ZStack {
            Color("BackgroundGray")
                .ignoresSafeArea()

            VStack(spacing: 24) {
                // כותרת בכחול
                Text("join us!")
                    .font(.system(size: 28, weight: .bold))
                    .foregroundColor(Color.blue)

                // שלושת שדות הטקסט עם מסגרת כחולה
                VStack(spacing: 16) {
                    TextField("enter email", text: $email)
                        .keyboardType(.emailAddress)
                        .autocapitalization(.none)
                        .padding()
                        .background(Color.white)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Color.blue, lineWidth: 1)
                        )

                    SecureField("enter password", text: $password)
                        .padding()
                        .background(Color.white)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Color.blue, lineWidth: 1)
                        )

                    SecureField("confirm password", text: $confirmPassword)
                        .padding()
                        .background(Color.white)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Color.blue, lineWidth: 1)
                        )
                }

                // כפתור רישום ורוד
                Button("register") {
                    // כאן תשים את הלוגיקה של רישום
                }
                .frame(maxWidth: .infinity, minHeight: 48)
                .background(Color.pink)
                .foregroundColor(.white)
                .cornerRadius(24)

                // קישור חזרה למסך ההתחברות
                HStack {
                    Text("Already have account?")
                        .foregroundColor(.gray)
                    NavigationLink("Sign in", destination: LoginView())
                        .foregroundColor(Color.pink)
                        .bold()
                }
            }
            .padding(24)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}

struct RegisterView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            RegisterView()
        }
    }
}
