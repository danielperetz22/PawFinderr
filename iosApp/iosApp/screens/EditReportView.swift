import SwiftUI
import Shared

struct EditReportView: View {
    let report: ReportModel
    var onSave: (_ description: String, _ name: String, _ phone: String, _ isLost: Bool) -> Void

    // Local editable copies
    @State private var descriptionText: String
    @State private var nameText: String
    @State private var phoneText: String
    @State private var isLostValue: Bool

    init(
        report: ReportModel,
        onSave: @escaping (_ description: String, _ name: String, _ phone: String, _ isLost: Bool) -> Void
    ) {
        self.report = report
        self.onSave = onSave
        _descriptionText = State(initialValue: report.description_)
        _nameText        = State(initialValue: report.name)
        _phoneText       = State(initialValue: report.phone)
        _isLostValue     = State(initialValue: report.isLost)
    }

    var body: some View {
        ZStack {
            Color("BackgroundGray").ignoresSafeArea()

            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // Top image (preview current)
                    if !report.imageUrl.isEmpty, let url = URL(string: report.imageUrl) {
                        AsyncImage(url: url) { img in
                            img.resizable().scaledToFill()
                        } placeholder: {
                            Color.gray.opacity(0.2)
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 220)
                        .clipped()
                        .cornerRadius(22)
                    }

                    // LOST / FOUND headline (matches details look)
                    Text(isLostValue ? "lost!" : "found!")
                        .font(.title2.weight(.bold))
                        .foregroundColor(isLostValue ? .red : Color("PrimaryPink"))

                    // Toggle pills (Lost / Found)
                    HStack(spacing: 10) {
                        Button {
                            isLostValue = true
                        } label: {
                            Text("Lost")
                                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity, minHeight: 44)
                                .background(isLostValue ? Color.red : Color("PrimaryPink").opacity(0.5))
                                .cornerRadius(12)
                        }

                        Button {
                            isLostValue = false
                        } label: {
                            Text("Found")
                                .font(.custom("BalooBhaijaan2-Bold", size: 16))
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity, minHeight: 44)
                                .background(!isLostValue ? Color("PrimaryPink") : Color("PrimaryPink").opacity(0.5))
                                .cornerRadius(12)
                        }
                    }

                    // Labeled editors (description / phone / name)
                    LabeledEditor(title: "description :", text: $descriptionText, multiline: true)
                    LabeledEditor(title: "contact me :", text: $phoneText)
                    LabeledEditor(title: "name :",        text: $nameText)

                    // Map placeholder (kept for parity with Compose)
                    RoundedRectangle(cornerRadius: 20)
                        .fill(Color.white)
                        .overlay(
                            RoundedRectangle(cornerRadius: 20)
                                .stroke(Color(.systemGray4), lineWidth: 1)
                        )
                        .frame(height: 180)
                        .overlay(Text("map").foregroundColor(.secondary))

                    // leave room for sticky Save
                    Spacer().frame(height: 108)
                }
                .padding(.horizontal, 16)
                .padding(.top, 16)
            }

            // Sticky Save button
            VStack {
                Spacer()
                Button {
                    onSave(descriptionText, nameText, phoneText, isLostValue)
                } label: {
                    Text("Save changes")
                        .font(.custom("BalooBhaijaan2-Bold", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity, minHeight: 52)
                        .background(Color("PrimaryPink"))
                        .cornerRadius(14)
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 16)
            }
        }
        .navigationTitle("Edit Report")
        .navigationBarTitleDisplayMode(.inline)
    }
}

// MARK: - Reusable labeled editor (matches your style)
private struct LabeledEditor: View {
    let title: String
    @Binding var text: String
    var multiline: Bool = false

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.caption)
                .foregroundColor(.secondary)

            if multiline {
                TextEditor(text: $text)
                    .font(.custom("BalooBhaijaan2-Bold", size: 16))
                    .frame(minHeight: 100)
                    .padding(10)
                    .background(Color.white)
                    .cornerRadius(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color(.systemGray4), lineWidth: 1)
                    )
            } else {
                TextField("", text: $text)
                    .font(.custom("BalooBhaijaan2-Bold", size: 16))
                    .padding(12)
                    .background(Color.white)
                    .cornerRadius(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color(.systemGray4), lineWidth: 1)
                    )
            }
        }
    }
}
