import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ZStack {
            Color.black.edgesIgnoringSafeArea(.all) // Change the color as per your requirement
            ComposeView()
        }
            .ignoresSafeArea(.all) // Compose has own keyboard handler
    }
}



