# How to Generate APK File

## Option 1: Build APK in Android Studio (Recommended)

### Step-by-Step:

1. **Open Project in Android Studio**
   - Launch Android Studio
   - Click "Open" and select the `FibFilterApp` folder
   - Wait for Gradle sync to complete

2. **Build Debug APK**
   ```
   Menu: Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```
   - Wait for build to complete (2-5 minutes first time)
   - Click "locate" in the notification that appears
   - APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

3. **Build Release APK (Signed)**
   ```
   Menu: Build → Generate Signed Bundle / APK
   ```
   - Select "APK"
   - Create or select keystore
   - Choose "release" build variant
   - APK will be at: `app/build/outputs/apk/release/app-release.apk`

## Option 2: Build APK via Command Line

### Prerequisites:
- Android SDK installed
- Java JDK 17 installed
- Environment variables set (ANDROID_HOME, JAVA_HOME)

### Commands:

**For Debug APK:**
```bash
cd FibFilterApp
./gradlew assembleDebug
```
APK location: `app/build/outputs/apk/debug/app-debug.apk`

**For Release APK:**
```bash
./gradlew assembleRelease
```
APK location: `app/build/outputs/apk/release/app-release-unsigned.apk`

## Option 3: Online Build Service (No Installation Required)

If you don't have Android Studio installed, you can use online build services:

### AppGyver / Appetize.io
1. Zip the entire FibFilterApp folder
2. Upload to online Android build service
3. Download generated APK

### GitHub Actions (Automated)
1. Push code to GitHub repository
2. Set up GitHub Actions workflow for Android build
3. APK will be generated automatically

## APK Size Expectations

- **Debug APK**: ~15-20 MB
- **Release APK**: ~10-15 MB (with ProGuard/R8 optimization)

## Installation on Android Device

### Via USB (ADB):
```bash
adb install app-debug.apk
```

### Direct Install:
1. Copy APK to phone
2. Enable "Install from Unknown Sources" in Settings
3. Tap APK file to install

## Troubleshooting Build Issues

### Issue: Gradle sync failed
**Solution**: 
- Check internet connection
- Update Android Studio
- Invalidate caches: File → Invalidate Caches / Restart

### Issue: SDK not found
**Solution**:
- Install Android SDK via Android Studio
- Set ANDROID_HOME environment variable
- Install required SDK platforms (API 34)

### Issue: Build takes too long
**Solution**:
- First build downloads dependencies (10-15 minutes)
- Subsequent builds are faster (1-2 minutes)
- Use `--offline` flag if dependencies are cached

### Issue: Out of memory
**Solution**:
Edit `gradle.properties`:
```
org.gradle.jvmargs=-Xmx4096m
```

## Quick Build Script

Save this as `build.sh` in FibFilterApp folder:

```bash
#!/bin/bash
echo "Building FIB Filter APK..."
./gradlew clean
./gradlew assembleDebug
echo "✓ Build complete!"
echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
```

Make executable: `chmod +x build.sh`
Run: `./build.sh`

## Pre-built APK Request

If you need a pre-built APK immediately:
1. I can provide build configuration
2. You can use cloud build services
3. Or share the project with someone who has Android Studio

## Signing the APK (For Play Store)

### Generate Keystore:
```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

### Sign APK:
```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore my-release-key.jks app-release-unsigned.apk my-key-alias
```

### Verify Signing:
```bash
jarsigner -verify -verbose -certs app-release.apk
```

## Next Steps After Building

1. **Test APK** on emulator or device
2. **Install** on target devices
3. **Distribute** via email, Drive, or Play Store

Need help with any step? Check the main README.md or QUICKSTART.md!
