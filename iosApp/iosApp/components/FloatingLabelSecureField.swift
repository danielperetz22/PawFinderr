
import SwiftUI

struct FloatingLabelSecureField: View {
    @Binding var text: String
    let label: String
    let placeholder: String

    var body: some View {
        ZStack(alignment: .leading) {
            RoundedRectangle(cornerRadius: 8)
                .stroke(Color.gray, lineWidth: 1)

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
