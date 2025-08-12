import SwiftUI
import Shared

struct ReportDetailsView: View {
    let report: ReportModel
    var onEdit: () -> Void = {}          // optional callback if you still want it
    var onDelete: () -> Void = {}

    @State private var showEdit = false
    @State private var showDeleteConfirm = false

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

                    // Location (optional)
                    if let loc = report.location, !loc.isEmpty {
                        HStack(alignment: .firstTextBaseline, spacing: 6) {
                            Text("📍")
                            Text(loc).font(.body)
                        }
                    }

                    // Map placeholder
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color.white)
                        .frame(height: 140)
                        .overlay(
                            Text("map")
                                .foregroundColor(.secondary)
                        )

                    Spacer().frame(height: 104) // leave room for the two buttons
                }
                .padding(.horizontal, 16)
                .padding(.top, 16)
            }

            // Bottom action buttons
            VStack(spacing: 10) {
                Spacer()

                // Edit
                Button {
                    showEdit = true
                    onEdit() // keep your external callback if you need analytics etc.
                } label: {
                    Text("Edit")
                        .font(.custom("BalooBhaijaan2-Bold", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity, minHeight: 48)
                        .background(Color("PrimaryPink"))
                        .cornerRadius(8)
                }

                // Delete
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
        .navigationTitle("report details")
        .navigationBarTitleDisplayMode(.inline)
        .alert("Delete report?", isPresented: $showDeleteConfirm) {
            Button("Delete", role: .destructive) { onDelete() }
            Button("Cancel", role: .cancel) { }
        } message: {
            Text("This action cannot be undone.")
        }
        // NAVIGATION goes on the root view, not on the Button
        .navigationDestination(isPresented: $showEdit) {
            EditReportView(report: report) { description, name, phone, isLost in
                // Handle save inside EditReportView via your shared VM.
                // If you want to pop back after saving:
                // (EditReportView can dismiss itself or call a closure.)
                showEdit = false
            }
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
