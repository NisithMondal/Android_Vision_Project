package com.nisith.androidvisionproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button imageLabelingButton = findViewById(R.id.image_labeling_button);
        Button textRecognizerButton = findViewById(R.id.text_recognizer_button);
        Button faceDetectionButton = findViewById(R.id.face_detection_button);
        Button faceDetectionWithCameraButton = findViewById(R.id.face_detection_with_camera_button);
        Button translateButton = findViewById(R.id.translate_button);
        Button languageRecognitionButton = findViewById(R.id.language_recognition_button);
        imageLabelingButton.setOnClickListener(new MyButtonClick());
        textRecognizerButton.setOnClickListener(new MyButtonClick());
        faceDetectionButton.setOnClickListener(new MyButtonClick());
        faceDetectionWithCameraButton.setOnClickListener(new MyButtonClick());
        translateButton.setOnClickListener(new MyButtonClick());
        languageRecognitionButton.setOnClickListener(new MyButtonClick());

    }

    private class MyButtonClick implements View.OnClickListener{
        public void onClick(View view){
            switch (view.getId()){
                case R.id.image_labeling_button:
                    startActivity(new Intent(HomeActivity.this, ImageLabelingActivity.class));
                    break;
                case R.id.text_recognizer_button:
                    startActivity(new Intent(HomeActivity.this, TextRecognizerActivity.class));
                    break;
                case R.id.face_detection_button:
                    startActivity(new Intent(HomeActivity.this, FaceDetectionActivity.class));
                    break;
                case R.id.face_detection_with_camera_button:
                    startActivity(new Intent(HomeActivity.this, FaceDetectionWithCameraActivity.class));
                case R.id.translate_button:
                    startActivity(new Intent(HomeActivity.this, TranslateActivity.class));
                    break;
                case R.id.language_recognition_button:
                    startActivity(new Intent(HomeActivity.this, LanguageRecognitionActivity.class));
                    break;

            }

        }
    }
}