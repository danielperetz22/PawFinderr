import SwiftUI
import MapKit
import CoreLocation
import Shared
import FirebaseAuth
import FirebaseFirestore

struct ReportDetailsView: View {
    let report: ReportModel
    var currentUserId: String? = nil
    var onEdit: () -> Void = {}
    var onDelete: () -> Void = {}
    

    @State private var current: ReportModel
    @State private var showEdit = false
    @State private var showDeleteConfirm = false

    @State private var addressText: String = ""
    @State private var isGeocoding = false
    @State private var savingError: String?
    @State private var isMapLoaded = false

    // Fallback for current user id on iOS
    @State private var me: String? = nil

    // delete
    @Environment(\.dismiss) private var dismiss
    @State private var isDeleting = false
    
    
    init(
        report: ReportModel,
        currentUserId: String? = nil,
        onEdit: @escaping () -> Void = {},
        onDelete: @escaping () -> Void = {}
    ) {
        self.report = report
        self.currentUserId = currentUserId
        self.onEdit  = onEdit
        self.onDelete = onDelete
        _current = State(initialValue: report)
    }

    var body: some View {
        ZStack {
            Color("BackgroundGray").ignoresSafeArea()

            ScrollView {
                VStack(alignment: .leading, spacing: 8) {
                    
                    // Image with loader
                    if !current.imageUrl.isEmpty, let url = URL(string: current.imageUrl) {
                        AsyncImage(url: url) { phase in
                            ZStack(alignment: .top) {
                                switch phase {
                                case .empty:
                                    Color.gray.opacity(0.2)
                                        .overlay(
                                            ProgressView()
                                        )
                                case .success(let img):
                                    img.resizable().scaledToFill()
                                case .failure:
                                    Color.gray.opacity(0.25)
                                        .overlay(
                                            Image(systemName: "photo")
                                                .font(.system(size: 18, weight: .semibold))
                                                .foregroundColor(.secondary)
                                        )
                                @unknown default:
                                    Color.gray.opacity(0.2)
                                }
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 200)
                        .clipped()
                        .cornerRadius(8)
                    }
                    
                    
                    Text(current.isLost ? "lost!" : "found!")
                        .font(.custom("BalooBhaijaan2-Bold", size: 28))
                        .foregroundColor(Color.darkGreen)
                    
                    
                    LabeledInline(title: "description :", value: current.description_)
                    LabeledInline(title: "contact me :", value: current.phone.isEmpty ? "—" : current.phone)
                    if !current.name.isEmpty {
                        LabeledInline(title: "", value: current.name)
                    }
                    
                    if let lat = safeLat, let lng = safeLng {
                        // Address line with geocoding loader
                        HStack(alignment: .firstTextBaseline, spacing: 8) {
                            Image(systemName: "mappin.and.ellipse")
                                .font(.system(size: 16, weight: .semibold))
                                .foregroundColor(Color("PrimaryPink"))
                            
                            if isGeocoding {
                                HStack(spacing: 8) {
                                    ProgressView()
                                        .scaleEffect(0.7)
                                    Text("Resolving address...")
                                        .font(.custom("BalooBhaijaan2-Medium", size: 16))
                                        .foregroundColor(.secondary)
                                }
                            } else {
                                Text(addressText.isEmpty
                                     ? String(format: "Lat %.5f, Lng %.5f", lat, lng)
                                     : addressText)
                                .font(.custom("BalooBhaijaan2-Medium", size: 16))
                                .foregroundColor(addressText.isEmpty ? .secondary : .primary)
                                .lineLimit(nil)
                            }
                        }
                        
                        // Map with top linear loader until first render
                        ZStack(alignment: .top) {
                            Map(initialPosition: .region(region(for: lat, lng))) {
                                Annotation("", coordinate: CLLocationCoordinate2D(latitude: lat, longitude: lng)) {
                                    Image(systemName: "mappin.circle.fill")
                                        .font(.title2)
                                        .foregroundColor(.red)
                                        .shadow(radius: 1)
                                }
                            }
                            .onAppear {
                                isMapLoaded = false
                                // Best-effort: mark map as loaded shortly after appear
                                DispatchQueue.main.asyncAfter(deadline: .now() + 0.6) {
                                    isMapLoaded = true
                                }
                            }
                            
                            if !isMapLoaded {
                                ProgressView()
                                
                            }
                        }
                        .frame(height: 200)
                        .cornerRadius(12)
                        
                    } else {
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color.white)
                            .frame(height: 140)
                            .overlay(
                                Text("No location available")
                                    .font(.custom("BalooBhaijaan2-Medium", size: 16))
                                    .foregroundColor(.secondary)
                            )
                    }
                    
                    if let lat = safeLat, let lng = safeLng {
                        Button {
                            openInAppleMaps(
                                lat: lat,
                                lng: lng,
                                name: current.name.isEmpty ? "Location" : current.name
                            )
                        } label: {
                            Text("Open in Maps")
                                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                                .underline(true)
                                .foregroundColor(Color.darkGray)
                                .padding(.vertical, 4)
                                .contentShape(Rectangle())
                        }
                        .buttonStyle(.plain)
                    
                }


                    // ----- Bottom owner-only action icons (NOT sticky) -----
                    if isOwner {
                        HStack {
                            Button {
                                showDeleteConfirm = true
                            } label: {
                                Image(systemName: "trash")
                                    .font(.system(size: 22, weight: .semibold))
                                    .foregroundColor(.red)
                                    .padding(8)
                            }


                            Spacer()

                            Button {
                                showEdit = true
                                onEdit()
                            } label: {
                                Image(systemName: "pencil")
                                    .font(.system(size: 22, weight: .semibold))
                                    .foregroundColor(Color("DarkGray"))
                                    .padding(8)
                            }
                        }
                        .padding(.vertical, 12)
                    }

                    // Room above any tab bar
                    Spacer().frame(height: 32)
                }
                .padding(.horizontal, 24)
                .padding(.top, 16)
            }
        }
        .navigationTitle("Report Details")
        .navigationBarTitleDisplayMode(.inline)
        .alert("Delete report?", isPresented: $showDeleteConfirm) {
            Button("Delete", role: .destructive) {
                guard isOwner else { return }
                guard !current.id.isEmpty else {
                    savingError = "Missing report id."
                    return
                }
                isDeleting = true
                Task {
                    do {
                        try await Firestore.firestore()
                            .collection("reports")
                            .document(current.id)
                            .delete()
                        isDeleting = false
                        onDelete()   // let parent refresh if it wants
                        dismiss()    // pop details screen
                    } catch {
                        isDeleting = false
                        savingError = "Failed to delete: \(error.localizedDescription)"
                    }
                }
            }
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
                    description: desc,
                    name:       name,
                    phone:      phone,
                    imageUrl:   imageUrl ?? current.imageUrl,
                    isLost:     isLost,
                    lat:        newLat,
                    lng:        newLng,
                    createdAt:  current.createdAt
                )

                Task { await reverseGeocode(lat: newLat, lng: newLng) }
                showEdit = false
            }
        }
        .task {
            // populate fallback uid once
            if me == nil {
                me = Auth.auth().currentUser?.uid
                // Debug (optional):
                // print("owner:", current.userId, "me:", me ?? "nil", "passed:", currentUserId ?? "nil")
            }
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

    // Show buttons only if this user owns it
    private var isOwner: Bool {
        let meId   = (currentUserId ?? me ?? "").trimmingCharacters(in: .whitespacesAndNewlines)
        let owner  = current.userId.trimmingCharacters(in: .whitespacesAndNewlines)
        return !meId.isEmpty && meId == owner
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
                self.addressText = parts
                    .compactMap { $0 }
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

private func openInAppleMaps(lat: Double, lng: Double, name: String) {
    let coord = CLLocationCoordinate2D(latitude: lat, longitude: lng)
    let placemark = MKPlacemark(coordinate: coord)
    let item = MKMapItem(placemark: placemark)
    item.name = name
    item.openInMaps(launchOptions: [
        MKLaunchOptionsDirectionsModeKey: MKLaunchOptionsDirectionsModeDriving
    ])
}



