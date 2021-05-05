package com.Revealit.Activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Revealit.CommonClasse.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.droidnet.DroidListener;
import com.droidnet.DroidNet;
import com.google.android.material.navigation.NavigationView;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DroidListener {

    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "MainActivity";

    public Toolbar toolbar;
    public DrawerLayout drawerLayout;
    public NavController navController;
    public NavigationView navigationView;
    private SessionManager mSessionManager;
    boolean doubleBackToExitPressedOnce = false;
    private DatabaseHelper mDatabaseHelper;
    private DroidNet mDroidNet;
    private TextView txtMobileNumber,txtUsername,txtinternetNotWorking;
    private LinearLayout linearInternetNotWorking;
    private ImageView imgUser;
    private TextView txtUserName;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavigation();

    }


    //Setting Up One Time Navigation
    private void setupNavigation() {

        mActivity = MainActivity.this;
        mContext = MainActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();


        txtinternetNotWorking = (TextView) findViewById(R.id.txtinternetNotWorking);
        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtMobileNumber = (TextView) findViewById(R.id.txtMobileNumber);

        linearInternetNotWorking = (LinearLayout) findViewById(R.id.linearInternetNotWorking);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);


        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        //Headet view text
        headerView = navigationView.getHeaderView(0);

        imgUser = (ImageView)headerView.findViewById(R.id.imgUser);
        txtUserName = (TextView)headerView.findViewById(R.id.txtUserName);

        Glide.with(mActivity)
                .load(mSessionManager.getPreference(Constants.USER_PROFILE_PICTURE))
                .circleCrop()
                .into(imgUser);

        //SET USER NAME
        txtUserName.setText(mSessionManager.getPreference(Constants.USER_FULL_NAME));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment), drawerLayout);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        menuItem.setChecked(true);

        drawerLayout.closeDrawers();

        int id = menuItem.getItemId();

        switch (id) {

            case R.id.home:
                navController.navigate(R.id.homeFragment);
                break;


        }
        return true;

    }


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }


        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        CommonMethods.displayToast(mContext, "Click back again");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 20000);
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {

        if (isConnected) {
            txtinternetNotWorking.setText(getResources().getString(R.string.strInternetAvailable));
            linearInternetNotWorking.setBackgroundColor(getResources().getColor(R.color.colorGreenDark));
        } else {
            txtinternetNotWorking.setText(getResources().getString(R.string.strNoInternetAvailable));
            linearInternetNotWorking.setBackgroundColor(getResources().getColor(R.color.colorRedDark));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Internet connection code
        mDroidNet = DroidNet.getInstance();
        mDroidNet.addInternetConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Remove Internet listner
        mDroidNet.removeInternetConnectivityChangeListener(this);
    }

}
