import SwiftUI
import MapKit
import CoreLocation
import Shared

struct ReportDetailsView: View {
    let report: ReportModel
    var onEdit: () -> Void = {}
    var onDelete: () -> Void = {}

    @State private var showEdit = false
    @State private var showDeleteConfirm = false

    // Address state
    @State private var isGeocoding = false

    var body: some View {
        ZStack {
            Color("BackgroundGray").ignoresSafeArea()

            ScrollView {
                VStack(alignment: .leading, spacing: 16) {

                    // Image
                    if !report.imageUrl.isEmpty, let url = URL(string: report.imageUrl) {
                        AsyncImage(url: url) { img in
                            img.resizable().scaledToFill()
                        } placeholder: {
                            Color.gray.opacity(0.2)
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 200)
                        .clipped()
                        .cornerRadius(12)
                    }

                    // Lost / Found
                    Text(report.isLost ? "lost!" : "found!")
                        .font(.title2.weight(.bold))
                        .foregroundColor(report.isLost ? .red : Color("PrimaryPink"))

                    // Description
                    LabeledLine(title: "description :", text: report.description_)

                    // Contact
                    LabeledLine(title: "contact me :", text: report.phone.isEmpty ? "—" : report.phone)

                    // Name
                    if !report.name.isEmpty {
                        Text(report.name)
                            .font(.body)
                    }

                    // Location map + address
                    if let lat = safeLat, let lng = safeLng {
                        Map(initialPosition: .region(region(for: lat, lng))) {
                            // Pin
                            Annotation("", coordinate: CLLocationCoordinate2D(latitude: lat, longitude: lng)) {
                                Image(systemName: "mappin.circle.fill")
                                    .font(.title2)
                                    .foregroundColor(.red)
                                    .shadow(radius: 1)
                            }
                        }
                        .frame(height: 200)
                        .cornerRadius(12)

                        // Address line
                        HStack(alignment: .firstTextBaseline, spacing: 6) {
                            Text("📍")
                            if isGeocoding {
                                Text("Resolving address…")
                                    .foregroundColor(.secondary)
                            } else if !addressText.isEmpty {
                                Text(addressText).font(.body)
                            } else {
                                Text(String(format: "Lat %.5f, Lng %.5f", lat, lng))
                                    .foregroundColor(.secondary)
                            }
                        }
                    } else {
                        // No coords available
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color.white)
                            .frame(height: 140)
                            .overlay(
                                Text("No location available")
                                    .foregroundColor(.secondary)
                            )
                    }

                    Spacer().frame(height: 104) // room for buttons
                }
                .padding(.horizontal, 16)
                .padding(.top, 16)
            }

            // Bottom action buttons
            VStack(spacing: 10) {
                Spacer()

                Button {
                    showEdit = true
                    onEdit()
                } label: {
                    Text("Edit")
                        .font(.custom("BalooBhaijaan2-Bold", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity, minHeight: 48)
                        .background(Color("PrimaryPink"))
                        .cornerRadius(8)
                }

                Button(role: .destructive) {
                    showDeleteConfirm = true
                } label: {
                    Text("Delete")
                        .font(.custom("BalooBhaijaan2-Bold", size: 16))
                        .frame(maxWidth: .infinity, minHeight: 48)
                        .foregroundColor(.red)
                        .background(Color.clear)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.red, lineWidth: 1)
                        )
                }
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 16)
        }
        .navigationTitle("Report Details")
        .navigationBarTitleDisplayMode(.inline)
        .alert("Delete report?", isPresented: $showDeleteConfirm) {
            Button("Delete", role: .destructive) { onDelete() }
            Button("Cancel", role: .cancel) { }
        } message: {
            Text("This action cannot be undone.")
        }
        .navigationDestination(isPresented: $showEdit) {
            EditReportView(report: report) { description, name, phone, isLost in
                showEdit = false
            }
        }
        .task {
            // kick off reverse‑geocoding if we have coordinates
            if let lat = safeLat, let lng = safeLng {
                await reverseGeocode(lat: lat, lng: lng)
            }
        }
    }

    // MARK: - Helpers

    private var safeLat: Double? {
        let lat = report.lat
        return lat.isNaN ? nil : lat
    }
    private var safeLng: Double? {
        let lng = report.lng
        return lng.isNaN ? nil : lng
    }

    private func region(for lat: Double, _ lng: Double) -> MKCoordinateRegion {
        MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: lat, longitude: lng),
            span: MKCoordinateSpan(latitudeDelta: 0.01, longitudeDelta: 0.01)
        )
    }

    @MainActor
    private func reverseGeocode(lat: Double, lng: Double) async {
        isGeocoding = true
        defer { isGeocoding = false }

        let geocoder = CLGeocoder()
        let location = CLLocation(latitude: lat, longitude: lng)

        do {
            let placemarks = try await geocoder.reverseGeocodeLocation(location)
            if let pm = placemarks.first {
                // Build a friendly address
                let parts = [
                    pm.name,
                    pm.thoroughfare,
                    pm.subThoroughfare,
                    pm.locality,
                    pm.administrativeArea,
                    pm.country
                ]
                let joined = parts
                    .compactMap { $0 }
                    .filter { !$0.trimmingCharacters(in: .whitespaces).isEmpty }
                    .joined(separator: ", ")
                self.addressText = joined
            } else {
                self.addressText = ""
            }
        } catch {
            // Don’t block UI if geocoder fails
            self.addressText = ""
        }
    }
}

private struct LabeledLine: View {
    let title: String
    let text: String

    var body: some View {
        if !text.isEmpty {
            VStack(alignment: .leading, spacing: 6) {
                Text(title)
                    .font(.caption)
                    .foregroundColor(.secondary)
                Text(text)
                    .font(.body)
            }
        }
    }
}
