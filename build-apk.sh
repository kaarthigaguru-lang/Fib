#!/bin/bash

# FIB Filter APK Builder Script
# This script helps build the APK file

echo "╔════════════════════════════════════════╗"
echo "║   FIB Filter App - APK Builder        ║"
echo "╚════════════════════════════════════════╝"
echo ""

# Check if we're in the right directory
if [ ! -f "settings.gradle.kts" ]; then
    echo "❌ Error: Please run this script from the FibFilterApp directory"
    exit 1
fi

echo "📋 Build Options:"
echo "1. Build Debug APK (fastest, for testing)"
echo "2. Build Release APK (optimized, for distribution)"
echo "3. Clean build files"
echo "4. Install requirements help"
echo ""
read -p "Select option (1-4): " choice

case $choice in
    1)
        echo ""
        echo "🔨 Building Debug APK..."
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        
        if [ -f "gradlew" ]; then
            chmod +x gradlew
            ./gradlew assembleDebug
            
            if [ $? -eq 0 ]; then
                echo ""
                echo "✅ BUILD SUCCESSFUL!"
                echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                echo "📱 APK Location:"
                echo "   app/build/outputs/apk/debug/app-debug.apk"
                echo ""
                echo "📲 To install on device:"
                echo "   adb install app/build/outputs/apk/debug/app-debug.apk"
            else
                echo "❌ Build failed. Check errors above."
            fi
        else
            echo "❌ Gradle wrapper not found."
            echo "💡 Please build using Android Studio instead."
            echo "   File → Build → Build Bundle(s) / APK(s) → Build APK(s)"
        fi
        ;;
        
    2)
        echo ""
        echo "🔨 Building Release APK..."
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        
        if [ -f "gradlew" ]; then
            chmod +x gradlew
            ./gradlew assembleRelease
            
            if [ $? -eq 0 ]; then
                echo ""
                echo "✅ BUILD SUCCESSFUL!"
                echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                echo "📱 APK Location:"
                echo "   app/build/outputs/apk/release/app-release-unsigned.apk"
                echo ""
                echo "⚠️  Note: This APK is unsigned. For distribution:"
                echo "   1. Sign the APK with your keystore"
                echo "   2. Or build signed APK via Android Studio"
            else
                echo "❌ Build failed. Check errors above."
            fi
        else
            echo "❌ Gradle wrapper not found."
            echo "💡 Please build using Android Studio instead."
        fi
        ;;
        
    3)
        echo ""
        echo "🧹 Cleaning build files..."
        if [ -f "gradlew" ]; then
            chmod +x gradlew
            ./gradlew clean
            echo "✅ Clean complete!"
        else
            rm -rf app/build
            rm -rf build
            echo "✅ Build directories removed!"
        fi
        ;;
        
    4)
        echo ""
        echo "📚 REQUIREMENTS FOR BUILDING APK"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo ""
        echo "Method 1: Android Studio (Easiest)"
        echo "  1. Download Android Studio from:"
        echo "     https://developer.android.com/studio"
        echo "  2. Install Android SDK (included)"
        echo "  3. Open this project"
        echo "  4. Build → Build Bundle(s) / APK(s) → Build APK(s)"
        echo ""
        echo "Method 2: Command Line"
        echo "  Requirements:"
        echo "  - Java JDK 17+"
        echo "  - Android SDK (command line tools)"
        echo "  - Gradle (or use included wrapper)"
        echo ""
        echo "  Setup:"
        echo "  1. Install Java JDK 17"
        echo "  2. Install Android SDK"
        echo "  3. Set environment variables:"
        echo "     export ANDROID_HOME=/path/to/android-sdk"
        echo "     export JAVA_HOME=/path/to/jdk"
        echo "  4. Run: ./gradlew assembleDebug"
        echo ""
        echo "Method 3: Use Pre-built APK"
        echo "  If you just need the APK without building:"
        echo "  - Find someone with Android Studio"
        echo "  - Use online build service (AppGyver, etc.)"
        echo "  - Request from developer community"
        echo ""
        ;;
        
    *)
        echo "❌ Invalid option"
        exit 1
        ;;
esac

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "For detailed instructions, see BUILD_APK_GUIDE.md"
echo ""
