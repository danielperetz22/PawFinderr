import SwiftUI

struct ToggleButton: View {
    let title: String
    let isSelected: Bool
    let selectedColor: Color
    let unselectedColor: Color
    var cornerRadius: CGFloat = 8
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                .foregroundColor(.white)
                .frame(maxWidth: .infinity, minHeight: 44)
                .background(isSelected ? selectedColor : unselectedColor)
                .cornerRadius(cornerRadius)
        }
    }
}
