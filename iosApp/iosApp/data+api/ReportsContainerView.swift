import SwiftUI
import Shared

struct ReportsContainerView: View {
  // Instantiate with the new zero-arg init
  private let reportVm = ReportViewModel()

  var body: some View {
    NewReportView(
      onAddPhoto:    { },
      onAddLocation: { },
      onPublish:     { description, name, phone, isLost, imageUrl, lat, lng in
        reportVm.saveReport(
          description: description,
          name:        name,
          phone:       phone,
          imageUrl:    imageUrl,
          isLost:      isLost,
          location: nil,
          lat: lat,
          lng: lng
        )
      }
    )
  }
}
