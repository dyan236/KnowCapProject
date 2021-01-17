package com.example.hacknortheastproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.LocalizedObjectAnnotation;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
    private Executor exe = Executors.newSingleThreadExecutor();
    private final String[] REQ_PERMS = {"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.INTERNET"};

    //*** PLEASE READ ***
    //
    //
    private static final String CLOUD_VISION_API_KEY = "[INSERT_YOUR_API_KEY_HERE]";
    //
    // API key removed for privacy reasons; If needed for judging process, please contact us at: jng4723@gmail.com
    //
    //
    //***




    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static String fileLoc;

    PreviewView pView;
    ImageButton b;
    GifImageView gif;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pView = findViewById(R.id.previewView);
        b = findViewById(R.id.button);
        initButton();
        gif = findViewById(R.id.loadingIcon);

        if (permissionsGranted())
            startCamera(); //start camera if permission has been granted by user
        else
            ActivityCompat.requestPermissions(this, REQ_PERMS, 1);
    }

    private void initButton() {
        ImageButton b2 = findViewById(R.id.galleryButton);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {
                try {
                    ProcessCameraProvider provider = future.get();
                    bindPreview(provider);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }
    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };
        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);
        Vision vision = builder.build();
        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);
            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("OBJECT_LOCALIZATION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);

            }});
            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});
        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");
        return annotateRequest;
    }
    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        //mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }
    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private Activity activity;
        private final WeakReference<MainActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(MainActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
            this.activity = activity;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                List<AnnotateImageResponse> responses = response.getResponses();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            Intent i = new Intent(activity, ViewActivity.class);
            i.putExtra("fileLoc", fileLoc);
            i.putExtra("objectName", result);
            activity.startActivity(i);

            System.out.println("***RESULT: " + result);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        String result = "";
        List<AnnotateImageResponse> responses = response.getResponses();
        AnnotateImageResponse res = responses.get(0);
        LocalizedObjectAnnotation entity = res.getLocalizedObjectAnnotations().get(0);
        result += entity.getName();
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void bindPreview(@NonNull ProcessCameraProvider provider) {
        Preview p = new Preview.Builder().build();
        CameraSelector cs = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        ImageAnalysis ia = new ImageAnalysis.Builder().build();
        ImageCapture.Builder iBuilder = new ImageCapture.Builder();
        final ImageCapture ic = iBuilder.setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation()).build();
        p.setSurfaceProvider(pView.getSurfaceProvider());
        Camera c = provider.bindToLifecycle((LifecycleOwner) this, cs, p, ia, ic);
        b.setOnClickListener(v -> {

            b.setVisibility(View.GONE);
            gif.setVisibility(View.VISIBLE);


            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);

            String dateNow = formatter.format(new Date());
            File f = new File(getBatchDirectoryName(), dateNow + ".jpg");
            fileLoc = getBatchDirectoryName() + "/" +dateNow + ".jpg";
            //fileLoc = getBatchDirectoryName() + "/" + "Capture.PNG";

            ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(f).build();
            ic.takePicture(options, exe, new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Image saved. ", Toast.LENGTH_SHORT).show();
                            //File f2 = new File(getBatchDirectoryName(), "Capture.PNG");
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bmOptions);

                            callCloudVision(bitmap);

                        }
                    });
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    exception.printStackTrace();
                    gif.setVisibility(View.GONE);
                    b.setVisibility(View.VISIBLE);

                }
            });
            //System.out.println("***" + fileLoc);
        });
    }

    public String getBatchDirectoryName() {
        String appPath = Environment.getExternalStorageDirectory().toString() + "/HackNortheastProject";
        File dir = new File(appPath);
        if (!dir.exists())
            dir.mkdirs();
        return appPath;
    }

    private boolean permissionsGranted() {
        for (String permission : REQ_PERMS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }
}