import SwiftUI
import Shared

struct NewReportView: View {
    @Environment(\.dismiss) private var dismiss

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

    // NEW: hold the chosen coordinates
    @State private var pickedLocation: (lat: Double, lng: Double)? = nil
    @State private var locationError: String? = nil

    // MARK: – Callbacks
    var onAddPhoto: () -> Void = {}
    var onAddLocation: () -> Void = {}

    var onPublish: (
        _ description: String,
        _ name: String,
        _ phone: String,
        _ isLost: Bool,
        _ imageUrl: String,
        _ lat: Double,
        _ lng: Double
    ) -> Void

    var body: some View {
        ZStack {
            Color("BackgroundGray").ignoresSafeArea()

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

                Button {
                    showPhotoOptions = true
                } label: {
                    ZStack(alignment: .bottomTrailing) {
                        if let uiImage = selectedImage {
                            Image(uiImage: uiImage)
                                .resizable()
                                .scaledToFill()
                                .frame(maxWidth: .infinity, maxHeight: 180)
                                .clipped()
                                .cornerRadius(8)
                        } else {
                            RoundedRectangle(cornerRadius: 8)
                                .fill(Color("BackgroundGray"))
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.gray, lineWidth: 1)
                                .frame(maxWidth: .infinity, maxHeight: 180)
                        }
                        
                        // Only this is tappable
                        Button {
                            showPhotoOptions = true
                        } label: {
                            Image(systemName: "plus")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(.white)
                                .frame(width: 44, height: 44)
                                .background(Color.darkGreen)
                                .cornerRadius(8)
                                .shadow(color: Color.black.opacity(0.2), radius: 4, x: 0, y: 2)
                        }
                        .padding(8)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 180)
                    .confirmationDialog("Select image source",
                                        isPresented: $showPhotoOptions,
                                        titleVisibility: .visible) {
                        Button("Photos") {
                            imagePickerSource = .photoLibrary
                            showImagePicker = true
                        }
                        if UIImagePickerController.isSourceTypeAvailable(.camera) {
                            Button("Camera") {
                                imagePickerSource = .camera
                                showImagePicker = true
                            }
                        }
                        Button("Cancel", role: .cancel) { }
                    }
                                        .sheet(isPresented: $showImagePicker) {
                                            ImagePicker(sourceType: imagePickerSource, selectedImage: $selectedImage)
                                        }
                }


                ZStack(alignment: .topLeading) {
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
                            .padding(.top, 12)
                            .padding(.leading, 16)
                            .allowsHitTesting(false)
                    }
                }

                TextField("Add your name here", text: $name)
                    .padding(12)
                    .font(.custom("BalooBhaijaan2-Bold", size: 16))
                    .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color(.gray), lineWidth: 1))

                TextField("+972.....", text: $phone)
                    .keyboardType(.phonePad)
                    .padding(12)
                    .font(.custom("BalooBhaijaan2-Bold", size: 16))
                    .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color(.gray), lineWidth: 1))

                Button {
                    // If you have a map picker, open it here:
                    onAddLocation()

                    // Or, if you want to grab the current GPS via your KMP `getLocation()`:
                    // (Uncomment if your Kotlin suspend is bridged to Swift concurrency)
                    /*
                    Task {
                        do {
                            let loc = try await LocationKt.getLocation()
                            pickedLocation = (lat: loc.latitude, lng: loc.longitude)
                            locationError = nil
                        } catch {
                            locationError = error.localizedDescription
                        }
                    }
                    */
                } label: {
                    Text("📍  Add location")
                        .foregroundColor(Color("PrimaryPink"))
                        .frame(maxWidth: .infinity)
                        .padding()
                        .font(.custom("BalooBhaijaan2-Bold", size: 16))
                        .background(Color.white.opacity(0.8))
                        .cornerRadius(8)
                }

                // Show what we currently have
                if let coords = pickedLocation {
                    Text(String(format: "📍 Location set (%.5f, %.5f)", coords.lat, coords.lng))
                        .font(.custom("BalooBhaijaan2-Medium", size: 16))
                        .foregroundColor(.secondary)
                }
                if let locationError {
                    Text(locationError)
                        .foregroundColor(.red)
                        .font(.footnote)
                }

                Button {
                    guard let uiImage = selectedImage,
                          let jpegData = uiImage.jpegData(compressionQuality: 0.8),
                          let coords = pickedLocation else {
                        // no image/location yet
                        return
                    }

                    isUploading = true
                    CloudinaryUploader.upload(jpegData) { url in
                        DispatchQueue.main.async {
                            isUploading = false
                            if let imageUrl = url {
                                onPublish(description, name, phone, isLost, imageUrl, coords.lat, coords.lng)
                                dismiss()
                            }
                        }
                    }
                } label: {
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
                // Disable until we have both image and location
                .disabled(selectedImage == nil || pickedLocation == nil || isUploading)

                Spacer()
            }
            .padding(.horizontal, 24)
        }
    }
}

private struct ToggleButton: View {
    let title: String
    let isSelected: Bool
    let selectedColor: Color    // SecondaryPink
    let unselectedColor: Color  // PrimaryPink
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .fontWeight(.semibold)
                .foregroundColor(.white)
                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                .frame(maxWidth: .infinity)
                .padding()
                .background(isSelected ? selectedColor : unselectedColor)
                .cornerRadius(8)
        }
    }
}

struct NewReportView_Previews: PreviewProvider {
    static var previews: some View {
        NewReportView(
            onAddPhoto: { },
            onAddLocation: { },
            onPublish: { description, name, phone, isLost, imageUrl, lat, lng in
                print("Preview publish:", description, name, phone, isLost, imageUrl, lat, lng)
            }
        )
    }
}
