package com.example.drawingapp

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skydoves.colorpickerview.ColorEnvelope

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.drawingapp", appContext.packageName)
    }

    fun test_color_change() {
        //val vm = drawingViewModel(drawingsRepository())
        val colorBefor = 7
        //vm.setSelectedColor(ColorEnvelope(9))
        assertNotEquals(colorBefor, 9)
    }

    fun test_size_change() {
        //val vm = drawingViewModel()
        val sizeBefor = 7f
        //vm.selSelectedSize(50f)
        assertNotEquals(sizeBefor, 50f)

    }

    @Test
    fun test_fill_canvas_green() {
        val bitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)
        val colorToFill = Color.argb(255, 0, 255, 0) // Green color

        // Fill the canvas with the specified color
        DrawingJNI.fillCanvas(bitmap, colorToFill)

        // Verify that all pixels in the bitmap are now filled with the specified color
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                assertEquals(colorToFill, bitmap.getPixel(x, y))
            }
        }
    }

    @Test
    fun test_fill_canvas_black() {
        val bitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)
        // Example color in the format: ARGB
        val colorToFill = Color.argb(255, 255, 255, 255) // Green color

        // Fill the canvas with the specified color
        DrawingJNI.fillCanvas(bitmap, colorToFill)

        // Verify that all pixels in the bitmap are now filled with the specified color
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                assertEquals(colorToFill, bitmap.getPixel(x, y))
            }
        }
    }

    @Test
    fun test_fill_canvas_white() {
        val bitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)
        // Example color in the format: ARGB
        val colorToFill = Color.argb(255, 0, 0, 0) // Green color

        // Fill the canvas with the specified color
        DrawingJNI.fillCanvas(bitmap, colorToFill)

        // Verify that all pixels in the bitmap are now filled with the specified color
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                assertEquals(colorToFill, bitmap.getPixel(x, y))
            }
        }
    }

    @Test
    fun test_clear_canvas() {
        val bitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)

        // Fill the canvas with a non-white color to ensure it's not already cleared
        val colorToFill = Color.RED
        DrawingJNI.fillCanvas(bitmap, colorToFill)

        // Clear the canvas
        DrawingJNI.clearCanvas(bitmap)

        // Verify that all pixels in the bitmap are now white
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                assertEquals(Color.WHITE, bitmap.getPixel(x, y))
            }
        }
    }

    @Test
    fun testDrawLine() {
        val fragment = CanvasFragment()
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        // Call drawLine function with start and end points
        fragment.lastX = 10f
        fragment.lastY = 20f
        fragment.drawLine(90f, 80f)

        // Verify that the line is drawn correctly
        val expectedColor = Color.BLACK
        assertEquals(expectedColor, bitmap.getPixel(10, 20))
        assertEquals(expectedColor, bitmap.getPixel(90, 80))
    }


}