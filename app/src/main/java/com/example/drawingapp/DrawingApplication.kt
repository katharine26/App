package com.example.drawingapp

import android.app.Application
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DrawingApplication : Application() {
    //coroutine scope tied to the application lifetime which we can run suspend functions in
    val scope = CoroutineScope(SupervisorJob())

    //get a reference to the DB singleton
    val db by lazy {
        Room.databaseBuilder(
            applicationContext, DrawingsDatabase::class.java, "drawing_database"
        ).build()
    }

    //create our repository singleton, using lazy to access the DB when we need it
    val drawingsRepository by lazy { drawingsRepository(scope, db.drawingDao()) }
}