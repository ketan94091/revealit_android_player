package com.Revealit.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.Revealit.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class TestActivity extends AppCompatActivity {



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        /*Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
        Uri intentUri =
                null;
        intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()

                .appendQueryParameter("file",getIntent().getStringExtra("URL"))
                //.appendQueryParameter("file", "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf")
              // .appendQueryParameter("file", "https://apac.sgp1.cdn.digitaloceanspaces.com/ar_models/1/2a.KitchenAid_StandMixer_CARED_680569095664_4a1b7a00a5bb0a1baf460475c5d335df.glb")
               // .appendQueryParameter("file", "https://apac.sgp1.cdn.digitaloceanspaces.com/ar_models/1/7a.Blue%20Bowl_Small_fb30838353a3ff1c5de00aebcc8c1e54.glb")
                //.appendQueryParameter("file", "https://apac.sgp1.cdn.digitaloceanspaces.com/ar_models/1/KitcheAid_Blender_cfd009624c77d60978e93715776a6d5b.glb")
                .appendQueryParameter("mode", "ar_only")
                .build();
        sceneViewerIntent.setData(intentUri);
        sceneViewerIntent.setPackage("com.google.ar.core");
        startActivity(sceneViewerIntent);
        finish();*/
    }



}
