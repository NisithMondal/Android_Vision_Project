package com.nisith.androidvisionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

public class FaceDetectionWithCameraActivity extends AppCompatActivity {

    private Button capturePhotoButton;
    private CameraView cameraView;
    private ProgressBar progressBar;
    private TextView resultTextView;
    private ImageView cameraFacingImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection_with_camera);
        capturePhotoButton = findViewById(R.id.capture_photo_button);
        cameraView = findViewById(R.id.camera_view);
        progressBar = findViewById(R.id.progress_bar);
        resultTextView = findViewById(R.id.result_text_view);
        progressBar.setVisibility(View.GONE);
        cameraFacingImageView = findViewById(R.id.camera_facing_image_view);
        cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
        capturePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
                resultTextView.setText("");
            }
        });

        cameraFacingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraView.getFacing() == CameraKit.Constants.FACING_FRONT){
                    cameraView.setFacing(CameraKit.Constants.FACING_BACK);
                    Toast.makeText(FaceDetectionWithCameraActivity.this, "Back Camera is Selected", Toast.LENGTH_SHORT).show();
                }else {
                    cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
                    Toast.makeText(FaceDetectionWithCameraActivity.this, "Front Camera is Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                Bitmap imageBitmap = cameraKitImage.getBitmap();
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();
                progressBar.setVisibility(View.VISIBLE);
                capturePhotoButton.setEnabled(false);
                cameraFacingImageView.setEnabled(false);
                startFaceDetection(imageBitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }


    private void startFaceDetection(Bitmap imageBitmap){
        FirebaseVisionImage firebaseVisionImage;
        firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();

        FirebaseVisionFaceDetector faceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);
        faceDetector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        progressBar.setVisibility(View.GONE);
                        capturePhotoButton.setEnabled(true);
                        cameraFacingImageView.setEnabled(true);
                        String result = "";
                        int index = 1;
                        if (firebaseVisionFaces.size()>0) {
                            for (FirebaseVisionFace face : firebaseVisionFaces) {
                                result = result + "Face_" + index + " Smiling Probability= " + face.getSmilingProbability() + "\n";
                                index++;
                            }
                        }else {
                            result = "Face Not Detected, try Again";
                        }
                        resultTextView.setText(result);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        capturePhotoButton.setEnabled(true);
                        cameraFacingImageView.setEnabled(true);
                        Toast.makeText(FaceDetectionWithCameraActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }





}