package com.example.drawingapp

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drawingapp.databinding.FragmentGalleryBinding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController

/*
Fragment that holds and displays my gallery of images along with allowing me to add a new image
 */
class GalleryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Get my viewmodel
        val myViewModel: DrawingViewModel by activityViewModels() {
            DrawingViewModel.ViewModelFactory((requireActivity().application as DrawingApplication).drawingsRepository)
        }
        val bitmapLiveData: LiveData<Bitmap?> = myViewModel.getBitmap()
        val binding = FragmentGalleryBinding.inflate(layoutInflater)

        binding.composeView1.setContent {
            MyComposable(Modifier.padding(16.dp),
                {
                    //New Drawing Button
                    myViewModel.createNewDrawing()
                    myViewModel.clearLines()
                    findNavController().navigate(R.id.action_galleryFragment_to_entireDrawingScreenFragment)
                },
                {
                    //Edit Drawing from Gallery Button
                    myViewModel.clearLines()
                    findNavController().navigate(R.id.action_galleryFragment_to_entireDrawingScreenFragment)

                })
        }
        return binding.root
    }
}
/*
Composable method that holds my gallery and my create new button
 */
@Composable
fun MyComposable(
    modifier: Modifier = Modifier,
    newDrawing: () -> Unit, editDrawing: () -> Unit
) {
    val Navy = Color(0xFF000080)
    Box(modifier = modifier.fillMaxSize()) {
        // Scrollable content (image grid)
        Column(modifier = Modifier.fillMaxSize()) {
            ImageListHandler(onItemClick = editDrawing)
        }

        // New Drawing button at the bottom
        Button(
            onClick = newDrawing,
            modifier = Modifier
                .align(Alignment.BottomEnd),
            colors = ButtonDefaults.buttonColors(
                containerColor = Navy, // Background color
                contentColor = Color.White   // Text or icon color
            )
        ) {
            Text(
                "+",
                fontSize = 50.sp
            )
        }
    }
}

/*
List/Gallery of images
 */
@Composable
fun ImageListHandler(
    viewModel: DrawingViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current.findActivity()
    ), onItemClick: () -> Unit
) {
    //Get a list of all drawing filenames in my database
    val drawingList by viewModel.drawings.collectAsState(initial = emptyList())
    val bitmapList: MutableList<Bitmap> = mutableListOf()

    //for each filename, load the bitmap and add it to my list of bitmaps
    for (drawing in drawingList) {
        val bitmap: Bitmap = viewModel.getDrawing(drawing.filepath)
        viewModel.addFilename(drawing.filename)
        bitmapList.add(bitmap)
    }
    val bitmapListCopy: MutableList<Bitmap> = mutableListOf()

    for (bitmap in bitmapList) {
        bitmapListCopy.add(bitmap.copy(Bitmap.Config.ARGB_8888, true))
    }
    // Display the grid of bitmaps
    DisplayGrid(bitmapListCopy, bitmapList, drawingList, viewModel, onItemClick)
}

/*
Show my grid given my lists of Drawings and bitmaps
 */
@Composable
fun DisplayGrid(
    copyList: List<Bitmap>?,
    bitmapList: List<Bitmap>?,
    drawingList: List<Drawing>,
    viewModel: DrawingViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current.findActivity()
    ),
    onItemClick: () -> Unit
) {
    var selectedFilename by remember { mutableStateOf("") }
    if (copyList != null) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp)
        ) {
            itemsIndexed(copyList) { index, photo ->
                val mutablePhoto = photo.copy(Bitmap.Config.ARGB_8888, true)
                Image(
                    bitmap = mutablePhoto.asImageBitmap(),
                    contentDescription = "Bitmap Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clickable {
                            selectedFilename =
                                drawingList[index].filepath // Update the selected filename
                            val bit = bitmapList?.get(index)
                            if (bit != null) {
                                viewModel.setBitmap(bit)
                            }
                            viewModel.setCurrentFile(drawingList[index].filename)
                            onItemClick()
                        }
                )
            }
        }
    }
}
