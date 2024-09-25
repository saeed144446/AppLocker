package com.example.robinblue.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelfieIntruderCapture {

    private final Context context;
    private final ExecutorService cameraExecutor;
    private ImageCapture imageCapture;

    public SelfieIntruderCapture(Context context) {
        this.context = context;
        this.cameraExecutor = Executors.newSingleThreadExecutor();
    }

    // Method to start the camera and initialize camera setup
    public void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, preview, imageCapture);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));

    }
    
    public void captureSelfie() {
        String filename = "intruder_selfie_" + System.currentTimeMillis() + ".jpg";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/IntruderSelfies");

            Uri imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            if (imageUri != null) {
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(imageUri)) {
                    if (outputStream != null) {
                        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(outputStream).build();

                        imageCapture.takePicture(outputOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                ((android.app.Activity) context).runOnUiThread(() -> {
                                    Toast.makeText(context, "Intruder selfie saved to gallery!", Toast.LENGTH_SHORT).show();
                                });
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                Log.e("CaptureSelfie", "Error capturing image: " + exception.getMessage());
                            }
                        });
                    } else {
                        Log.e("CaptureSelfie", "Failed to open OutputStream.");
                    }
                } catch (Exception e) {
                    Log.e("CaptureSelfie", "Exception: " + e.getMessage());
                }
            } else {
                Log.e("CaptureSelfie", "Failed to insert image into MediaStore.");
            }
        } else {
            File photoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                    "IntruderSelfies/" + filename);

            if (!photoFile.getParentFile().exists()) {
                photoFile.getParentFile().mkdirs();
            }

            ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

            imageCapture.takePicture(outputOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    MediaScannerConnection.scanFile(context, new String[]{photoFile.getAbsolutePath()},
                            null, (path, uri) -> ((android.app.Activity) context).runOnUiThread(() -> {
                                Toast.makeText(context, "Intruder selfie saved to gallery!", Toast.LENGTH_SHORT).show();
                            }));
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    Log.e("CaptureSelfie", "Error capturing image: " + exception.getMessage());
                }
            });
        }
    }



    public void shutdown() {
        cameraExecutor.shutdown(); // Shutdown the camera executor when done
    }
}
