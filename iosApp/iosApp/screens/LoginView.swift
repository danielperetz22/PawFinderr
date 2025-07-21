import SwiftUI

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
                            // login action
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
