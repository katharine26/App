package com.example.drawingapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.drawingapp.databinding.FragmentGalleryBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlin.math.abs
import kotlin.math.min

/*
Current Canvas Fragment. Canvas is now a composable element along with every tool button to edit our canvas
 */
class CanvasFragmentComp : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //Define View Model for further access in this class
        val myViewModel: DrawingViewModel by activityViewModels {
            DrawingViewModel.ViewModelFactory((requireActivity().application as DrawingApplication).drawingsRepository)
        }
        val binding = FragmentGalleryBinding.inflate(layoutInflater)

        //Set my content to be my composable DrawScreen Function
        binding.composeView1.setContent {
            DrawingScreen(Modifier, myViewModel, {
                sizeButton(myViewModel)
            }, {
                saveDrawingButton(myViewModel)
            }, {
                colorPickerButton(myViewModel)
            }, {
                fillCanvasButton(myViewModel)
            }, {
                myViewModel.togglePen()
            })
        }
        return binding.root
    }

    /*
    This button makes the entire canvas one color. Essentially overwriting everything on the canvas and making it the same chosen color.
     */
    private fun fillCanvasButton(viewModel: DrawingViewModel) {
        ColorPickerDialog.Builder(requireContext()).setTitle("ColorPicker Dialog")
            .setPreferenceName("MyColorPickerDialog").setPositiveButton("Confirm",
                ColorEnvelopeListener() { envelope: ColorEnvelope, _: Boolean ->
                    viewModel.changeBackgroundColor(Color(envelope.color))
                }).setNegativeButton("Cancel",
                DialogInterface.OnClickListener() { dialoguInterface: DialogInterface, _: Int ->
                    dialoguInterface.dismiss()
                }).show()
    }

    /*
    This button lets the user change the color of the pen being used
     */
    private fun colorPickerButton(viewModel: DrawingViewModel) {
        ColorPickerDialog.Builder(requireContext()).setTitle("ColorPicker Dialog")
            .setPreferenceName("MyColorPickerDialog").setPositiveButton("Confirm",
                ColorEnvelopeListener() { envelope: ColorEnvelope, _: Boolean ->
                    viewModel.setSelectedColor(Color(envelope.color))
                }).setNegativeButton("Cancel",
                DialogInterface.OnClickListener() { dialoguInterface: DialogInterface, _: Int ->
                    dialoguInterface.dismiss()
                }).show()
    }

    /*
    This button saves my drawing
     */
    private fun saveDrawingButton(viewModel: DrawingViewModel) {
        val filePath = "${context?.filesDir}/"
        Log.e("SaveDrawing CanvFrag Fragment: ", filePath)
        viewModel.saveDrawing(filePath)
        findNavController().navigate(R.id.action_entireDrawingScreenFragment_to_galleryFragment)

    }

    /*
    This button sets the size of my pen
     */
    private fun sizeButton(viewModel: DrawingViewModel) {
        val dialogView = layoutInflater.inflate(R.layout.pen_size_dialog, null)
        val seekBarPenSize = dialogView.findViewById<SeekBar>(R.id.seekBar2)

        val alertDialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
            .setTitle("Pen Size").setPositiveButton("OK") { dialog, _ ->
                val penSize = seekBarPenSize.progress
                viewModel.selSelectedSize(penSize.toFloat())
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        alertDialogBuilder.show()
    }
}

/*
Manages my Canvas and my Tool Buttons
 */
