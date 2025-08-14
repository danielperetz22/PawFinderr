import SwiftUI
import MapKit
import Shared

struct FeedView: View {
    @EnvironmentObject private var session: SessionStore

    // Map state
    @State private var cameraPosition: MapCameraPosition = .region(
        MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 32.0853, longitude: 34.7818), // TLV fallback
            span: MKCoordinateSpan(latitudeDelta: 0.05, longitudeDelta: 0.05)
        )
    )
    @State private var userCoordinate: CLLocationCoordinate2D?
    @State private var isLocating = false
    @State private var locationError: String?

    // Reports
    @State private var reports: [ReportModel] = []
    @State private var isLoadingReports = false
    @State private var reportsError: String?

    // Pin selection
    @State private var selectedReport: ReportModel? = nil

    var body: some View {
        ZStack {
            Color("BackgroundGray").ignoresSafeArea()

            VStack(spacing: 16) {
                Map(position: $cameraPosition) {
                    // Native blue dot (requires location permission)
                    UserAnnotation()

                    // Red, tappable pins
                    ForEach(reports, id: \.id) { rpt in
                        let lat = rpt.lat
                        let lng = rpt.lng
                        if !lat.isNaN, !lng.isNaN {
                            let coord = CLLocationCoordinate2D(latitude: lat, longitude: lng)
                            Annotation("", coordinate: coord) {
                                VStack(spacing: 2) {
                                    // Make the pin itself navigate
                                    NavigationLink {
                                        ReportDetailsView(report: rpt)
                                            .navigationTitle("Report Details")
                                            .navigationBarTitleDisplayMode(.inline)
                                    } label: {
                                        Image(systemName: "mappin.circle.fill")
                                            .font(.title)
                                            .foregroundColor(.red)
                                            .shadow(radius: 2)
                                    }

                                    Text(rpt.name.isEmpty ? (rpt.isLost ? "Lost" : "Found") : rpt.name)
                                        .font(.caption2)
                                        .lineLimit(1)
                                }
                            }
                        }
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(maxHeight: .infinity)
                .padding(.top, 32)
                .padding(.bottom, 80)
                .overlay(alignment: .topTrailing) {
                    HStack(spacing: 8) {
                        if isLoadingReports { ProgressView().padding(8) }
                        Button(action: reloadReports) {
                            Image(systemName: "arrow.clockwise")
                                .foregroundColor(.white)
                                .padding(10)
                                .background(Color.gray.opacity(0.85))
                                .clipShape(Circle())
                        }
                        Button(action: locateMe) {
                            Image(systemName: "location.fill")
                                .foregroundColor(.white)
                                .padding(10)
                                .background(Color.accentColor)
                                .clipShape(Circle())
                        }
                    }
                    .padding(12)
                }

                if isLocating { ProgressView("Getting your location…") }
                if let err = locationError {
                    Text(err).font(.footnote).foregroundColor(.red).padding(.horizontal)
                }
                if let rerr = reportsError {
                    Text(rerr).font(.footnote).foregroundColor(.red).padding(.horizontal)
                }
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 16)
        }
        .onAppear { session.currentTitle = "Feed" }
        .task {
            if reports.isEmpty { reloadReports() }
            if userCoordinate == nil { locateMe() }
        }
        .navigationBarTitleDisplayMode(.inline)
        // Removed: local NavigationStack + navigationDestination
    }

    private func reloadReports() {
        guard !isLoadingReports else { return }
        isLoadingReports = true
        reportsError = nil

        Shared.ReportRepositoryImpl().getAllReports { list, error in
            DispatchQueue.main.async {
                self.isLoadingReports = false
                if let error = error {
                    self.reportsError = error.localizedDescription
                    self.reports = []
                    return
                }
                if let arr = list as? [ReportModel] {
                    self.reports = arr
                } else if let anyArr = list as? [Any] {
                    self.reports = anyArr.compactMap { $0 as? ReportModel }
                } else {
                    self.reports = []
                }
            }
        }
    }

    private func locateMe() {
        guard !isLocating else { return }
        isLocating = true
        locationError = nil

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
                    MKCoordinateRegion(
                        center: coord,
                        span: MKCoordinateSpan(latitudeDelta: 0.02, longitudeDelta: 0.02)
                    )
                )
            }
        }
    }
}
