package com.symoh.imageviewrotating;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button btSelect, btSave;
    private static  int IMAGE_PICK_REQUEST = 1;
    private Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.Image);
        btSelect = findViewById(R.id.Select);
        btSave = findViewById(R.id.Save);
        
        btSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChoser();
            }
        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap;
                bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"TestApp",null);
                Toast.makeText(MainActivity.this, "Image saved", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private  String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null);{

            imageView.setRotation(getCameraRotation(mImageUri));

            mImageUri = data.getData();
            imageView.setImageURI(mImageUri);
           // Picasso.get().load(mImageUri).into(imageView);
        }
    }

    private int getCameraRotation(Uri mImageUri) {
        int rotate = 0;
        try {
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(String.valueOf(mImageUri));
            }catch (IOException exe){
                exe.printStackTrace();
            }int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 90;
                    break;
                default:
                    rotate = 0;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private void openFileChoser() {
        Intent intent =  new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_PICK_REQUEST);
    }
}