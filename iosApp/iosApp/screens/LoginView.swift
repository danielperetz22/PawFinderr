import SwiftUI

struct LoginView: View {
    @State private var email = ""
    @State private var password = ""

    var body: some View {
        ZStack {
            Color.white
                .ignoresSafeArea()

            VStack(spacing: 24) {
                // כותרת בכחול
                Text("welcome back!")
                    .font(.system(size: 28, weight: .bold))
                    .foregroundColor(Color.blue)

                // שדות עם מסגרת כחולה
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
                }

                // כפתור ורוד
                Button("log in") {
                    // פעולה
                }
                .frame(maxWidth: .infinity, minHeight: 48)
                .background(Color.pink)
                .foregroundColor(.white)
                .cornerRadius(24)

                // כאן, אם תרצי, תוסיפי טקסט קישור
                HStack {
                    Text("Don't have an account?")
                        .foregroundColor(.gray)
                    NavigationLink("Sign up", destination: RegisterView())
                        .foregroundColor(Color.pink)
                        .bold()
                }
            }
            .padding(24)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            LoginView()
        }
    }
}
