package com.nisith.androidvisionproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class ImageLabelingActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView resultTextView;
    private static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_labeling);
        imageView = findViewById(R.id.image_view);
        resultTextView = findViewById(R.id.result_text_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri selectedImageUri = data.getData();
            Picasso.get().load(selectedImageUri).fit().centerCrop().into(imageView);
            performImageLabelingWithMLKit(selectedImageUri);
        }
    }

    private void performImageLabelingWithMLKit(Uri selectedImageUri) {
        FirebaseVisionImage visionImage;
        try {
            visionImage = FirebaseVisionImage.fromFilePath(getApplicationContext(), selectedImageUri);
            FirebaseVisionImageLabeler visionImageLabeler = FirebaseVision.getInstance()
                    .getOnDeviceImageLabeler();
            visionImageLabeler.processImage(visionImage)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                            String result="";
                            for (FirebaseVisionImageLabel imageLabel : firebaseVisionImageLabels){
                                String text = imageLabel.getText();
                                float confidence = imageLabel.getConfidence();
                                String entityId = imageLabel.getEntityId();
                                result =result + "Name: "+text + "  confidence= "+confidence+ "  entity_id= "+entityId+" \n";
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


        } catch (IOException error){
            error.printStackTrace();
        }


    }
}
















