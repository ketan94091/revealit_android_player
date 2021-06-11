package com.Revealit.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.DotsLocationsModel;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

import java.util.List;

public class ARviewActivity extends AppCompatActivity {


    private FrameLayout frameOverlay;
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_test);

        Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
        Uri intentUri = null;
        intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.1").buildUpon()

                .appendQueryParameter("file", "https://apac.sgp1.cdn.digitaloceanspaces.com/ar_models/1/KitcheAid_Blender_cfd009624c77d60978e93715776a6d5b.glb")
                //.appendQueryParameter("file", getIntent().getStringExtra(Constants.AR_VIEW_URL))
                .appendQueryParameter("mode", "ar_only")
                .appendQueryParameter("link ",getIntent().getStringExtra(Constants.AR_VIEW_MODEL_URL))
                .appendQueryParameter("title ", getIntent().getStringExtra(Constants.AR_VIEW_MODEL_NAME))
                .build();
        sceneViewerIntent.setData(intentUri);
        sceneViewerIntent.setPackage("com.google.ar.core");
        startActivity(sceneViewerIntent);
        finish();

    }

}
