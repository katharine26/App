package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/*
Drawing View where we define our onDraw function
 */
class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private lateinit var bitmap: Bitmap

    override fun onDraw(canvas: Canvas) {
        canvas?.let { nonNullCanvas ->
            super.onDraw(nonNullCanvas)
            canvas.drawBitmap(bitmap, null, Rect(0, 0, width, height), null)
        }
    }

}
