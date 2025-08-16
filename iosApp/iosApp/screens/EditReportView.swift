import SwiftUI
import MapKit
import CoreLocation
import Shared


struct EditReportView: View {
    let report: ReportModel
    var onSave: (_ description: String,
                 _ name: String,
                 _ phone: String,
                 _ isLost: Bool,
                 _ lat: Double?,
                 _ lng: Double?) -> Void

    // Local editable copies
    @State private var descriptionText: String
    @State private var nameText: String
    @State private var phoneText: String
    @State private var isLostValue: Bool

    // Location editing
    @State private var coords: CLLocationCoordinate2D?
    @State private var addressText: String = ""
    @State private var isGeocoding = false
    @State private var showLocationPicker = false

    init(
        report: ReportModel,
        onSave: @escaping (_ description: String, _ name: String, _ phone: String, _ isLost: Bool, _ lat: Double?, _ lng: Double?) -> Void
    ) {
        self.report = report
        self.onSave = onSave
        _descriptionText = State(initialValue: report.description_)
        _nameText = State(initialValue: report.name)
        _phoneText = State(initialValue: report.phone)
        _isLostValue = State(initialValue: report.isLost)

        if !report.lat.isNaN, !report.lng.isNaN {
            _coords = State(initialValue: CLLocationCoordinate2D(latitude: report.lat, longitude: report.lng))
        } else {
            _coords = State(initialValue: nil)
        }
    }

    var body: some View {
        ZStack {
            Color("BackgroundGray").ignoresSafeArea()

            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    
                    HStack(spacing: 10) {
                        ToggleButton(
                            title: "Lost",
                            isSelected: isLostValue,
                            selectedColor: .secondaryPink,
                            unselectedColor: .primaryPink,
                            cornerRadius: 12
                        ) { isLostValue = true }

                        ToggleButton(
                            title: "Found",
                            isSelected: !isLostValue,
                            selectedColor: .secondaryPink,
                            unselectedColor: .primaryPink,
                            cornerRadius: 8
                        ) { isLostValue = false }
                    }

                    
                    if !report.imageUrl.isEmpty, let url = URL(string: report.imageUrl) {
                        AsyncImage(url: url) { img in img.resizable().scaledToFill() }
                        placeholder: { Color.gray.opacity(0.2) }
                        .frame(maxWidth: .infinity)
                        .frame(height: 220)
                        .clipped()
                        .cornerRadius(8)
                    }



                    FloatingLabelTextField(text: $descriptionText, label: "description",placeholder: "Edit description" )
                    FloatingLabelTextField(text: $phoneText, label: "phone number", placeholder: "Edit phone number").keyboardType(.phonePad)
                    FloatingLabelTextField(text: $nameText, label: "name", placeholder: "Edit name" )

                    HStack(alignment: .firstTextBaseline, spacing: 10) {
                        Text("📍")
                        Group {
                            if isGeocoding {
                                Text("Resolving address…").foregroundColor(.secondary)
                            } else {
                                Text("No location set yet").foregroundColor(.secondary)
                            }
                        }
                        Spacer(minLength: 8)
                        Button { showLocationPicker = true } label: {
                            HStack(spacing: 6) {
                                Image(systemName: "pencil").font(.system(size: 14, weight: .bold))
                                Text("Change location").font(.system(size: 14, weight: .semibold))
                            }
                            .foregroundColor(.white)
                            .padding(.horizontal, 12)
                            .padding(.vertical, 8)
                            .background(Color("PrimaryPink"))
                            .cornerRadius(8)
                            .shadow(color: Color.black.opacity(0.15), radius: 3, x: 0, y: 2)
                        }
                    }

                    Spacer().frame(height: 108)
                }
                .padding(.horizontal, 24)
                .padding(.top, 16)
            }

            VStack {
                Spacer()
                Button {
                    onSave(descriptionText, nameText, phoneText, isLostValue, coords?.latitude, coords?.longitude)
                } label: {
                    Text("Save changes")
                        .font(.custom("BalooBhaijaan2-Bold", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity, minHeight: 52)
                        .background(Color("PrimaryPink"))
                        .cornerRadius(14)
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 16)
            }
        }
        .navigationTitle("Edit Report")
        .navigationBarTitleDisplayMode(.inline)
        .sheet(isPresented: $showLocationPicker) {
            LocationPickerView(
                initialCenter: coords ?? CLLocationCoordinate2D(latitude: 32.0853, longitude: 34.7818)
            ) { lat, lng in
                coords = CLLocationCoordinate2D(latitude: lat, longitude: lng)
                Task { await reverseGeocodeIfNeeded() }
            }
        }
        .task { await reverseGeocodeIfNeeded() }
    }

    @MainActor
    private func reverseGeocodeIfNeeded() async {
        guard let c = coords else { addressText = ""; return }
        isGeocoding = true
        defer { isGeocoding = false }

        let geocoder = CLGeocoder()
        do {
            let placemarks = try await geocoder.reverseGeocodeLocation(.init(latitude: c.latitude, longitude: c.longitude))
            if let pm = placemarks.first {
                let parts = [pm.name, pm.thoroughfare, pm.subThoroughfare, pm.locality, pm.administrativeArea, pm.country]
                addressText = parts.compactMap { $0?.trimmingCharacters(in: .whitespaces) }
                                   .filter { !$0.isEmpty }
                                   .joined(separator: ", ")
            } else {
                addressText = ""
            }
        } catch { addressText = "" }
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
        let region = MKCoordinateRegion(center: initialCenter,
                                        span: MKCoordinateSpan(latitudeDelta: 0.01, longitudeDelta: 0.01))
        _cameraPosition = State(initialValue: .region(region))
        _currentCenter  = State(initialValue: initialCenter)
    }

    var body: some View {
        ZStack {
            Map(position: $cameraPosition)
                .mapControls { MapUserLocationButton(); MapCompass() }
                .onMapCameraChange { context in currentCenter = context.region.center }
                .ignoresSafeArea(edges: .bottom)

            Image(systemName: "mappin.circle.fill")
                .font(.system(size: 28))
                .foregroundColor(.red)
                .shadow(radius: 2)

            VStack {
                Spacer()
                Text(String(format: "Lat: %.5f   Lng: %.5f", currentCenter.latitude, currentCenter.longitude))
                    .font(.footnote).foregroundColor(.secondary).padding(.bottom, 8)

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

