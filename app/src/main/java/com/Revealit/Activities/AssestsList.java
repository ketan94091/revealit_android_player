package com.Revealit.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.R;

import java.net.URL;


public class AssestsList extends AppCompatActivity implements View.OnClickListener {


    private URL myURL;
    private Button btnButcherBlock,btnblender,btnbowl,btnMixer;
    private ImageView imgBackArrow;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assestes_list);



    }

    @Override
    protected void onResume() {
        super.onResume();
        setID();
        setOnclicks();
    }

    private void setID() {


        btnMixer = (Button) findViewById(R.id.btnMixer);
        btnbowl = (Button) findViewById(R.id.btnbowl);
        btnblender = (Button) findViewById(R.id.btnblender);
        btnButcherBlock = (Button) findViewById(R.id.btnButcherBlock);
        imgBackArrow = (ImageView) findViewById(R.id.imgBackArrow);


    }

    private void setOnclicks() {

        btnMixer.setOnClickListener(this);
        btnbowl.setOnClickListener(this);
        btnblender.setOnClickListener(this);
        btnButcherBlock.setOnClickListener(this);
        imgBackArrow.setOnClickListener(this);

    }


    @Override
    public void onClick(View mView) {


        switch (mView.getId()){

            case R.id.btnMixer:

                Intent mIntent = new Intent(this, TestActivity.class);
                mIntent.putExtra("URL", "https://apac.sgp1.cdn.digitaloceanspaces.com/ar_models/1/2a.KitchenAid_StandMixer_CARED_680569095664_4a1b7a00a5bb0a1baf460475c5d335df.glb");
                startActivity(mIntent);

                break;

            case R.id.btnbowl:

                Intent mIntentOne = new Intent(this, TestActivity.class);
                mIntentOne.putExtra("URL", "https://apac.sgp1.cdn.digitaloceanspaces.com/ar_models/1/7a.Blue%20Bowl_Small_fb30838353a3ff1c5de00aebcc8c1e54.glb");
                startActivity(mIntentOne);

                break;

            case R.id.btnblender:

                Intent mIntentTwo = new Intent(this, TestActivity.class);
                mIntentTwo.putExtra("URL", "https://apac.sgp1.cdn.digitaloceanspaces.com/ar_models/1/KitcheAid_Blender_cfd009624c77d60978e93715776a6d5b.glb");
                startActivity(mIntentTwo);

                break;
            case R.id.btnButcherBlock:

                Intent mIntentThree = new Intent(this, TestActivity.class);
                mIntentThree.putExtra("URL", "https://apac.sgp1.cdn.digitaloceanspaces.com/ar_models/1/1a.JohnBoos_ButcherBlock3_Black_662969282615_033a5d8be38bc65ace32de2ce21a333f.glb");
                startActivity(mIntentThree);

                break;

            case R.id.imgBackArrow:

                finish();
                break;
        }

    }
}
