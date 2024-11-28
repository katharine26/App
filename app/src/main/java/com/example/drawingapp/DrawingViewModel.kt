package com.example.drawingapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow

/*
Drawing View Model for app so we can stay lifecycle aware
 */
class DrawingViewModel(private val repository: drawingsRepository) : ViewModel() {

    //Width and Height of our canvas
    private val width: Int = 2250
    private val height: Int = 2600

    //Pen / Rectangle Toggle Button
    private val pen = MutableLiveData(true)
    val observablePen: LiveData<Boolean> = pen

    //Lines we need to draw on the canvas when we are updating
    private val lines = MutableLiveData<MutableList<Line>>(mutableListOf())
    val observableLines = lines as LiveData<List<Line>>

    //Rectangles we need to draw on the canvas when we are updating
    private val rectangles = MutableLiveData<MutableList<Rectangle>>(mutableListOf())
    val observableRectangles = rectangles as LiveData<List<Rectangle>>
    //Start and end of recangle that we are currently drawing
    var startOffset by mutableStateOf(Offset.Zero)
    var endOffset by mutableStateOf(Offset.Zero)

    //Current Bitmap that we are editing
    private val bitmap: MutableLiveData<Bitmap?> =
        MutableLiveData(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
    val observableBitmap = bitmap as LiveData<Bitmap?>

    //Current Color we are drawing with
    private val setColor: MutableLiveData<Color?> = MutableLiveData(null)
    val observableColor = setColor as LiveData<Color?>

    //Current Size of our pen
    private val setSelectedSize: MutableLiveData<Float?> = MutableLiveData(null)
    val observableSize = setSelectedSize as LiveData<Float?>

    //A list of all the filenames loaded from our database
    private val filenames: MutableLiveData<List<String>?> = MutableLiveData(null)
    private val observableFilenames = filenames as LiveData<List<String>>

    //The current file that we need from our database
    private val currentFile: MutableLiveData<String> = MutableLiveData(null)
    private val observableFile = currentFile as LiveData<String>

    //All drawings
    val drawings: Flow<List<Drawing>> = repository.allDrawings
    //Method to define our file that we want to edit
    fun setCurrentFile(cur: String) {
        currentFile.value = cur
    }

    //Method to switch between drawing a rectangle or a line
    fun togglePen() {
        val current = pen.value ?: true // Default to true if null
        pen.value = !current
    }

    //Method to add line to be updated
    fun addLine(line: Line) {
        Log.e("ViewModel AddLine: ", "Adding line " + line.start + " : " + line.end)
        lines.value = (lines.value ?: mutableListOf()).apply {
            add(line)
        }
    }

    //Method to add rectangle to be upadated
    fun addRect(rectangle: Rectangle) {
        Log.e("ViewModel AddLine: ", "Adding line " + rectangle.height + " : " + rectangle.width)
        rectangles.value = (rectangles.value ?: mutableListOf()).apply {
            add(rectangle)
        }
    }

    //Update method to get canvas ready to save
    fun updateBitmap() {
        // Define bitmap size as needed, possibly based on the canvas size
        var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        observableBitmap.value?.let { existingBitmap ->
            canvas.drawBitmap(existingBitmap, 0f, 0f, null)
        }

        //Draw all lines on the bitmap
        lines.value?.forEach { line ->
            val paint = android.graphics.Paint().apply {
                color = line.color.toArgb()
                strokeWidth = line.strokeWidth
                isAntiAlias = true
                strokeCap = android.graphics.Paint.Cap.ROUND
            }
            canvas.drawLine(line.start.x, line.start.y, line.end.x, line.end.y, paint)

        }

        //Draw all rectangles on the bitmap
        rectangles.value?.forEach { rectangle ->
            val paint = android.graphics.Paint().apply {
                color = rectangle.color.toArgb()
                strokeWidth = rectangle.strokeWidth
                style = android.graphics.Paint.Style.STROKE // Use STROKE for outlined rectangles
                isAntiAlias = true
            }
            canvas.drawRect(
                rectangle.topLeft.x,
                rectangle.topLeft.y,
                rectangle.topLeft.x + rectangle.width,
                rectangle.topLeft.y + rectangle.height,
                paint
            )
        }

        //Make sure this bitmap is set as my current bitmap
        setBitmap(bitmap)
    }

    //Pulls a file from my database
    fun getDrawing(filename: String): Bitmap {
        var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        try {
            bitmap = BitmapFactory.decodeFile(filename)
        } catch (e: Exception) {
            Log.e("Error", "Exception while decoding file: $filename", e)
        }
        return bitmap
    }

    //Adds a filename to my list of filenames for future access
    fun addFilename(filename: String) {
        var updatedList = mutableListOf<String>()
        if (observableFilenames.value == null) {
            updatedList.add(filename)
            filenames.value = updatedList
        } else {
            if (observableFilenames.value!!.contains(filename) == false) {
                updatedList = (observableFilenames.value!! + filename).toMutableList()
                filenames.value = updatedList
            }
        }
    }

    fun getFilenames(): LiveData<List<String>> {
        return observableFilenames
    }

    fun getBitmap(): LiveData<Bitmap?> {
        return observableBitmap
    }

    fun setBitmap(newBitmap: Bitmap) {
        bitmap.value = newBitmap
    }

    fun clearLines() {
        lines.value = mutableListOf()
    }

    fun clearRectangles() {
        rectangles.value = mutableListOf()
    }

    //Saves drawing into local storage while saving the filename in my database
    fun saveDrawing(filepath: String) {
        updateBitmap()
        clearLines()
        clearRectangles()
        val filename = currentFile.value
        val newFilePath = "$filepath$filename.png"
        if (observableFilenames.value?.contains(observableFile.value) == true)
            repository.saveDrawing(newFilePath, bitmap.value!!, true)
        else
            repository.saveDrawing(newFilePath, bitmap.value!!, false)
    }

    fun setSelectedColor(color: Color) {
        setColor.value = color
    }

    fun changeBackgroundColor(backgroundColor: Color) {
        clearLines()
        clearRectangles()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(backgroundColor.toArgb())

        setBitmap(bitmap)
    }

    //Creates a new empty drawing
    fun createNewDrawing() {
        getFilenames()
        bitmap.value = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        if (observableFilenames.value?.isEmpty() == true || observableFilenames.value == null) {
            currentFile.value = "file1"
        } else {
            val lastFile = observableFilenames.value?.get(observableFilenames.value!!.size - 1)
            val lastNum = lastFile?.split("file")
            val num = lastNum?.get(lastNum.size - 1)
            val newNum = num?.toIntOrNull()?.plus(1) ?: 1
            currentFile.value = "file$newNum"
        }
    }

    fun selSelectedSize(drawSize: Float) {
        setSelectedSize.value = drawSize
    }

    // This factory class allows us to define custom constructors for the view model
    class ViewModelFactory(private val repository: drawingsRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DrawingViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DrawingViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}