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
    
    func getCustomerBillingInfo() async throws -> CustomerBillingInfo {
        
        let rcCustomerInfo = try await Purchases.shared.customerInfo()
        return CustomerBillingInfo(
            entitlements: rcCustomerInfo.entitlements.active.keys.map{ $0.description },
            purchases: Array(rcCustomerInfo.allPurchasedProductIdentifiers),
            managementUrl: rcCustomerInfo.managementURL?.absoluteString
        )
        
    }
    
    func getProducts() async throws -> [Product] {
        let offerings = try await Purchases.shared.offerings()
        return offerings.current?.availablePackages.compactMap { package in
            switch package.identifier {
            case ProductStarter.companion.ID:
                return ProductStarter(
                    id: package.storeProduct.productIdentifier,
                    title: package.storeProduct.localizedTitle,
                    description: package.storeProduct.localizedDescription,
                    price: package.storeProduct.localizedPriceString
                )
                
            case ProductAllIn.companion.ID:
                return ProductAllIn(
                    id: package.storeProduct.productIdentifier,
                    title: package.storeProduct.localizedTitle,
                    description: package.storeProduct.localizedDescription,
                    price: package.storeProduct.localizedPriceString
                )
                
            default:
                let period: ProductPeriod
                if let subscriptionPeriod = package.storeProduct.subscriptionPeriod {
                    switch subscriptionPeriod.unit {
                    case .day:
                        period = ProductPeriodDuration(value: Int32(subscriptionPeriod.value), unit: .day)
                    case .week:
                        period = ProductPeriodDuration(value: Int32(subscriptionPeriod.value), unit: .week)
                    case .month:
                        period = ProductPeriodDuration(value: Int32(subscriptionPeriod.value), unit: .month)
                    case .year:
                        period = ProductPeriodDuration(value: Int32(subscriptionPeriod.value), unit: .year)
                    @unknown default:
                        period = ProductPeriodDuration(value: Int32(subscriptionPeriod.value), unit: .unknown)
                    }
                } else {
                    period = ProductPeriodLifetime()
                }
                
                return ProductOther(
                    id: package.storeProduct.productIdentifier,
                    packageId: package.identifier,
                    title: package.storeProduct.localizedTitle,
                    description: package.storeProduct.localizedDescription,
                    price: package.storeProduct.localizedPriceString,
                    period: period
                )
            }
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
