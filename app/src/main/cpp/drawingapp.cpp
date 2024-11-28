
#include <jni.h>
#include <android/bitmap.h>

extern "C" {
// Method which clears the bitmap (turns all of its pixels white)
JNIEXPORT void JNICALL
Java_com_example_drawingapp_DrawingJNI_00024Companion_clearCanvas(JNIEnv *env, jobject obj,
                                                                  jobject bitmap) {
    AndroidBitmapInfo bitmapInfo;
    void *pixels;

    // Iterate over pixels and set them to white (0xFFFFFFFF)
    for (int y = 0; y < bitmapInfo.height; ++y) {
        for (int x = 0; x < bitmapInfo.width; ++x) {
            int pixelIndex = y * bitmapInfo.width + x;
            uint32_t *pixel = reinterpret_cast<uint32_t *>(static_cast<uint8_t *>(pixels) +
                                                           pixelIndex * 4); // Pointer arithmetic
            *pixel = 0xFFFFFFFF; // Set pixel to white
        }
    }

    // Unlocks pixels of bitmap
    AndroidBitmap_unlockPixels(env, bitmap);
}

// This method fills every pixel of the bitmap with a color which is passed as a parameter
JNIEXPORT void JNICALL
Java_com_example_drawingapp_DrawingJNI_00024Companion_fillCanvas(JNIEnv *env, jobject obj,
                                                                 jobject bitmap, jint color) {

    AndroidBitmapInfo bitmapInfo;
    void *pixels;

    // Iterate over pixels and set them to chosen color
    for (int y = 0; y < bitmapInfo.height; ++y) {
        for (int x = 0; x < bitmapInfo.width; ++x) {
            int pixelIndex = y * bitmapInfo.width + x;
            uint32_t *pixel = reinterpret_cast<uint32_t *>(static_cast<uint8_t *>(pixels) +
                                                           pixelIndex * 4);
            *pixel = static_cast<uint32_t>(color);
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}
}