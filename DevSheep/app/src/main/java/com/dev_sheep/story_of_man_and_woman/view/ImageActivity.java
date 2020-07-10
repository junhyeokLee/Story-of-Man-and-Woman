package com.dev_sheep.story_of_man_and_woman.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dev_sheep.story_of_man_and_woman.R;

public class ImageActivity extends AppCompatActivity {

    ImageView imageView;
   private Bitmap image;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ef_activity_image_picker);

        imageView = findViewById(R.id.id_Profile_Image);

        Intent intent = getIntent();
        byte[] arr = getIntent().getByteArrayExtra("image");
        image = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        ImageView BigImage = (ImageView)findViewById(R.id.id_Profile_Image);
        BigImage.setImageBitmap(image);


    }
}
