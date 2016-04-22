package com.raywenderlich.memeify;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class TakePictureActivity extends Activity implements View.OnClickListener {
    private static final int TAKE_PHOTO_REQUEST_CODE = 1;
    private Boolean pictureTaken = false;


    private static final String APP_PICTURE_DIRECTORY = "/Memeify";
    private static final String MIME_TYPE_IMAGE = "image/";
    private static final String FILE_SUFFIX_JPG = ".jpg";

    private Uri selectedPhotoPath;

    private ImageView takePictureImageView;
    private TextView lookingGoodTextView;
    private Button nextScreenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        takePictureImageView = (ImageView) findViewById(R.id.picture_imageview);
        takePictureImageView.setOnClickListener(this);

        lookingGoodTextView = (TextView) findViewById(R.id.looking_good_textview);

        nextScreenButton = (Button) findViewById(R.id.enter_text_button);
        nextScreenButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.picture_imageview:
                takePictureWithCamera();
                break;

            case R.id.enter_text_button:
                break;

            default:
                break;
        }
    }

    // understand implicit intent
    private void takePictureWithCamera() {
        // create intent to capture image from camera
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // create an empty file to store image
        File photoFile = createImageFile();
        selectedPhotoPath = Uri.parse(photoFile.getAbsolutePath());

        // đặt tả đường dẫn to save image.
        captureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        // TAKE_PHOTO_REQUEST_CODE: dùng để xác định intent khi được trả về
        // Verify that the intent will resolve to an activity
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(captureIntent, TAKE_PHOTO_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // xác định intent trả về có đúng là Intent của bạn ko?
        if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            setImageViewWithImage();
        }
    }

    // scale picture cho vừa với screen (điện thoại cũng tự scale - nhưng mình tự scale sẽ tiết kiệm bộ nhớ hơn)
    private void setImageViewWithImage() {
        Bitmap pictureBitmap = BitmapResizer.ShrinkBitmap(selectedPhotoPath.toString(),
                takePictureImageView.getWidth(),
                takePictureImageView.getHeight());
        takePictureImageView.setImageBitmap(pictureBitmap);
        lookingGoodTextView.setVisibility(View.VISIBLE);
        pictureTaken = true;
    }

    private File createImageFile() {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + APP_PICTURE_DIRECTORY);
        storageDir.mkdirs();

        File imageFile = null;

        try {
            imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    FILE_SUFFIX_JPG,         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFile;
    }

    private Uri getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return Uri.parse(result);
    }


}