@Composable
fun DrawingScreen(
    modifier: Modifier = Modifier,
    viewModel: DrawingViewModel,
    sizeButton: () -> Unit,
    saveDrawing: () -> Unit,
    colorPicker: () -> Unit,
    fillCanvas: () -> Unit,
    toggleButton: () -> Unit
) {
    val navy = Color(0xFF000080) //Button Color
    val buttonSize = 22.dp
    Box(modifier = modifier.fillMaxSize()) {
        //Canvas
        Column(modifier = Modifier.fillMaxSize()) {
            canvasScreen(viewModel)
        }
        //Box to hold all buttons in a row at the bottom of my screen
        Box(
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                item {
                    Box(
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        //Save File Button
                        Button(
                            onClick = saveDrawing,
                            modifier = Modifier.align(Alignment.BottomEnd),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = navy, // Background color
                                contentColor = Color.White   // Text or icon color
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.save),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(buttonSize)
                            )
                        }
                    }
                }

                item {
                    Box(
                    ) {
                        //Pen Size Button
                        Button(
                            onClick = sizeButton,
                            modifier = Modifier.align(Alignment.BottomEnd),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = navy, // Background color
                                contentColor = Color.White   // Text or icon color
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.size),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(buttonSize)
                            )
                        }
                    }
                }
                item {
                    //Color Picker Button
                    Box(
                    ) {
                        Button(
                            onClick = colorPicker,
                            modifier = Modifier.align(Alignment.BottomEnd),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = navy, // Background color
                                contentColor = Color.White   // Text or icon color
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.color),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(buttonSize)
                            )
                        }
                    }
                }
                item {
                    //Fill Canvas Button
                    Box(
                    ) {
                        Button(
                            onClick = fillCanvas,
                            modifier = Modifier.align(Alignment.BottomEnd),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = navy, // Background color
                                contentColor = Color.White   // Text or icon color
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.paint),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(buttonSize)
                            )
                        }
                    }
                }
                item {
                    Box(
                    ) {
                        //Pen to Rectangle Toggle Button
                        Button(
                            onClick = toggleButton,
                            modifier = Modifier.align(Alignment.BottomEnd),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = navy, // Background color
                                contentColor = Color.White   // Text or icon color
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.pentoggle),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(buttonSize)
                            )
                        }
                    }
                }
            }
        }
    }
}

/*
This function manages drawing on the canvas in real time
 */
@Composable
fun canvasScreen(myViewModel: DrawingViewModel) {

    val lines = remember {
        mutableStateListOf<Line>()
    }

    val rectangles = remember {
        mutableStateListOf<Rectangle>()
    }

    val bitmap = myViewModel.observableBitmap.observeAsState()
    // Convert bitmap to ImageBitmap when it’s not null
    val imageBitmap = bitmap.value?.asImageBitmap()
    //This will decide if i am drawing lines or rectangles
    val isPenMode = myViewModel.observablePen.observeAsState(initial = false)

    //Define my canvas
    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures(onDragStart = { startOffset ->
                // Save the start position
                myViewModel.startOffset = startOffset
            }, onDrag = { change, dragAmount ->
                change.consume()
                // Update the end position dynamically
                myViewModel.endOffset = change.position

                if (isPenMode.value) {
                    // Handle line drawing
                    val line = Line(
                        color = myViewModel.observableColor.value
                            ?: Color.Black, // Default to black
                        start = change.position - dragAmount,
                        end = change.position,
                        strokeWidth = myViewModel.observableSize.value ?: 10f // Default to 10f
                    )
                    lines.add(line)
                    myViewModel.addLine(line)
                }
            }, onDragEnd = {
                if (!isPenMode.value) {
                    // Handle rectangle drawing
                    val startOffset = myViewModel.startOffset
                    val endOffset = myViewModel.endOffset

                    val width = abs(endOffset.x - startOffset.x)
                    val height = abs(endOffset.y - startOffset.y)

                    val topLeft = Offset(
                        x = min(startOffset.x, endOffset.x), y = min(startOffset.y, endOffset.y)
                    )

                    val rectangle = Rectangle(
                        topLeft = topLeft,
                        width = width,
                        height = height,
                        color = myViewModel.observableColor.value ?: Color.Black,
                        strokeWidth = myViewModel.observableSize.value ?: 10f
                    )
                    rectangles.add(rectangle)
                    myViewModel.addRect(rectangle)
                }
            })
        }) {
        // Draw the base image if it exists
        imageBitmap?.let {
            drawImage(it, topLeft = Offset.Zero)
        }

        //Draw all lines and rectangles
        lines.forEach { line ->
            drawLine(
                color = line.color,
                start = line.start,
                end = line.end,
                strokeWidth = line.strokeWidth,
                cap = StrokeCap.Round
            )
        }
        rectangles.forEach { rectangle ->
            drawRect(
                color = rectangle.color,
                topLeft = rectangle.topLeft,
                size = Size(rectangle.width, rectangle.height),
                style = Stroke(width = rectangle.strokeWidth)
            )
        }

    }
}

/*
Data Class that defines a line
 */
data class Line(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val strokeWidth: Float = 10f,
)

/*
Data Class that defines my rectangles
 */
data class Rectangle(
    val topLeft: Offset,
    val width: Float,
    val height: Float,
    val color: Color,
    val strokeWidth: Float
)