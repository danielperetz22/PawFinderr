import SwiftUI
import MapKit
import Shared

struct NewReportView: View {
    @Environment(\.dismiss) private var dismiss

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

    // Location
    @State private var pickedLocation: (lat: Double, lng: Double)? = nil
    @State private var locationError: String? = nil
    @State private var showLocationPicker: Bool = false

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
                            .fill(Color.backgroundGray)
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color.gray, lineWidth: 1)
                            .frame(maxWidth: .infinity, maxHeight: 180)
                    }

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

                // Description
                ZStack(alignment: .topLeading) {
                    TextEditor(text: $description)
                        .padding(12)
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

                FloatingLabelTextField(
                    text: $name,
                    label: "name",
                    placeholder: "Add your name here"
                )

                FloatingLabelTextField(
                    text: $phone,
                    label: "contact",
                    placeholder: "+972..."
                ).keyboardType(.phonePad)

                Button {
                    onAddLocation()
                    showLocationPicker = true
                } label: {
                    Text("📍  Add location")
                        .foregroundColor(Color("PrimaryPink"))
                        .frame(maxWidth: .infinity, maxHeight: 48)
                        .padding()
                        .font(.custom("BalooBhaijaan2-Bold", size: 16))
                        .background(Color.white.opacity(0.7))
                        .cornerRadius(8)
                }
                .sheet(isPresented: $showLocationPicker) {
                    LocationPickerView(
                        initialCenter: pickedLocation.map { CLLocationCoordinate2D(latitude: $0.lat, longitude: $0.lng) }
                            ?? CLLocationCoordinate2D(latitude: 32.0853, longitude: 34.7818) // TLV default
                    ) { lat, lng in
                        pickedLocation = (lat, lng)
                        locationError = nil
                    }
                }

                // Current selection preview / error
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

                // Publish
                Button {
                    guard let uiImage = selectedImage,
                          let jpegData = uiImage.jpegData(compressionQuality: 0.8),
                          let coords = pickedLocation else {
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
                .disabled(selectedImage == nil || pickedLocation == nil || isUploading)

                Spacer()
            }
            .padding(.horizontal, 24)
        }
    }
}

private struct LocationPickerView: View {
    @Environment(\.dismiss) private var dismiss

    let initialCenter: CLLocationCoordinate2D
    let onPick: (_ lat: Double, _ lng: Double) -> Void

    @State private var cameraPosition: MapCameraPosition
    @State private var currentCenter: CLLocationCoordinate2D

    init(initialCenter: CLLocationCoordinate2D,
         onPick: @escaping (_ lat: Double, _ lng: Double) -> Void) {
        self.initialCenter = initialCenter
        self.onPick = onPick
        let region = MKCoordinateRegion(
            center: initialCenter,
            span: MKCoordinateSpan(latitudeDelta: 0.01, longitudeDelta: 0.01)
        )
        _cameraPosition = State(initialValue: .region(region))
        _currentCenter  = State(initialValue: initialCenter)
    }

    var body: some View {
        ZStack {
            Map(position: $cameraPosition)
                .mapControls {
                    MapUserLocationButton()
                    MapCompass()
                }
                .onMapCameraChange { ctx in
                    currentCenter = ctx.region.center
                }

                .ignoresSafeArea(edges: .bottom)

            // Center pin
            Image(systemName: "mappin.circle.fill")
                .font(.system(size: 28))
                .foregroundColor(.red)
                .shadow(radius: 2)

            VStack {
                Spacer()
                // Coordinates readout (optional)
                Text(String(format: "Lat: %.5f   Lng: %.5f", currentCenter.latitude, currentCenter.longitude))
                    .font(.footnote)
                    .foregroundColor(.secondary)
                    .padding(.bottom, 8)

                HStack {
                    Button("Cancel") { dismiss() }
                        .frame(height: 44)
                        .padding(.horizontal, 16)
                        .background(Color.gray.opacity(0.15))
                        .cornerRadius(8)

                    Spacer(minLength: 12)

                    Button {
                        onPick(currentCenter.latitude, currentCenter.longitude)
                        dismiss()
                    } label: {
                        Text("Use this location")
                            .fontWeight(.semibold)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity, minHeight: 44)
                            .background(Color("PrimaryPink"))
                            .cornerRadius(8)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 16)
            }
        }
        .presentationDetents([.medium, .large])
        .presentationDragIndicator(.visible)
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
