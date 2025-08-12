import SwiftUI
import Shared

struct ReportDetailsView: View {
    let report: ReportModel
    var onEdit: () -> Void = {}

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

                    // Location
                    if let loc = report.location, !loc.isEmpty {
                        HStack(alignment: .firstTextBaseline, spacing: 6) {
                            Text("📍")
                            Text(loc).font(.body)
                        }
                    }
                    // Map placeholder block (optional)
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color.white)
                        .frame(height: 140)
                        .overlay(
                            Text("map")
                                .foregroundColor(.secondary)
                        )

                    // Spacer so button does not overlap content
                    Spacer().frame(height: 88)
                }
                .padding(.horizontal, 16)
                .padding(.top, 16)
            }

            // Bottom Edit button
            VStack {
                Spacer()
                Button(action: onEdit) {
                    Text("Edit")
                        .font(.custom("BalooBhaijaan2-Bold", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity, minHeight: 48)
                        .background(Color("PrimaryPink"))
                        .cornerRadius(8)
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 16)
            }
        }
        .navigationTitle("report details")
        .navigationBarTitleDisplayMode(.inline)
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

struct ReportDetailsView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            ReportDetailsView(
                report: ReportModel(
                    id: "1",
                    userId: "u",
                    description: "Very friendly dog near the park. Brown, small, wearing a blue collar.",
                    name: "Hila",
                    phone: "+972 50 000 0000",
                    imageUrl: "https://picsum.photos/600/400",
                    isLost: true,
                    location: "Tel Aviv",
                    createdAt: 0
                )
            )
        }
    }
}
