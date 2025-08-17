import SwiftUI
import MapKit
import CoreLocation
import Shared

struct ReportDetailsView: View {
    let report: ReportModel
    var onEdit: () -> Void = {}
    var onDelete: () -> Void = {}

    @State private var current: ReportModel
    @State private var showEdit = false
    @State private var showDeleteConfirm = false

    // Address state
    @State private var addressText: String = ""
    @State private var isGeocoding = false
    @State private var savingError: String?

    init(report: ReportModel, onEdit: @escaping () -> Void = {}, onDelete: @escaping () -> Void = {}) {
        self.report = report
        self.onEdit  = onEdit
        self.onDelete = onDelete
        _current = State(initialValue: report)
    }

    var body: some View {
        ZStack {
            Color("BackgroundGray").ignoresSafeArea()

            ScrollView {
                VStack(alignment: .leading, spacing: 8) {

                    // Image
                    if !current.imageUrl.isEmpty, let url = URL(string: current.imageUrl) {
                        AsyncImage(url: url) { img in
                            img.resizable().scaledToFill()
                        } placeholder: {
                            Color.gray.opacity(0.2)
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 200)
                        .clipped()
                        .cornerRadius(8)
                    }

                    // Lost / Found — always dark green, custom font
                    Text(current.isLost ? "lost!" : "found!")
                        .font(.custom("BalooBhaijaan2-Bold", size: 28))
                        .foregroundColor(Color.darkGreen)

                    // Inline fields (title bold, value not)
                    LabeledInline(title: "description :", value: current.description_)
                    LabeledInline(title: "contact me :", value: current.phone.isEmpty ? "—" : current.phone)
                    if !current.name.isEmpty {
                        LabeledInline(title: "", value: current.name)
                    }

                    // Location map + address line
                    if let lat = safeLat, let lng = safeLng {
                        HStack(alignment: .firstTextBaseline, spacing: 8) {
                            Image(systemName: "mappin.and.ellipse")
                                .font(.system(size: 16, weight: .semibold))
                                .foregroundColor(Color("PrimaryPink"))
                            Text(addressText.isEmpty
                                 ? String(format: "Lat %.5f, Lng %.5f", lat, lng)
                                 : addressText)
                            .font(.custom("BalooBhaijaan2-Medium", size: 16))
                            .foregroundColor(addressText.isEmpty ? .secondary : .primary)
                            .lineLimit(nil)
                            .frame(maxWidth: .infinity, alignment: .leading)
                        }
                        Map(initialPosition: .region(region(for: lat, lng))) {
                            Annotation("", coordinate: CLLocationCoordinate2D(latitude: lat, longitude: lng)) {
                                Image(systemName: "mappin.circle.fill")
                                    .font(.title2)
                                    .foregroundColor(.red)
                                    .shadow(radius: 1)
                            }
                        }
                        .frame(height: 200)
                        .cornerRadius(12)

                        
                    } else {
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color.white)
                            .frame(height: 140)
                            .overlay(Text("No location available")
                                .font(.custom("BalooBhaijaan2-Medium", size: 16))
                                .foregroundColor(.secondary))
                    }

                    Spacer().frame(height: 104)
                }
                .padding(.horizontal, 24)
                .padding(.top, 16)
            }

            // Bottom action buttons
            VStack(spacing: 8) {
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
            .padding(.horizontal, 24)
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
        .alert("Save failed", isPresented: .constant(savingError != nil)) {
            Button("OK") { savingError = nil }
        } message: {
            Text(savingError ?? "")
        }
        .navigationDestination(isPresented: $showEdit) {
            EditReportView(report: current) { desc, name, phone, isLost, latOpt, lngOpt, imageUrl in
                let newLat = latOpt ?? current.lat
                let newLng = lngOpt ?? current.lng

                current = ReportModel(
                    id:         current.id,
                    userId:     current.userId,
                    description: desc,                   // correct label
                    name:       name,
                    phone:      phone,
                    imageUrl:   imageUrl ?? current.imageUrl,
                    isLost:     isLost,
                    location:   current.location,        // keep your existing string address if any
                    lat:        newLat,
                    lng:        newLng,
                    createdAt:  current.createdAt
                )

                Task { await reverseGeocode(lat: newLat, lng: newLng) }
                showEdit = false
            }
        }
        .task {
            if let lat = safeLat, let lng = safeLng {
                await reverseGeocode(lat: lat, lng: lng)
            }
        }
    }


    private var safeLat: Double? {
        let lat = current.lat
        return lat.isNaN ? nil : lat
    }
    private var safeLng: Double? {
        let lng = current.lng
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
                let parts = [pm.name, pm.thoroughfare, pm.subThoroughfare, pm.locality, pm.administrativeArea, pm.country]
                self.addressText = parts.compactMap { $0 }
                    .map { $0.trimmingCharacters(in: .whitespaces) }
                    .filter { !$0.isEmpty }
                    .joined(separator: ", ")
            } else {
                self.addressText = ""
            }
        } catch {
            self.addressText = ""
        }
    }
}

private struct LabeledInline: View {
    let title: String
    let value: String

    var body: some View {
        if !value.isEmpty {
            (Text(title + " ")
                .font(.custom("BalooBhaijaan2-Bold", size: 16))
             + Text(value)
                .font(.custom("BalooBhaijaan2-Medium", size: 16)))
            .foregroundColor(.primary)
            .frame(maxWidth: .infinity, alignment: .leading)
            .lineLimit(nil)
        }
    }
}

extension Notification.Name {
    static let reportsDidChange = Notification.Name("reportsDidChange")
}
