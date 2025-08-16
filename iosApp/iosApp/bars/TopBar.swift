import SwiftUI

struct TopBar<Content: View>: View {
    let title: String
    let onSignOut: () -> Void
    let content: Content

    @Environment(\.dismiss) private var dismiss

    init(
        title: String,
        onSignOut: @escaping () -> Void,
        @ViewBuilder content: () -> Content
        
        
    ) {
        self.title = title
        self.onSignOut = onSignOut
        self.content = content()
        
        // MARK: – UINavigationBarAppearance configuration
                let navAppearance = UINavigationBarAppearance()
                navAppearance.configureWithOpaqueBackground()
                navAppearance.backgroundColor = UIColor.white.withAlphaComponent(0.3)
                navAppearance.titleTextAttributes = [
                    .foregroundColor: UIColor.darkGray
                ]
                navAppearance.largeTitleTextAttributes = [
                    .foregroundColor: UIColor.darkGray
                ]
                navAppearance.shadowColor = UIColor(
                    red:   144.0/255.0,
                    green: 209.0/255.0,
                    blue:  216.0/255.0,
                    alpha: 1.0
                )
                navAppearance.shadowImage = nil

                // החלפה גלובלית
                UINavigationBar.appearance().standardAppearance = navAppearance
                if #available(iOS 15.0, *) {
                    UINavigationBar.appearance().scrollEdgeAppearance = navAppearance
                }
    }

    var body: some View {
        content
            .navigationTitle(title)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                // Leading “Back” button
                ToolbarItem(placement: .navigationBarLeading) {
                    Button {
                        dismiss()
                    } label: {
                        Image(systemName: "chevron.left")
                    }
                    .tint(.darkGray)
                }
            }
    }
}

extension View {
    func topBar(title: String, onSignOut: @escaping () -> Void) -> some View {
        TopBar(title: title, onSignOut: onSignOut) {
            self
        }
    }
}

#if DEBUG
struct TopBar_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            // Wrap in NavigationStack so "Back" button shows up,
            // and you can simulate pushing/popping in the canvas.
            VStack {
                Text("Hello, world!")
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color(white: 0.95))
            }
        }
    }
}
#endif
