import SwiftUI
import MapKit
import Shared

struct FeedView: View {
    @EnvironmentObject private var session: SessionStore

    // TLV fallback
    private let tlv = CLLocationCoordinate2D(latitude: 32.0853, longitude: 34.7818)

    @State private var cameraPosition: MapCameraPosition = .region(
        MKCoordinateRegion(center: CLLocationCoordinate2D(latitude: 32.0853, longitude: 34.7818),
                           span: MKCoordinateSpan(latitudeDelta: 0.05, longitudeDelta: 0.05))
    )
    @State private var userCoordinate: CLLocationCoordinate2D?
    @State private var isLocating = false
    @State private var locationError: String?

    var body: some View {
        ZStack {
            Color("BackgroundGray").ignoresSafeArea()

            VStack(spacing: 16) {

                // ✅ Blue dot via UserAnnotation()
                Map(position: $cameraPosition) {
                    UserAnnotation() // the native blue dot (shows when permission is granted)

                    // Optional extra pin while you still center manually:
                    if let coord = userCoordinate {
                        Marker("You are here", coordinate: coord)
                    } else {
                        Marker("Tel Aviv", coordinate: tlv)
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(maxHeight: .infinity)
                .padding(.top, 32)
                .padding(.bottom, 80)
                .overlay(alignment: .topTrailing) {
                    // Your locate button (you can swap this for MapUserLocationButton() if you prefer)
                    Button(action: locateMe) {
                        Image(systemName: "location.fill")
                            .foregroundColor(.white)
                            .padding(10)
                            .background(Color.accentColor)
                            .clipShape(Circle())
                            .shadow(radius: 2)
                    }
                    .padding(12)
                }

                if isLocating { ProgressView("Getting your location…") }
                if let err = locationError {
                    Text(err).font(.footnote).foregroundColor(.red).padding(.horizontal)
                }
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 16)
        }
        .onAppear {
            session.currentTitle = "Feed"
            if userCoordinate == nil { locateMe() }
        }
        .navigationBarTitleDisplayMode(.inline)
    }

    private func locateMe() {
        guard !isLocating else { return }
        isLocating = true
        locationError = nil

        // Call the KMM suspend fun through the generated Swift bridge
        Shared.LocationApi().get { location, error in
            DispatchQueue.main.async {
                defer { self.isLocating = false }
                if let error = error {
                    self.locationError = error.localizedDescription
                    return
                }
                guard let loc = location else {
                    self.locationError = "Unknown location error"
                    return
                }
                let coord = CLLocationCoordinate2D(latitude: loc.latitude, longitude: loc.longitude)
                self.userCoordinate = coord
                self.cameraPosition = .region(
                    MKCoordinateRegion(center: coord,
                                       span: MKCoordinateSpan(latitudeDelta: 0.02, longitudeDelta: 0.02))
                )
            }
        }
    }
}
