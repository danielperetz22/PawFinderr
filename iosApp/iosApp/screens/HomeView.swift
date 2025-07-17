import SwiftUI

struct HomeView: View {
    var body: some View {
        ZStack {
            Color("BackgroundGray")
                .ignoresSafeArea()

            VStack(spacing: 32) {
                Image("logo")
                    .renderingMode(.original)
                    .resizable()
                    .scaledToFit()
                    .frame(height: 100)

                VStack(spacing: 8) {
                    Text("welcome to PawFinder")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(.black)
                    Text("help fins lost dogs quickly!")
                        .font(.system(size: 16))
                        .foregroundColor(.gray)
                }

                VStack(alignment: .leading, spacing: 24) {
                    ForEach([
                        ("Quick Reports", "File a report in seconds"),
                        ("Live Map",       "See every pin at a glance"),
                        ("Direct Pickup",  "Call the reporter")
                    ], id: \.0) { title, subtitle in
                        HStack(spacing: 16) {
                            Circle()
                                .fill(Color("CircleBackground"))
                                .frame(width: 24, height: 24)
                            VStack(alignment: .leading, spacing: 2) {
                                Text(title)
                                    .font(.system(size: 18, weight: .semibold))
                                    .foregroundColor(.black)
                                Text(subtitle)
                                    .font(.system(size: 14))
                                    .foregroundColor(.gray)
                            }
                        }
                    }
                }

                Spacer()

                               VStack(spacing: 16) {
                                   NavigationLink("get started", destination: RegisterView())
                                       .frame(maxWidth: .infinity, minHeight: 48)
                                       .background(Color.pink)
                                       .foregroundColor(.white)
                                       .cornerRadius(24)

                                   NavigationLink("log in", destination: LoginView())
                                       .frame(maxWidth: .infinity, minHeight: 48)
                                       .overlay(
                                           RoundedRectangle(cornerRadius: 24)
                                            .stroke(Color.pink, lineWidth: 2)
                                       )
                                       .foregroundColor(Color.pink)
                               }
                           }
                           .padding(24)
                           .frame(maxWidth: .infinity, maxHeight: .infinity)
                       }
                   }
               }
