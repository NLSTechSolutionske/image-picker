package com.nlstechsolutions.imagepicker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nlstechsolutions.libraries.image_picker.ImagePicker;

public class MainTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);


        new ImagePicker.Builder(this).cropType(ImagePicker.CropType.FREE).resultUri((uri, file) -> {

            return null;
        }).show();
    }
}