import Foundation
import RevenueCat
import FirebaseAuth
import shared

class RevenueCatBillingProvider: BillingProvider {
    
    func configure(apiKey: String, userId: String?) async throws {
        Purchases.logLevel = .debug
        Purchases.configure(withAPIKey: apiKey, appUserID: userId)
    }
    
    func logIn(userId: String, email: String?) async throws {
        let _ = try await Purchases.shared.logIn(userId)
        if email != nil {
            Purchases.shared.attribution.setEmail(email)
        }
    }
    
    func setEmail(email: String) async throws {
        Purchases.shared.attribution.setEmail(email)
    }
    
    func logOut() async throws {
        if !Purchases.shared.isAnonymous {
            let _ = try await Purchases.shared.logOut()
        }
    }
    
    func getCustomerInfo() async throws -> BillingProviderCustomerInfo {
        
        let rcCustomerInfo = try await Purchases.shared.customerInfo()
        return BillingProviderCustomerInfo(
            entitlements: rcCustomerInfo.entitlements.active.keys.map{ $0.description },
            purchases: Array(rcCustomerInfo.allPurchasedProductIdentifiers),
            managementUrl: rcCustomerInfo.managementURL?.absoluteString
        )
        
    }
    
    func getProducts() async throws -> [BillingProviderProduct] {
        
        let offerings = try await Purchases.shared.offerings()
        return offerings.current?.availablePackages.map { package in
            let periodUnit = switch package.storeProduct.subscriptionPeriod?.unit {
            case .day: "DAY"
            case .week: "WEEK"
            case .month: "MONTH"
            case .year: "YEAR"
            default: "ONCE"
            }
            return BillingProviderProduct(
                id: package.storeProduct.productIdentifier,
                packageId: package.identifier,
                title: package.storeProduct.localizedTitle,
                description: package.storeProduct.localizedDescription,
                price: package.storeProduct.localizedPriceString,
                period: BillingProviderPeriod(
                    value: Int32(package.storeProduct.subscriptionPeriod?.value ?? 0),
                    unit: periodUnit
                )
            )
        } ?? []
    }
    
    func purchase(packageId: String) async throws {
        
        let offerings = try await Purchases.shared.offerings()
        let package = offerings.current?.availablePackages.first(where: { $0.identifier == packageId })
        if(package != nil){
            let result = try await Purchases.shared.purchase(package: package!)
            print("Purchase result: \(result)")
            if result.transaction == nil {
                throw NSError(domain: "Unknown error", code: 1)
            } else if result.userCancelled {
                throw NSError(domain: "User cancelled purchase", code: 2)
            }
            
        }
    }
    
    func restorePurchases() async throws {
        let result = try await Purchases.shared.restorePurchases()
        print("Restore result: \(result)")
    }
}
