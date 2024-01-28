import ComposeApp
import SwiftUI

@main
struct iOSApp: App {
	@UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate


	init() {
		AppModuleKt.doInitKoin(appDeclaration: { _ in })
	}

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
