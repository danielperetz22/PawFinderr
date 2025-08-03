import Foundation
import Cloudinary

struct CloudinaryUploader {
  /// Uploads JPEG data (signed with your API secret), calls back with the secure URL or `nil` on failure.
  static func upload(
    _ data: Data,
    completion: @escaping (String?) -> Void
  ) {
    let params = CLDUploadRequestParams()
      .setResourceType(.image)
      // optionally: .setFolder("reports")
    
    CloudinaryManager.shared
      .createUploader()
      .signedUpload(
        data: data,
        params: params,
        progress: nil
      ) { result, error in
        if let secureUrl = result?.secureUrl {
          completion(secureUrl)
        } else {
          print("⛔️ Cloudinary signedUpload failed:", error?.localizedDescription ?? "unknown")
          completion(nil)
        }
      }
  }
}
