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
                VStack(alignment: .leading, spacing: 16) {

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

                    // Lost / Found
                    Text(current.isLost ? "lost!" : "found!")
                        .font(.title2.weight(.bold))
                        .foregroundColor(current.isLost ? .red : Color("PrimaryPink"))

                    // Description
                    LabeledLine(title: "description :", text: current.description_)

                    // Contact
                    LabeledLine(title: "contact me :", text: current.phone.isEmpty ? "—" : current.phone)

                    // Name
                    if !current.name.isEmpty {
                        Text(current.name)
                            .font(.body)
                    }

                    // Location map + address
                    if let lat = safeLat, let lng = safeLng {
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

                        HStack(alignment: .firstTextBaseline, spacing: 6) {
                            Text("📍")
                            if isGeocoding {
                                Text("Resolving address…").foregroundColor(.secondary)
                            } else if !addressText.isEmpty {
                                Text(addressText).font(.body)
                            } else {
                                Text(String(format: "Lat %.5f, Lng %.5f", lat, lng))
                                    .foregroundColor(.secondary)
                            }
                        }
                    } else {
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color.white)
                            .frame(height: 140)
                            .overlay(Text("No location available").foregroundColor(.secondary))
                    }

                    Spacer().frame(height: 104)
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
        .alert("Save failed", isPresented: .constant(savingError != nil)) {
            Button("OK") { savingError = nil }
        } message: {
            Text(savingError ?? "")
        }
        .navigationDestination(isPresented: $showEdit) {
            EditReportView(report: current) { description, name, phone, isLost, lat, lng in
                let repo = Shared.ReportRepositoryImpl()

                // Kotlin nullable primitive wrappers (adjust to NSNumber? if your headers use that)
                func kBool(_ v: Bool?) -> KotlinBoolean?   { v.map { KotlinBoolean(bool: $0) } }
                func kDouble(_ v: Double?) -> KotlinDouble? { v.map { KotlinDouble(double: $0) } }

                repo.updateReport(
                    reportId: current.id,
                    description: description,
                    name: name,
                    phone: phone,
                    imageUrl: nil,
                    isLost: kBool(isLost),
                    location: nil,
                    lat: kDouble(lat),
                    lng: kDouble(lng),
                    completionHandler: { error in
                        Task { @MainActor in
                            if let error = error {
                                self.savingError = error.localizedDescription
                                return
                            }
                            self.current = ReportModel(
                                id: current.id,
                                userId: current.userId,
                                description: description,
                                name: name,
                                phone: phone,
                                imageUrl: current.imageUrl,
                                isLost: isLost,
                                location: current.location,
                                lat: lat ?? current.lat,
                                lng: lng ?? current.lng,
                                createdAt: current.createdAt               // ← required
                            )
                            self.showEdit = false

                            if let lat = self.safeLat, let lng = self.safeLng {
                                await self.reverseGeocode(lat: lat, lng: lng)
                            } else {
                                self.addressText = ""
                            }

                            NotificationCenter.default.post(name: .reportsDidChange, object: nil)
                        }
                    }
                )
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

extension Notification.Name {
    static let reportsDidChange = Notification.Name("reportsDidChange")
}
