package com.example.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.io.OutputStream

/*
Repository directly accesses the database methods
 */
class drawingsRepository(private val scope: CoroutineScope, private val dao: DrawingDAO) {


    val allDrawings = dao.allDrawings()

    fun saveDrawing(filepath: String, bitmap: Bitmap, exists: Boolean) {
        var filename = ""
        scope.launch {
            val parts = filepath.split("[/.]".toRegex())
            filename = parts[parts.size - 2]
            if (!exists) {
                dao.addDrawingData(Drawing(filename, filepath));
            }
        }
        val out: OutputStream = FileOutputStream(filepath)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
}