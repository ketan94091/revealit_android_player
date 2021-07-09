package com.Revealit.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.Constants;
import com.Revealit.R;

public class ARviewActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_test);

        Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
        Uri intentUri = null;
        intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.1").buildUpon()
                .appendQueryParameter("file", getIntent().getStringExtra(Constants.AR_VIEW_URL))
                .appendQueryParameter("mode", "ar_only")
                //.appendQueryParameter("link",""+getIntent().getStringExtra(Constants.AR_VIEW_MODEL_URL))
                //.appendQueryParameter("title", ""+getIntent().getStringExtra(Constants.AR_VIEW_MODEL_NAME))
                .build();
        sceneViewerIntent.setData(intentUri);
        //sceneViewerIntent.setPackage("com.google.ar.core");
        sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
        startActivity(sceneViewerIntent);
        finish();


    }

}
