import SwiftUI

struct BottomBar: View {
    @EnvironmentObject private var session: SessionStore

    init() {
        let appearance = UITabBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = UIColor.white.withAlphaComponent(0.3)

        
        appearance.stackedLayoutAppearance.normal.iconColor = UIColor.lightGray
        appearance.stackedLayoutAppearance.normal.titleTextAttributes = [
            .foregroundColor: UIColor.lightGray
        ]
        
        appearance.stackedLayoutAppearance.selected.iconColor = UIColor.darkGray
        appearance.stackedLayoutAppearance.selected.titleTextAttributes = [
            .foregroundColor: UIColor.darkGray
        ]
        
        appearance.shadowColor = UIColor(
                    red:   144.0/255.0,
                    green: 209.0/255.0,
                    blue:  216.0/255.0,
                    alpha: 1.0)
        appearance.shadowImage = nil

        
        UITabBar.appearance().standardAppearance = appearance
        if #available(iOS 15.0, *) {
            UITabBar.appearance().scrollEdgeAppearance = appearance
        }
    }

    var body: some View {
        TabView {
            FeedView()
                .tabItem {
                    Label("Feed",   systemImage: "pin")
                }

            ProfileView()
                .tabItem {
                    Label("Profile", systemImage: "person.circle")
                }

            MyReportsView()
                .tabItem {
                    Label("Reports", systemImage: "list.bullet")
                }
        }
    }
}
//#Preview {
//    BottomBar()
//}
