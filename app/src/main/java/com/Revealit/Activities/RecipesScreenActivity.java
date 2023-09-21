package com.Revealit.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.Revealit.Adapter.ProductPurchaseVendorListAdapter;
import com.Revealit.Adapter.ViewPagerProductImagesAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.GetProductDetailsModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.UserOnboardingProcess.NewAuthGetStartedActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RecipesScreenActivity extends AppCompatActivity {


    private FrameLayout frameOverlay;
    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ProgressBar progressLoadData;
    private ViewPagerProductImagesAdapter mViewPagerProductImagesAdapter;
    private int REQUEST_PERMISSION = 100;
    private int dotsCount;
    private ImageView[] dots;
    private String shareImageFileName = "RevealitShareImage.jpg";
    String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private Bitmap savedBitMap;
    private GetProductDetailsModel.Data mProductData;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_recipes_screen);

        setIds();

    }


    private void setIds() {

        mActivity = RecipesScreenActivity.this;
        mContext = RecipesScreenActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        openProductPurchaseDialog(getIntent().getStringExtra("ITEM_ID"));

    }

    private void openProductPurchaseDialog(String itemId) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RecipesScreenActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_product_purchase_new, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog mAlertDialog = dialogBuilder.create();
        mAlertDialog.setCancelable(true);

        progressLoadData = (ProgressBar) dialogView.findViewById(R.id.progressLoadData);

        //GET PRODUCT DETAILS
        callGetProductData(dialogView, itemId, mAlertDialog);

        mAlertDialog.show();

        mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                mAlertDialog.dismiss();
                finish();
            }
        });



    }

    private void callGetProductData(View dialogView, String itemId, AlertDialog mAlertDialog) {

        CommonMethods.printLogE("Response @ callGetProductData ITEM ID : ", "" + itemId);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                okhttp3.Request requestOriginal = chain.request();

                okhttp3.Request request = requestOriginal.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + " " + mSessionManager.getPreference(Constants.AUTH_TOKEN))
                        .method(requestOriginal.method(), requestOriginal.body())
                        .build();


                return chain.proceed(request);
            }
        });
        final OkHttpClient httpClient1 = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .client(httpClient1.newBuilder().connectTimeout(10, TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).build())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient1)
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);

        Call<GetProductDetailsModel> call = patchService1.getProductDetails(Constants.API_GET_PRODUCT_DETAILS + "" + itemId);

        call.enqueue(new Callback<GetProductDetailsModel>() {
            @Override
            public void onResponse(Call<GetProductDetailsModel> call, retrofit2.Response<GetProductDetailsModel> response) {


                CommonMethods.printLogE("Response @ callGetProductData : ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callGetProductData : ", "" + response.code());

                if (response.code() == Constants.API_CODE_200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ callGetProductData : ", "" + gson.toJson(response.body()));


                    //SAVE DATA
                     mProductData = response.body().getData();


                    //UPDATE UI
                    updateProductDetailsUI(dialogView, response.body().getData(), mAlertDialog);


                } else if (response.code() == Constants.API_CODE_401) {

                    progressLoadData.setVisibility(View.GONE);
                    mAlertDialog.dismiss();

                    Intent mLoginIntent = new Intent(mActivity, NewAuthGetStartedActivity.class);
                    mActivity.startActivity(mLoginIntent);
                    mActivity.finish();

                } else {
                    progressLoadData.setVisibility(View.GONE);
                    mAlertDialog.dismiss();

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }

            }

            @Override
            public void onFailure(Call<GetProductDetailsModel> call, Throwable t) {

                progressLoadData.setVisibility(View.GONE);
                mAlertDialog.dismiss();

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

            }
        });


    }

    private void updateProductDetailsUI(View dialogView,final GetProductDetailsModel.Data data, AlertDialog mAlertDialog) {

        //LOAD HEADER IMAGE
        ImageView imgHeaderViewDialogView = (ImageView) dialogView.findViewById(R.id.imgHeaderView);
        Glide.with(mActivity)
                .load(data.getHeader())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                }).into(imgHeaderViewDialogView);


        //SET PRODUCT NAME
        TextView txtProductNameDialogView = (TextView) dialogView.findViewById(R.id.txtProductName);
        txtProductNameDialogView.setText(data.getProduct_name());


        //SET DISCOUNT
        TextView txtDiscountDialogView = (TextView) dialogView.findViewById(R.id.txtDiscount);
        txtDiscountDialogView.setText(data.getOffers().getData().get(0).getOfferText());


        //CLICK ON PURCHASE TEXT FOR BUY THIS PRODUCT
        String url = data.getOffers().getData().get(0).getVendorUrl();
        String name = data.getOffers().getData().get(0).getVendorName();
        TextView txtPurchaseDialogView = (TextView) dialogView.findViewById(R.id.txtPurchase);
        txtPurchaseDialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(mActivity, WebViewScreen.class);
                mIntent.putExtra(Constants.RESEARCH_URL, "" +url);
                mIntent.putExtra(Constants.RESEARCH_URL_SPONSER, "" +name);
                startActivity(mIntent);
            }
        });


        //SET SPONSOR LOGO
        ImageView imgSponsorLogoDialogView = (ImageView) dialogView.findViewById(R.id.imgSponsorLogo);
        Glide.with(mActivity)
                .load(data.getOffers().getData().get(0).getVendorLogoUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                }).into(imgSponsorLogoDialogView);


        //SET PRODUCT IMAGES IN VIEW PAGER WITH INDICATOR
        ViewPager viewPager = (ViewPager) dialogView.findViewById(R.id.viewPager);
        LinearLayout viewPagerCountDots = (LinearLayout) dialogView.findViewById(R.id.viewPagerCountDots);

        //SET VIEW PAGER ADAPTER
        mViewPagerProductImagesAdapter = new ViewPagerProductImagesAdapter(RecipesScreenActivity.this, data.getImages().getData());
        viewPager.setAdapter(mViewPagerProductImagesAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
                }

                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setUiPageViewController(viewPagerCountDots);

        //SET OFFERS LIST
        RecyclerView recycleVenderListDialogView = (RecyclerView) dialogView.findViewById(R.id.recycleVenderList);
        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(mActivity);
        recycleVenderListDialogView.setLayoutManager(recylerViewLayoutManager);

        //SET CATEGORY LIST
        ProductPurchaseVendorListAdapter mProductPurchaseVendorListAdapter = new ProductPurchaseVendorListAdapter(mContext, mActivity, mProductData.getOffers().getData());
        recycleVenderListDialogView.setAdapter(mProductPurchaseVendorListAdapter);

        //DISMISS ALERT DIALOG
        LinearLayout linearImgCancelDialogView = (LinearLayout) dialogView.findViewById(R.id.linearImgCancel);
        linearImgCancelDialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();
                finish();
            }
        });

        //SHARE VIEW DIALOG
        LinearLayout linearShareDialogView = (LinearLayout) dialogView.findViewById(R.id.linearShare);
        linearShareDialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mSessionManager.getPreferenceBoolean(Constants.READ_WRITE_PERMISSION)) {

                    //SAVE IT IN LOCAL STORAGE AND THEN SHARE
                    imgHeaderViewDialogView.invalidate();
                    BitmapDrawable drawable = (BitmapDrawable) imgHeaderViewDialogView.getDrawable();
                    savedBitMap = drawable.getBitmap();

                    //SAVE IMAGE
                    storeImage(savedBitMap);

                    //OPEN ANCHOR VIEW
                    displayPopupWindow(linearShareDialogView);

                } else {

                    readWriteExternalStoragePermission();
                }
            }
        });



        //CLICK ON PURCHASE TEXT FOR BUY THIS PRODUCT
        LinearLayout linearImgARviewDialogView = (LinearLayout) dialogView.findViewById(R.id.linearImgARview);
        linearImgARviewDialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                Uri intentUri = null;
                intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.1").buildUpon()

                        //.appendQueryParameter("file",getIntent().getStringExtra("URL"))
                        //.appendQueryParameter("file", "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf")
                        // .appendQueryParameter("file", "https://apac.sgp1.cdn.digitaloceanspaces.com/ar_models/1/2a.KitchenAid_StandMixer_CARED_680569095664_4a1b7a00a5bb0a1baf460475c5d335df.glb")
                        // .appendQueryParameter("file", "https://apac.sgp1.cdn.digitaloceanspaces.com/ar_models/1/7a.Blue%20Bowl_Small_fb30838353a3ff1c5de00aebcc8c1e54.glb")
                        .appendQueryParameter("file", "https://apac.sgp1.cdn.digitaloceanspaces.com/ar_models/1/KitcheAid_Blender_cfd009624c77d60978e93715776a6d5b.glb")
                        .appendQueryParameter("mode", "ar_only")
                        .appendQueryParameter("link ", "" + data.getVendor_url())
                        .appendQueryParameter("title ", "" + data.getProduct_name())
                        .build();
                sceneViewerIntent.setData(intentUri);
                sceneViewerIntent.setPackage("com.google.ar.core");
                startActivity(sceneViewerIntent);


            }
        });


        LinearLayout linarFavoriteDialogView = (LinearLayout) dialogView.findViewById(R.id.linarFavorite);


        RelativeLayout relativeContentDialogView = (RelativeLayout) dialogView.findViewById(R.id.relativeContent);
        relativeContentDialogView.setVisibility(View.VISIBLE);

        //HIDE PROGRESS AFTER SETTING ALL DATA
        progressLoadData.setVisibility(View.GONE);


    }
    private void setUiPageViewController(LinearLayout viewPagerCountDots) {

        dotsCount = mViewPagerProductImagesAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            viewPagerCountDots.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    private void readWriteExternalStoragePermission() {

        if (ContextCompat.checkSelfPermission(RecipesScreenActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RecipesScreenActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        } else {

            requestStoragePermission();

        }
    }

    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.strPermissionNeeded))
                    .setMessage(getResources().getString(R.string.strPermissionNeededMsg))
                    .setPositiveButton(getResources().getString(R.string.strOk), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(RecipesScreenActivity.this, PERMISSIONS, REQUEST_PERMISSION);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.strCancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mSessionManager.updatePreferenceBoolean(Constants.READ_WRITE_PERMISSION, true);
            }
        }

    }
    private void storeImage(Bitmap savedBitMap) {

        String root = Environment.getExternalStorageDirectory().toString();


        File file = new File(root, shareImageFileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            savedBitMap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void displayPopupWindow(View anchorView) {

        PopupWindow popupShareSocialMedia = new PopupWindow(RecipesScreenActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.share_anchor_view_popup_content, null);
        layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, 450);
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        layout.measure(spec, spec);
        popupShareSocialMedia.setContentView(layout);
        popupShareSocialMedia.setHeight(450);
        popupShareSocialMedia.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupShareSocialMedia.setOutsideTouchable(true);
        popupShareSocialMedia.setFocusable(true);
        popupShareSocialMedia.setBackgroundDrawable(new BitmapDrawable());
        popupShareSocialMedia.showAsDropDown(anchorView);

        TextView txtFacebook = (TextView) layout.findViewById(R.id.txtFacebook);
        TextView txtTwitter = (TextView) layout.findViewById(R.id.txtTwitter);
        TextView txtInstagram = (TextView) layout.findViewById(R.id.txtInstagram);
        txtFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CHEK IF FACEBOOK INSTALLED OR NOT
                if (CommonMethods.isAppInstalled(mContext, "com.facebook.katana")) {

                    SharePhoto photo = new SharePhoto.Builder().setBitmap(savedBitMap).build();
                    SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
                    ShareDialog dialog = new ShareDialog(RecipesScreenActivity.this);
                    if (dialog.canShow(SharePhotoContent.class)) {
                        dialog.show(content);
                    } else {
                        CommonMethods.displayToast(mContext, getResources().getString(R.string.strSomethingWentWrong));
                    }
                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strFbNotInstalled));
                }

                //CLOSE POPUP WINDOW
                popupShareSocialMedia.dismiss();

            }
        });
        txtTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonMethods.isAppInstalled(mContext, "com.twitter.android")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("image/*");

                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    File media = new File(Environment.getExternalStorageDirectory() + "/" + shareImageFileName);

                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(media));
                    intent.setPackage("com.twitter.android");
                    startActivity(intent);
                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strTwitterNotInstalled));
                }
                //CLOSE POPUP WINDOW
                popupShareSocialMedia.dismiss();

            }
        });
        txtInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonMethods.isAppInstalled(mContext, "com.instagram.android")) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");

                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    File media = new File(Environment.getExternalStorageDirectory() + "/" + shareImageFileName);

                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(media));
                    shareIntent.setPackage("com.instagram.android");
                    startActivity(shareIntent);
                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strInstagramNotInstalled));
                }

                //CLOSE POPUP WINDOW
                popupShareSocialMedia.dismiss();
            }
        });
    }


}
