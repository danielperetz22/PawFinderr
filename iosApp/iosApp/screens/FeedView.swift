import SwiftUI
import MapKit
import Shared

struct FeedView: View {
    @EnvironmentObject private var session: SessionStore

    @State private var cameraPosition: MapCameraPosition = .region(
        MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 32.0853, longitude: 34.7818),
            span: MKCoordinateSpan(latitudeDelta: 0.05, longitudeDelta: 0.05)
        )
    )
    @State private var userCoordinate: CLLocationCoordinate2D?
    @State private var isLocating = false
    @State private var locationError: String?

    @State private var reports: [ReportModel] = []
    @State private var isLoadingReports = false
    @State private var reportsError: String?

    @State private var selectedReport: ReportModel? = nil
    @State private var showNewReport = false

    var body: some View {
        ZStack {
            Color("BackgroundGray").ignoresSafeArea()

            VStack(spacing: 16) {
                Map(position: $cameraPosition) {
                    UserAnnotation()

                    ForEach(reports, id: \.id) { rpt in
                        let lat = rpt.lat
                        let lng = rpt.lng
                        if !lat.isNaN, !lng.isNaN {
                            let coord = CLLocationCoordinate2D(latitude: lat, longitude: lng)
                            Annotation("", coordinate: coord) {
                                VStack(spacing: 2) {
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
                .padding(.bottom, 16)
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
                    .padding(16)
                }

                HStack {
                    Spacer()
                    Button {
                        showNewReport = true
                    } label: {
                        Image(systemName: "plus")
                            .font(.system(size: 20, weight: .bold))
                            .foregroundColor(.white)
                            .frame(width: 44, height: 44)
                            .background(Color.darkGreen)
                            .cornerRadius(8)
                            .shadow(color: Color.black.opacity(0.2),
                                    radius: 4, x: 0, y: 2)
                    }
                }

                if isLocating { ProgressView("Getting your location…") }
                if let err = locationError {
                    Text(err).font(.footnote).foregroundColor(.red).padding(.horizontal)
                }
                if let rerr = reportsError {
                    Text(rerr).font(.footnote).foregroundColor(.red).padding(.horizontal)
                }
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 16)
        }
        .onAppear { session.currentTitle = "Feed" }
        .task {
            if reports.isEmpty { reloadReports() }
            if userCoordinate == nil { locateMe() }
        }
        .navigationBarTitleDisplayMode(.inline)
        .sheet(isPresented: $showNewReport) {
            ReportsContainerView()
        }
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
                if let arr = list {
                    self.reports = arr
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
