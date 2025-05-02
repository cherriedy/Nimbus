package com.optlab.nimbus;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

/**
 * The NimbusApplication class is the entry point of the application, used to initialize Hilt for
 * dependency injection, Timber for logging, and other application-wide configurations. We have to
 * set the application name in the AndroidManifest.xml file to use this class as the application
 * class.
 */
@HiltAndroidApp // Annotation to trigger Hilt's code generation and setup.
public class NimbusApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initTimber();
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
