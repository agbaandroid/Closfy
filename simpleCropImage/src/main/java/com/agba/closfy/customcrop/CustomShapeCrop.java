package com.agba.closfy.customcrop;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class CustomShapeCrop extends Activity {

    private static final String TAG = "CropImage";

    final int IMAGE_MAX_SIZE = 1024;
    public static final String IMAGE_PATH = "image-path";
    public static final String SCALE = "scale";
    public static final String ROTATION_IN_DEGREES = "rotation_in_degrees";
    public static final String ASPECT_X = "aspectX";
    public static final String ASPECT_Y = "aspectY";
    public static final String OUTPUT_X = "outputX";
    public static final String OUTPUT_Y = "outputY";
    public static final String SCALE_UP_IF_NEEDED = "scaleUpIfNeeded";

    public static final String ACTION_INLINE_DATA = "inline-data";
    public static final String RETURN_DATA_AS_BITMAP = "data";

    public static final int RESULT_ERROR = 99;

    private ContentResolver mContentResolver;
    private DrawingView mImageView;
    Bitmap mBitmap;

    LinearLayout pestanaNormal;

    Float mRotation;
    int mOutputX;
    int mOutputY;
    boolean mScale;
    boolean mScaleUp;
    String mImagePath;
    int mAspectX;
    int mAspectY;

    TextView botonGuardar;
    TextView botonCancelar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_shape_crop);

        mImageView = (DrawingView) findViewById(R.id.image);
        pestanaNormal = (LinearLayout) findViewById(R.id.pestanaNormal);
        botonGuardar = (TextView) findViewById(R.id.save);
        botonCancelar = (TextView) findViewById(R.id.cancel);
        mContentResolver = getContentResolver();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            //mBitmap = extras.getParcelable("imagen");
            mImagePath = extras.getString(IMAGE_PATH);
            mRotation = extras.getFloat(ROTATION_IN_DEGREES);
            mAspectX = extras.getInt(ASPECT_X);
            mAspectY = extras.getInt(ASPECT_Y);

            mOutputX = extras.getInt(OUTPUT_X);
            mOutputY = extras.getInt(OUTPUT_Y);
            mScale = extras.getBoolean(SCALE, true);
            mScaleUp = extras.getBoolean(SCALE_UP_IF_NEEDED, true);

            try {
                mBitmap = getBitmap(mImagePath);

                //mBitmap = Bitmap.createScaledBitmap(mBitmap, 0, 0, false);

                mImageView.bitmap = mBitmap;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                setResult(RESULT_ERROR);
            }
        }

        //mImageView.setImageBitmapResetBase(mBitmap, true);
        // Make UI fullscreen.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        pestanaNormal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bitmap bitmap = mImageView.recortarImagen();

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

                // you can create a new file name "test.jpg" in sdcard
                // folder.
                String idFoto = "customCrop_" + String.valueOf(System.currentTimeMillis())
                        + ".png";

                File dbFile = new File(
                        Environment.getExternalStorageDirectory(),
                        "/Closfy/Tmp");

                File file = new File(dbFile, idFoto);
                try {
                    file.createNewFile();
                    // write the bytes in file
                    FileOutputStream fo = new FileOutputStream(file);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Bundle extras = new Bundle();
                extras.putBoolean("isCustom", true);
                extras.putString("idFoto", idFoto);
                Intent returnIntent = new Intent();
                returnIntent.putExtras(extras);
                setResult(Activity.RESULT_OK, returnIntent);

                finish();
            }
        });

        botonCancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putBoolean("isCancel", true);
                Intent returnIntent = new Intent();
                returnIntent.putExtras(extras);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private Uri getImageUri(String path) {

        return Uri.fromFile(new File(path));
    }

    private Bitmap getBitmap(String path) throws Exception {

        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = mContentResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(
                        2,
                        (int) Math.round(Math.log(IMAGE_MAX_SIZE
                                / (double) Math.max(o.outHeight, o.outWidth))
                                / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file " + path + " not found");
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "file " + path + " not found");
            throw e;
        }
    }

}
