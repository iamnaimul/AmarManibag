package com.iamnaimul.amarmanibag

import android.app.Application

class AmarManibagApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
