BinomoAssistant - Android Studio project (minimal)

What this package contains:
- A minimal Android Studio project that analyzes screenshots of trading charts locally
  and returns a simple UP/DOWN signal with a confidence score.
- The app uses a lightweight image-processing heuristic (no OpenCV required) so you can
  build it in Android Studio without extra native SDKs.

Important:
- This project is a starting point. The analysis is heuristic; it is NOT a guaranteed
  profit system. Test extensively in demo mode before risking real money.
- To build an APK:
    1. Open this folder in Android Studio (File > Open > /path/to/BinomoAssistant).
    2. Let Gradle sync. You may need to install the Android SDK and a suitable JDK.
    3. Build > Build Bundle(s) / APK(s) > Build APK(s).
    4. Install the debug APK on your device.

Notes and improvements you can add:
- Integrate TFLite model for better accuracy (app/src/main/assets).
- Add OCR (tess-two) to read price axis and reconstruct accurate OHLC.
- Add logging of signals and results (SQLite) for backtesting.
- Add demo-mode integration with Binomo wrappers only after thorough testing.

Files included: simple Kotlin sources, layouts, Gradle build files.

Enjoy, and be careful with real money.
