import Cloudinary

final class CloudinaryManager {
  static let shared: CLDCloudinary = {
    // replace with your real values:
    let config = CLDConfiguration(
      cloudName: "duk7ujnww",
      apiKey:    "344296978824576",
      apiSecret: "WI-D8ORFmsk1cJWP2aGuiu6mPWk"
    )
    return CLDCloudinary(configuration: config)
  }()
}
