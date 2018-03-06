package com.example.filip.unibook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class AddProfilePictureActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView imageView;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile_picture);
        button = (Button) findViewById(R.id.btnImage);
        imageView = (ImageView) findViewById(R.id.ivProfile);

      //  button.setOnClickListener(new View.OnClickListener() {
      //      @Override
      //      public void onClick(View view) {
      //          choseImg();
      //      }
      //  });
    }

  //  public void choseImg(){
  //      Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
  //      startActivityForResult(photoPickerIntent, PICK_IMAGE);
  //  }
//
  //  @Override
  //  protected void onActivityResult(int requestCode, int resultCode, Intent data){
  //      super.onActivityResult(requestCode, resultCode, data);
//
  //      if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
  //          imageUri = data.getData();
  //          imageView.setImageURI(imageUri);
  //          Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
  //          ByteArrayOutputStream baos = new ByteArrayOutputStream();
  //          bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
  //          byte[] imageInByte = baos.toByteArray();
//
  //          DatabaseHelper db = new DatabaseHelper(this);
  //          db.uploadImg(imageInByte);
  //      }
  //  }
}
