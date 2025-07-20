import SwiftUI

struct HomeView: View {
    var body: some View {
        ZStack {
            Color("BackgroundGray")
                .ignoresSafeArea()
            
            VStack(spacing: 24) {
                Image("logo")
                    .renderingMode(.original)
                    .resizable()
                    .scaledToFit()
                    .frame(height: 165)
                    .padding(.top, 100)
                
                VStack(spacing: 2) {
                    Text("welcome to PawFinder")
                        .font(.custom("BalooBhaijaan2-Bold", size: 22))

                    Text("help fins lost dogs quickly!")
                        .font(.custom("BalooBhaijaan2-SemiBold", size: 22))
                }
                
                VStack(alignment: .leading, spacing: 24) {
                    ForEach([
                        ("Quick Reports", "File a report in seconds"),
                        ("Live Map",       "See every pin at a glance"),
                        ("Direct Pickup",  "Call the reporter")
                    ], id: \.0) { title, subtitle in
                        HStack(spacing: 16) {
                            Circle()
                                .fill(Color.lightGreen)
                                .frame(width: 30, height: 30)
                            VStack(alignment: .leading, spacing: 2) {
                                Text(title)
                                    .font(.custom("BalooBhaijaan2-Medium", size: 16))
                                    .foregroundColor(.black)
                                Text(subtitle)
                                    .font(.custom("BalooBhaijaan2-Medium", size: 14))
                                    .foregroundColor(.black)
                            }
                        }
                    }
                }
                
                Spacer()
                
                VStack(spacing: 16) {
                    NavigationLink(destination: RegisterView()) {
                        Text("get started")
                            .font(.custom("BalooBhaijaan2-Bold", size: 16))
                            .frame(maxWidth: .infinity, minHeight: 44)
                            .background(Color.primaryPink)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                    
                    NavigationLink(destination: LoginView()) {
                        Text("log in")
                            .font(.custom("BalooBhaijaan2-Bold", size: 16))
                            .frame(maxWidth: .infinity, minHeight: 44)
                            .background(
                                RoundedRectangle(cornerRadius: 8)
                                .fill(Color.white.opacity(0.3))

                            )
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color.white, lineWidth: 2)
                            )
                            .foregroundColor(.primaryPink)
                    }
                }
                .padding(32)
            }
        }
    }}
