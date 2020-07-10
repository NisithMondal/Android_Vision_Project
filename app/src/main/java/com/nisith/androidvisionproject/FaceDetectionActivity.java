package com.nisith.androidvisionproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.nisith.androidvisionproject.Halper.GraphicOverlay;
import com.nisith.androidvisionproject.Halper.RectOverlay;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class FaceDetectionActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button detectFaceButton;
    private TextView resultTextView;
    private static int REQUEST_CODE = 100;
    private GraphicOverlay graphicOverlay;
//    private CameraKitView cameraKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);
        imageView = findViewById(R.id.image_view);
        detectFaceButton = findViewById(R.id.detect_face_button);
        resultTextView = findViewById(R.id.result_text_view);
        graphicOverlay = findViewById(R.id.graphic_overlay);
//        cameraKitView = findViewById(R.id.camera);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Pick Image"), REQUEST_CODE);
            }
        });
    }














    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri selectedImageUri = data.getData();
            Picasso.get().load(selectedImageUri).into(imageView);
            performFaceDetectionWithMLKit(selectedImageUri);
        }
    }

    private void performFaceDetectionWithMLKit(Uri selectedImageUri){
        FirebaseVisionImage visionImage;
        Bitmap imageMutableBitmap;
        graphicOverlay.clear();

        try {
            visionImage = FirebaseVisionImage.fromFilePath(getApplicationContext(), selectedImageUri);
            final Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            imageMutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas  = new Canvas(imageMutableBitmap);
            FirebaseVisionFaceDetectorOptions options =
                    new FirebaseVisionFaceDetectorOptions.Builder()
                            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                            .enableTracking()
                            .build();
            FirebaseVisionFaceDetector faceDetector = FirebaseVision.getInstance()
                    .getVisionFaceDetector(options);
            faceDetector.detectInImage(visionImage)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                            String result = "";
                            for (FirebaseVisionFace firebaseVisionFace : firebaseVisionFaces){
                                Rect rect = firebaseVisionFace.getBoundingBox();
//                                Paint paint = new Paint();
//                                paint.setColor(Color.RED);
//                                paint.setStyle(Paint.Style.STROKE);
//                                paint.setStrokeWidth(10f);
//                                canvas.drawRect(rect, paint);
//                                imageView.draw(canvas);
                                RectOverlay rectOverlay = new RectOverlay(graphicOverlay, rect);
                                graphicOverlay.add(rectOverlay);

                                Log.d("ABCD", "Face Traking Id= "+firebaseVisionFace.getTrackingId());

                                FirebaseVisionFaceLandmark leftEar = firebaseVisionFace.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                FirebaseVisionFaceLandmark rightEar = firebaseVisionFace.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR);
                                FirebaseVisionPoint leftEarPos = null;
                                Log.d("ABCD", "Left ear="+leftEar.getPosition().getX());
                                if (leftEar != null) {
                                    leftEarPos = leftEar.getPosition();

                                }
                                result = result + "SmilingProbability= " + firebaseVisionFace.getSmilingProbability() +
                                        " LeftEyeOpenProbability= " + firebaseVisionFace.getLeftEyeOpenProbability() +
                                        " RightEyeOpenProbability= " + firebaseVisionFace.getRightEyeOpenProbability()+ "X= "+ leftEarPos.getX()
                                        + "Y= "+ leftEarPos.getY() + "\n\n";
                            }
                            resultTextView.setText(result);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("ABCD", e.getMessage());
                        }
                    });


        }catch (IOException e){
            e.printStackTrace();
        }
    }




}