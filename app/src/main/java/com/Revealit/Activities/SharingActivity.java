package com.Revealit.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.R;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;


public class SharingActivity extends AppCompatActivity {

    private String strType = "";
    private ImageView imgTest;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        imgTest = (ImageView)findViewById(R.id.imgTest);

        Intent mIntent = getIntent();
        if (mIntent != null) {

            strType = mIntent.getStringExtra("TYPE");

            buildDialog(this,"");


        }

    }

    public void buildDialog(Context mContext, String strMessege) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.app_name));
        builder.setMessage(strMessege);
        builder.setNegativeButton(mContext.getResources().getString(R.string.strOk), null);
        AlertDialog dialog = builder.create();


        String qrPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/share.jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap mustOpen = BitmapFactory.decodeFile(qrPath, options);

        // imgTest.setImageBitmap(mustOpen);


        SharePhoto photo = new SharePhoto.Builder().setBitmap(mustOpen).build();
        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
        ShareDialog dialog11 = new ShareDialog(this);
        if (dialog11.canShow(SharePhotoContent.class)) {
            dialog11.show(content);

        }

        dialog.show();

        //this.finish();
    }



}
