import SwiftUI
import Shared

struct MyReportsView: View {
    @State private var reports: [ReportModel] = []
    @State private var isLoading = false
    @State private var errorText: String?
    @State private var showNewReport = false

    private let repo = ReportRepositoryImpl()
    private let auth = RemoteFirebaseRepository()

    var body: some View {
        NavigationStack { // ① add navigation container
            ZStack {
                Color("BackgroundGray").ignoresSafeArea()

                if isLoading {
                    ProgressView().scaleEffect(1.2)
                } else if let err = errorText {
                    Text(err).foregroundColor(.red)
                } else if reports.isEmpty {
                    Text("No reports yet")
                        .font(.title2)
                        .foregroundColor(.gray)
                } else {
                    List(reports, id: \.id) { rpt in
                        // ② wrap row in a NavigationLink to details
                        NavigationLink {
                            ReportDetailsView(report: rpt)
                                .navigationTitle("report details")
                                .navigationBarTitleDisplayMode(.inline)
                        } label: {
                            ReportRow(report: rpt)
                        }
                        .listRowSeparator(.hidden)
                        .listRowBackground(Color.clear)
                    }
                    .listStyle(.plain)
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
                                .font(.system(size: 22, weight: .bold))
                                .foregroundColor(.white)
                                .padding(18)
                                .background(Color("SecondaryPink"))
                                .clipShape(Circle())
                                .shadow(radius: 4)
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
            self.reports = list ?? []
        }
    }
}

struct ReportRow: View {
    let report: ReportModel

    var body: some View {
        HStack(spacing: 14) {
            if !report.imageUrl.isEmpty, let url = URL(string: report.imageUrl) {
                AsyncImage(url: url) { img in img.resizable() } placeholder: {
                    Color.gray.opacity(0.2)
                }
                .frame(width: 88, height: 88)
                .clipShape(RoundedRectangle(cornerRadius: 14))
            }

            VStack(alignment: .leading, spacing: 6) {
                Text(report.name.isEmpty ? "Untitled" : report.name)
                    .font(.headline)

                let desc = report.description_
                if !desc.isEmpty {
                    Text(desc)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .lineLimit(2)
                }

                HStack {
                    Text(report.isLost ? "Lost" : "Found")
                        .font(.caption)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 5)
                        .background(Color("PrimaryPink"))
                        .foregroundColor(.white)
                        .clipShape(Capsule())

                    if !report.phone.isEmpty {
                        Text(report.phone)
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
            }
            Spacer()
        }
        .padding(14)
        .background(Color.white)
        .cornerRadius(14)
        .shadow(color: Color.black.opacity(0.08), radius: 4, x: 0, y: 3)
    }
}
