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
        _nameText        = State(initialValue: report.name)
        _phoneText       = State(initialValue: report.phone)
        _isLostValue     = State(initialValue: report.isLost)

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
                    if !report.imageUrl.isEmpty, let url = URL(string: report.imageUrl) {
                        AsyncImage(url: url) { img in img.resizable().scaledToFill() }
                        placeholder: { Color.gray.opacity(0.2) }
                        .frame(maxWidth: .infinity)
                        .frame(height: 220)
                        .clipped()
                        .cornerRadius(8)
                    }

                    Text(isLostValue ? "lost!" : "found!")
                        .font(.title2.weight(.bold))
                        .foregroundColor(isLostValue ? .red : Color("PrimaryPink"))

                    HStack(spacing: 10) {
                        Button {
                            isLostValue = true
                        } label: {
                            Text("Lost")
                                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity, minHeight: 44)
                                .background(isLostValue ? Color.red : Color("PrimaryPink").opacity(0.5))
                                .cornerRadius(12)
                        }
                        Button {
                            isLostValue = false
                        } label: {
                            Text("Found")
                                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity, minHeight: 44)
                                .background(!isLostValue ? Color("PrimaryPink") : Color("PrimaryPink").opacity(0.5))
                                .cornerRadius(8)
                        }
                    }

                    LabeledEditor(title: "description :", text: $descriptionText, multiline: true)
                    LabeledEditor(title: "contact me :", text: $phoneText)
                    LabeledEditor(title: "name :",        text: $nameText)

                    HStack(alignment: .firstTextBaseline, spacing: 10) {
                        Text("📍")
                        Group {
                            if isGeocoding {
                                Text("Resolving address…").foregroundColor(.secondary)
                            } else if let c = coords, !addressText.isEmpty {
                                Text(addressText).font(.body)
                            } else if let c = coords {
                                Text(String(format: "Lat %.5f, Lng %.5f", c.latitude, c.longitude))
                                    .foregroundColor(.secondary)
                            } else {
                                Text("No location set yet").foregroundColor(.secondary)
                            }
                        }
                        Spacer(minLength: 8)
                        Button {
                            showLocationPicker = true
                        } label: {
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

                    if let c = coords {
                        Map(initialPosition: .region(MKCoordinateRegion(center: c, span: MKCoordinateSpan(latitudeDelta: 0.01, longitudeDelta: 0.01)))) {
                            Annotation("", coordinate: c) {
                                Image(systemName: "mappin.circle.fill").font(.title2).foregroundColor(.red)
                            }
                        }
                        .frame(height: 180)
                        .cornerRadius(20)
                        .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color(.systemGray4), lineWidth: 1))
                    } else {
                        RoundedRectangle(cornerRadius: 20)
                            .fill(Color.white)
                            .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color(.systemGray4), lineWidth: 1))
                            .frame(height: 180)
                            .overlay(Text("map").foregroundColor(.secondary))
                    }

                    Spacer().frame(height: 108)
                }
                .padding(.horizontal, 16)
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

// MARK: - Location Picker (sheet dialog)
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

// --- existing helper view stays the same ---
private struct LabeledEditor: View {
    let title: String
    @Binding var text: String
    var multiline: Bool = false

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title).font(.caption).foregroundColor(.secondary)
            if multiline {
                TextEditor(text: $text)
                    .font(.custom("BalooBhaijaan2-Bold", size: 16))
                    .frame(minHeight: 100)
                    .padding(10)
                    .background(Color.white)
                    .cornerRadius(12)
                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color(.systemGray4), lineWidth: 1))
            } else {
                TextField("", text: $text)
                    .font(.custom("BalooBhaijaan2-Bold", size: 16))
                    .padding(12)
                    .background(Color.white)
                    .cornerRadius(12)
                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color(.systemGray4), lineWidth: 1))
            }
        }
    }
}
