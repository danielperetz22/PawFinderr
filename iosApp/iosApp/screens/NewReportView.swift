import SwiftUI

struct NewReportView: View {
    // MARK: – State
    @State private var isLost: Bool = true
    @State private var description: String = ""
    @State private var name: String = ""
    @State private var phone: String = ""
    @State private var selectedImage: UIImage? = nil
    @State private var showPhotoOptions: Bool = false
    @State private var showImagePicker: Bool = false
    @State private var imagePickerSource: UIImagePickerController.SourceType = .photoLibrary

    // MARK: – Callbacks
    var onAddPhoto: () -> Void = {}
    var onAddLocation: () -> Void = {}
    var onPublish: () -> Void = {}

    var body: some View {
        ZStack {
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
                Button {
                    showPhotoOptions = true
                } label: {
                    ZStack(alignment: .bottomTrailing) {
                        // If we have an image, show it:
                        if let uiImage = selectedImage {
                            Image(uiImage: uiImage)
                                .resizable()
                                .scaledToFill()
                                .frame(maxWidth: .infinity, maxHeight: 250)
                                .clipped()
                                .cornerRadius(8)
                        } else {
                            // your empty placeholder
                            RoundedRectangle(cornerRadius: 8)
                                .fill(Color("BackgroundGray"))
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.gray, lineWidth: 1)
                        }

                        // always overlay the “+”:
                        Text("+")
                            .font(.custom("BalooBhaijaan2-Bold", size: 16))
                            .foregroundColor(.white)
                            .padding(8)
                            .background(Color("SecondaryPink").opacity(0.5))
                            .clipShape(Circle())
                            .padding(6)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 250)
                }
                .confirmationDialog("Select image source",
                                    isPresented: $showPhotoOptions,
                                    titleVisibility: .visible) {
                    Button("Photos") {
                        imagePickerSource = .photoLibrary
                        showImagePicker = true
                    }
                    // only offer Camera if available
                    if UIImagePickerController.isSourceTypeAvailable(.camera) {
                        Button("Camera") {
                            imagePickerSource = .camera
                            showImagePicker = true
                        }
                    }
                    Button("Cancel", role: .cancel) { }
                }
                .sheet(isPresented: $showImagePicker) {
                    ImagePicker(sourceType: imagePickerSource,
                                selectedImage: $selectedImage)
                }


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
