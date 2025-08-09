import SwiftUI
import Shared

struct MyReportsView: View {
    @State private var reports: [ReportModel] = []
    @State private var isLoading = false
    @State private var errorText: String?
    @State private var showNewReport = false   // ← added


    private let repo = ReportRepositoryImpl()
    private let auth = RemoteFirebaseRepository()

    var body: some View {
        ZStack {
            Color(white: 0.95).ignoresSafeArea()

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
                    ReportRow(report: rpt)
                        .listRowSeparator(.hidden)
                        .listRowBackground(Color.clear)
                }
                .listStyle(.plain)
            }
            
            // ↓↓↓ added: floating + button
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
                            .background(Color.blue)
                            .clipShape(Circle())
                            .shadow(radius: 4)
                    }
                    .padding(.trailing, 16)
                    .padding(.bottom, 24)
                }
            }
            // ↑↑↑
        }
        .onAppear { loadReports() }
        .sheet(isPresented: $showNewReport) {   // ← added
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
            self.reports = (list ?? []).sorted(by: { $0.id > $1.id }) // newest first
        }
    }
}

struct ReportRow: View {
    let report: ReportModel

    var body: some View {
        HStack(spacing: 12) {
            if !report.imageUrl.isEmpty, let url = URL(string: report.imageUrl) {
                AsyncImage(url: url) { img in img.resizable() } placeholder: {
                    Color.gray.opacity(0.2)
                }
                .frame(width: 72, height: 72)
                .clipShape(RoundedRectangle(cornerRadius: 12))
            }

            VStack(alignment: .leading, spacing: 4) {
                Text(report.name.isEmpty ? "Untitled" : report.name)
                    .font(.headline)

                let desc = report.description_   // Swift name for Kotlin `description`
                if !desc.isEmpty {
                    Text(desc)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .lineLimit(1)
                }

                HStack {
                    Text(report.isLost ? "Lost" : "Found")
                        .font(.caption)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(Capsule().stroke(.primary, lineWidth: 1))
                    if !report.phone.isEmpty {
                        Text(report.phone).font(.caption)
                    }
                }
            }
            Spacer()
        }
        .padding(10)
        .background(Color.white)
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.05), radius: 3, x: 0, y: 2)
    }
}
