import Foundation
import SwiftUI
import shared
import StoreKit

class IOSCapabilityProvider: OSCapabilityProvider {
    
    @Environment(\.requestReview) private var requestReview
    
    func openUrl(url: String) {
        guard let parsedUrl = URL(string: url) else {
            print("IOSCapabilityProvider/openUrl: Invalid URL: \(url)")
            return
        }
        UIApplication.shared.open(parsedUrl)
    }
    
    func getPlatform() -> OSCapabilityProviderPlatform {
        return OSCapabilityProviderPlatform.ios
    }
    
    func getAppVersion() -> String {
        return Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "0.0.0"
    }
    
    func managePurchases(){
        openUrl(url: "https://apps.apple.com/account/subscriptions")
    }
    
    func openAppSettings(){
        guard let settingsUrl = URL(string: UIApplication.openSettingsURLString) else {
            print("IOSCapabilityProvider/openAppSettings: Invalid URL: \(UIApplication.openSettingsURLString)")
            return
        }
        UIApplication.shared.open(settingsUrl)
    }
    
    func requestStoreReview(){
        Task {
            await requestReview()
        }
    }
    
    func shareImage(imageByteArray: KotlinByteArray, mimeType: String, title: String, message: String) {
        guard let image = UIImage(data: imageByteArray.toData()) else {
            print("IOSCapabilityProvider/shareImage: Failed to create UIImage from data")
            return
        }
        let activityItems: [Any] = [image, message]
        
        // Create and present UIActivityViewController
        DispatchQueue.main.async {
            let activityViewController = UIActivityViewController(activityItems: activityItems, applicationActivities: nil)
            
            // Set a subject for the share sheet (optional)
            activityViewController.setValue(title, forKey: "subject")
            
            // Present the view controller
            UIApplication.shared.windows.first?.rootViewController?.present(activityViewController, animated: true, completion: nil)
        }
    }
    
    func vibrate(durationMs: Int64, strength: OSCapabilityProviderVibrationStrength) {
        let generator = UIImpactFeedbackGenerator(style: .heavy)
        generator.impactOccurred()
    }
    
}
