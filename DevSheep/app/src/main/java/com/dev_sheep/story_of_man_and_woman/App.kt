package com.dev_sheep.story_of_man_and_woman

import android.app.Application
import com.dev_sheep.story_of_man_and_woman.di.appComponent
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    companion object {
        @Volatile lateinit var myApp: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        val database = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)
        configureDI()
    }


    private fun configureDI() = startKoin {
        androidContext(this@App)

        modules(appComponent)
    }
}
