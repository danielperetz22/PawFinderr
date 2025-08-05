import SwiftUI
import Shared

struct NewReportView: View {
    // MARK: – State
    @State private var isLost: Bool = true
    @State private var description: String = ""
    @State private var name: String = ""
    @State private var phone: String = ""
    @State private var selectedImage: UIImage? = nil
    @State private var showPhotoOptions: Bool = false
    @State private var showImagePicker: Bool = false
    @State private var isUploading = false
    @State private var uploadedUrl: String? = nil
    @State private var imagePickerSource: UIImagePickerController.SourceType = .photoLibrary

    // MARK: – Callbacks
    var onAddPhoto: () -> Void = {}
    var onAddLocation: () -> Void = {}
    var onPublish: (
          _ description: String,
          _ name: String,
          _ phone: String,
          _ isLost: Bool,
          _ imageUrl: String
        ) -> Void = { _,_,_,_,_ in }


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
                                .frame(maxWidth: .infinity, maxHeight: 180)
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
                    .frame(height: 180)
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


                // MARK: Description
                TextEditor(text: $description)
                  .padding(12)
                  .font(.custom("BalooBhaijaan2-Bold", size: 16))
                  .scrollContentBackground(.hidden)
                  .background(Color("BackgroundGray"))
                  .cornerRadius(8)
                  .overlay(
                    RoundedRectangle(cornerRadius: 8)
                      .stroke(Color.gray, lineWidth: 1)
                  )
                  .frame(height: 100)
                if description.isEmpty {
                  Text("Description")
                    .foregroundColor(.gray)
                    .padding(.top, 12)     // match the editor’s padding
                    .padding(.leading, 16) // match the editor’s padding + border
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
                Button {
                    guard let uiImage = selectedImage,
                                let jpegData = uiImage.jpegData(compressionQuality: 0.8)
                          else { return }

                          isUploading = true
                          CloudinaryUploader.upload(jpegData) { url in
                            DispatchQueue.main.async {
                              isUploading = false
                              if let imageUrl = url {
                                // ① call your callback with *all* the fields:
                                onPublish(
                                  description,
                                  name,
                                  phone,
                                  isLost,
                                  imageUrl
                                )
                                 
                              }
                            }
                          }
                }label: {
                    // 2️⃣ Label
                    if isUploading {
                      ProgressView()
                        .progressViewStyle(.circular)
                        .frame(width: 24, height: 24)
                    } else {
                      Text("Publish Report")
                        .font(.custom("BalooBhaijaan2-Bold", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity, minHeight: 48)
                        .background(Color("PrimaryPink"))
                        .cornerRadius(8)
                    }
                }
                // 3️⃣ Modifiers go here, no trailing closure:
                .disabled(selectedImage == nil || isUploading)
                
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
    NewReportView(
      onAddPhoto:    { /* no-op */ },
      onAddLocation: { /* no-op */ },
      onPublish:     { description, name, phone, isLost, imageUrl in
        // You can even preview values here:
        print("Preview publish:", description, name, phone, isLost, imageUrl)
      }
    )
  }
}
