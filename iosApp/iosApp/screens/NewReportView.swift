import SwiftUI

struct NewReportView: View {
    // MARK: – State
    @State private var isLost: Bool = true
    @State private var description: String = ""
    @State private var name: String = ""
    @State private var phone: String = ""
    
    // MARK: – Callbacks
    var onAddPhoto: () -> Void = {}
    var onAddLocation: () -> Void = {}
    var onPublish: () -> Void = {}
    
    var body: some View {
        ZStack {
            // full-screen background
            Color("BackgroundGray")
                .ignoresSafeArea()
            
            VStack(spacing: 16) {
                Spacer()
                
                // MARK: Lost / Found toggle
                HStack(spacing: 8) {
                    ToggleButton(
                        title: "Lost",
                        isSelected: isLost,
                        selectedColor: Color("SecondaryPink"),
                        unselectedColor: Color("PrimaryPink")
                    ) { isLost = true }
                    
                    ToggleButton(
                        title: "Found",
                        isSelected: !isLost,
                        selectedColor: Color("SecondaryPink"),
                        unselectedColor: Color("PrimaryPink")
                    ) { isLost = false }
                }


                
                // MARK: Photo placeholder
                Button(action: onAddPhoto) {
                    ZStack(alignment: .bottomTrailing) {
                        // fill + stroke will both size to this ZStack’s frame
                        RoundedRectangle(cornerRadius: 8)
                            .fill(Color("BackgroundGray"))

                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(.gray), lineWidth: 1)

                        Text("+")
                            .font(.custom("BalooBhaijaan2-Bold", size: 16))
                            .foregroundColor(.white)
                            .padding(8)
                            .background(Color("SecondaryPink").opacity(0.5))
                            .clipShape(Circle())
                            .padding(6)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 120)
                }

                
                TextField("Description",text: $description)
                    .frame(height: 80)
                    .padding(4)
                    .font(.custom("BalooBhaijaan2-Bold", size: 16))
                    .background(Color("BackgroundGray"))
                    .cornerRadius(8)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(.gray), lineWidth: 1)
                    )

                // MARK: Name
                TextField("Add your name here", text: $name)
                    .padding(12)
                    .font(.custom("BalooBhaijaan2-Bold", size: 16))
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(.gray), lineWidth: 1)
                    )
                
                // MARK: Phone
                TextField("+972.....", text: $phone)
                    .keyboardType(.phonePad)
                    .padding(12)
                    .font(.custom("BalooBhaijaan2-Bold", size: 16))
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(.gray), lineWidth: 1)
                    )
                
                // MARK: Add Location
                Button(action: onAddLocation) {
                    Text("📍  Add location")
                        .foregroundColor(Color("PrimaryPink"))
                        .frame(maxWidth: .infinity)
                        .padding()
                        .font(.custom("BalooBhaijaan2-Bold", size: 16))
                        .background(Color.white.opacity(0.8))
                        .cornerRadius(8)
                }
                
                // MARK: Publish Report
                Button(action: onPublish) {
                    Text("Publish Report")
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                        .font(.custom("BalooBhaijaan2-Bold", size: 16))
                        .frame(maxWidth: .infinity, minHeight: 48)
                        .background(Color("PrimaryPink"))
                        .cornerRadius(8)
                }
                
                Spacer()
            }
            .padding(.horizontal, 24)
        }
    }
}

// MARK: – Reusable Toggle Button with custom colors
private struct ToggleButton: View {
    let title: String
    let isSelected: Bool
    let selectedColor: Color    // → SecondaryPink
    let unselectedColor: Color  // → PrimaryPink
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .fontWeight(.semibold)
                .foregroundColor(.white)
                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                .frame(maxWidth: .infinity)
                .padding()
                .background(isSelected
                                ? selectedColor
                                : unselectedColor)
                .cornerRadius(8)
        }
    }
}


struct NewReportView_Previews: PreviewProvider {
    static var previews: some View {
        NewReportView()
    }
}
