import SwiftUI

struct MyReportsView: View {
    var body: some View {
        VStack {
            Text("My Reports (Placeholder)")
                .font(.title2)
                .foregroundColor(.gray)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(white: 0.95))
    }
}

struct MyReportsView_Previews: PreviewProvider {
    static var previews: some View {
        MyReportsView()
    }
}
