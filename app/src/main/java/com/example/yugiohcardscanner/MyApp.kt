package com.example.yugiohcardscanner

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * [MyApp] is the main application class for the YuGiOh Card Scanner app.
 *
 * It serves as the entry point for the application and is annotated with `@HiltAndroidApp`,
 * enabling Hilt dependency injection for the entire app.
 *
 * This class is responsible for initializing the application-level components and
 * provides a global context for the rest of the application.
 */
@HiltAndroidApp
class MyApp : Application()