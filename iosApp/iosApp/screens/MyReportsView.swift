import SwiftUI
import Shared

struct MyReportsView: View {
    @State private var reports: [ReportModel] = []
    @State private var isLoading = false
    @State private var isRefreshing = false
    @State private var errorText: String?
    @State private var showNewReport = false

    private let repo = ReportRepositoryImpl()
    private let auth = RemoteFirebaseRepository()

    var body: some View {
        NavigationStack {
            ZStack {
                Color("BackgroundGray").ignoresSafeArea()

                if isLoading && reports.isEmpty {
                    VStack(spacing: 10) {
                        ProgressView().scaleEffect(1.2)
                        Text(isRefreshing ? "Refreshing..." : "Loading reports...")
                            .font(.custom("BalooBhaijaan2-Regular", size: 14))
                            .foregroundColor(.secondary)
                    }
                } else if let err = errorText {
                    ScrollView {
                        VStack(spacing: 12) {
                            Text(err)
                                .foregroundColor(.red)
                                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                            Button("Try again") { loadReports() }
                                .buttonStyle(.borderedProminent)
                        }
                        .frame(maxWidth: .infinity, minHeight: 300)
                    }
                    .refreshable { await loadReportsAsync() }
                } else if reports.isEmpty {
                    ScrollView {
                        VStack(spacing: 8) {
                            Text("No reports yet")
                                .font(.custom("BalooBhaijaan2-Bold", size: 24))
                                .foregroundColor(.gray)
                            Text("Pull to refresh")
                                .font(.custom("BalooBhaijaan2-Regular", size: 14))
                                .foregroundColor(.secondary)
                        }
                        .frame(maxWidth: .infinity, minHeight: 300)
                    }
                    .refreshable { await loadReportsAsync() }
                } else {
                    List(reports, id: \.id) { rpt in
                        NavigationLink {
                            ReportDetailsView(report: rpt)
                                .navigationTitle("Report Details")
                                .navigationBarTitleDisplayMode(.inline)
                        } label: {
                            ReportRow(report: rpt)
                        }
                        .listRowSeparator(.hidden)
                        .listRowBackground(Color.clear)
                    }
                    .listStyle(.plain)
                    .padding(.bottom, 12)
                    .refreshable { await loadReportsAsync() }
                }

                // Floating + button
                VStack {
                    Spacer()
                    HStack {
                        Spacer()
                        Button {
                            showNewReport = true
                        } label: {
                            Image(systemName: "plus")
                                .font(.custom("BalooBhaijaan2-Bold", size: 20))
                                .foregroundColor(.white)
                                .frame(width: 44, height: 44)
                                .background(Color.darkGreen)
                                .cornerRadius(8)
                                .shadow(color: Color.black.opacity(0.2),
                                        radius: 4, x: 0, y: 2)
                        }
                        .padding(.trailing, 16)
                        .padding(.bottom, 24)
                    }
                }
            }
            .navigationTitle("My Reports")
            .navigationBarTitleDisplayMode(.inline)
        }
        .onAppear { loadReports() }
        .sheet(isPresented: $showNewReport) {
            ReportsContainerView()
        }
    }

    private func loadReports() {
        guard let uid = auth.currentUserUid() else {
            self.errorText = "Please sign in first."
            return
        }
        isLoading = true
        errorText = nil

        repo.getReportsForUser(userId: uid) { list, err in
            self.isLoading = false
            if let err = err {
                self.errorText = err.localizedDescription
                return
            }
            self.reports = (list ?? []).sorted(by: { $0.id > $1.id })
        }
    }

    private func loadReportsAsync() async {
        guard let uid = auth.currentUserUid() else {
            await MainActor.run { self.errorText = "Please sign in first." }
            return
        }
        await MainActor.run {
            errorText = nil
            isRefreshing = true
        }
        await withCheckedContinuation { (cont: CheckedContinuation<Void, Never>) in
            repo.getReportsForUser(userId: uid) { list, err in
                Task { @MainActor in
                    if let err = err {
                        self.errorText = err.localizedDescription
                    } else {
                        self.reports = (list ?? []).sorted(by: { $0.id > $1.id })
                    }
                    self.isRefreshing = false
                    cont.resume()
                }
            }
        }
    }
}

struct ReportRow: View {
    let report: ReportModel

    var body: some View {
        HStack(spacing: 14) {
            if let url = URL(string: report.imageUrl), !report.imageUrl.isEmpty {
                AsyncImage(url: url) { phase in
                    ZStack(alignment: .top) {
                        switch phase {
                        case .empty:
                            Color.gray.opacity(0.2)
                                .overlay(
                                    ProgressView()
                                        .progressViewStyle(.linear)
                                        .tint(Color("PrimaryPink"))
                                        .frame(height: 3),
                                    alignment: .top
                                )

                        case .success(let image):
                            image
                                .resizable()
                                .scaledToFill()

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
                .frame(width: 88, height: 88)
                .clipShape(RoundedRectangle(cornerRadius: 14))
            } else {
                Color.gray.opacity(0.2)
                    .frame(width: 88, height: 88)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
            }

            VStack(alignment: .leading, spacing: 6) {
                Text(report.name.isEmpty ? "Untitled" : report.name)
                    .font(.custom("BalooBhaijaan2-Bold", size: 16))

                let desc = report.description_
                if !desc.isEmpty {
                    Text(desc)
                        .font(.custom("BalooBhaijaan2-Regular", size: 16))
                        .foregroundColor(.secondary)
                        .truncationMode(.tail)
                        .fixedSize(horizontal: false, vertical: true)
                        .lineLimit(2)
                }

                HStack {
                    Text(report.isLost ? "Lost" : "Found")
                        .font(.custom("BalooBhaijaan2-Bold", size: 16))
                        .padding(.horizontal, 10)
                        .padding(.vertical, 5)
                        .background(Color("PrimaryPink"))
                        .foregroundColor(.white)
                        .clipShape(Capsule())

                    if !report.phone.isEmpty {
                        Text(report.phone)
                            .font(.custom("BalooBhaijaan2-Regular", size: 16))
                            .foregroundColor(.secondary)
                    }
                }
            }
            Spacer()
        }
        .frame(height: 120)
        .padding(14)
        .background(Color.white)
        .cornerRadius(14)
        .shadow(color: Color.black.opacity(0.08), radius: 4, x: 0, y: 3)
    }
}

