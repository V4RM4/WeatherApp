# WeatherApp Project Setup Guide

This guide provides detailed instructions for setting up an Android Kotlin project using Android Studio.

## Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable version recommended)
- JDK 11 or higher
- At least 8GB RAM (16GB recommended)
- 10GB+ free disk space

## Installation

### Install Android Studio

1. Download Android Studio from the [official website](https://developer.android.com/studio)
2. Follow the installation wizard instructions for your operating system
3. Launch Android Studio after installation

## Running Your Project

### Open the project by clicking the "Clone Repository" button on Android Studio

### 1. Set Up an Emulator

1. Go to Tools > Device Manager
2. Click "Create Device"
3. Select a device definition (e.g., Pixel 6)
4. Select a system image (e.g., Android 13)
5. Configure the AVD and click "Finish"

### 2. Connect a Physical Device (Alternative)

1. Enable Developer Options on your device:
   - Go to Settings > About Phone
   - Tap "Build Number" 7 times
2. Enable USB Debugging in Developer Options
3. Connect your device via USB
4. Allow USB debugging when prompted on your device

### 3. Run the App

1. Click the green "Run" button in the toolbar
2. Select your emulator or connected device
3. Wait for the app to build and install


## Troubleshooting

- **Build Errors**: Try "File > Invalidate Caches / Restart"
- **Gradle Sync Issues**: Check your internet connection and proxy settings
- **Emulator Problems**: Update your graphics drivers and HAXM installation


