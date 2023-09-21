package com.Revealit.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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
import com.Revealit.Adapter.SavedListNameAdapter;
import com.Revealit.Adapter.ViewPagerProductImagesAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.GetProductDetailsModel;
import com.Revealit.ModelClasses.SavedProductListModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.UserOnboardingProcess.NewAuthGetStartedActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ProductBuyingScreenActivity extends AppCompatActivity {


    private Activity mActivity;
    private Context mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ProgressBar progressLoadData;
    private ViewPagerProductImagesAdapter mViewPagerProductImagesAdapter;
    private int REQUEST_PERMISSION = 100;
    private int dotsCount;
    private ImageView[] dots;
    String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private Bitmap savedBitMap;
    private GetProductDetailsModel.Data mProductData;
    private ImageView imgHeaderViewDialogView;
    private RelativeLayout relatvieHeaderImage;
    private String strItemId;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_product_buying_screen);

        setIds();

    }


    private void setIds() {

        mActivity = ProductBuyingScreenActivity.this;
        mContext = ProductBuyingScreenActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();
        
         imgHeaderViewDialogView = (ImageView)findViewById(R.id.imgHeaderView);
        relatvieHeaderImage = (RelativeLayout)findViewById(R.id.relatvieHeaderImage);


        strItemId =getIntent().getStringExtra("ITEM_ID");

        openProductPurchaseDialog(strItemId);

    }

    private void openProductPurchaseDialog(String itemId) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
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

               // mAlertDialog.dismiss();
                finish();
            }
        });


    }

    private void callGetProductData(View dialogView, String itemId, AlertDialog mAlertDialog) {


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

    private void updateProductDetailsUI(View dialogView, final GetProductDetailsModel.Data data, AlertDialog mAlertDialog) {



        //LOAD HEADER IMAGE
        
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
                mIntent.putExtra(Constants.RESEARCH_URL, "" + url);
                mIntent.putExtra(Constants.RESEARCH_URL_SPONSER, "" + name);
                startActivity(mIntent);
            }
        });


        //SET SPONSOR LOGO
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new RoundedCorners(25));
        ImageView imgSponsorLogoDialogView = (ImageView) dialogView.findViewById(R.id.imgSponsorLogo);
        Glide.with(mActivity)
                .load(data.getOffers().getData().get(0).getVendorLogoUrl()).apply(requestOptions)
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
        mViewPagerProductImagesAdapter = new ViewPagerProductImagesAdapter(ProductBuyingScreenActivity.this, data.getImages().getData());
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

               // mAlertDialog.dismiss();
                finish();
            }
        });

        //SAVED ITEM LIST
        LinearLayout linearSaved = (LinearLayout) dialogView.findViewById(R.id.linearSaved);
        linearSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              CommonMethods.displayToast(mContext,"SAVED");
            }
        });

        //SHARE VIEW DIALOG
        LinearLayout linearShareDialogView = (LinearLayout) dialogView.findViewById(R.id.linearShare);
        linearShareDialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mSessionManager.getPreferenceBoolean(Constants.READ_WRITE_PERMISSION)) {

//                    //SAVE IT IN LOCAL STORAGE AND THEN SHARE
//                    imgHeaderViewDialogView.invalidate();
//                    BitmapDrawable drawable = (BitmapDrawable) imgHeaderViewDialogView.getDrawable();
//                    savedBitMap = drawable.getBitmap();

                    savedBitMap = Bitmap.createBitmap(relatvieHeaderImage.getWidth(), relatvieHeaderImage.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(savedBitMap);
                    relatvieHeaderImage.draw(canvas);

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


        //AR VIEW ICON HIDE IF NO AR MODEL AVAILABLE
        if (data.getGlb_model_url() == null) {
            linearImgARviewDialogView.setVisibility(View.INVISIBLE);
        }

        linearImgARviewDialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //IF DEVICE SUPPORT AR VIEW
                //IF APP ENVIRONMENT IS T-CURATOR(WHICH SUPPORT MULTIPLE GLB)
//                if (CommonMethods.isDeviceSupportAR(mActivity) && mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID) == 2) {
//                    //OPEN AR VIEW
//                    Intent mARviewIntent = new Intent(ProductBuyingScreenActivity.this, ArModelViewerWeb.class);
//                    mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_NAME, data.getProduct_name());
//                    mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_URL, data.getVendor_url());
//                    mARviewIntent.putExtra(Constants.AR_MODEL_ID, ""+data.getItem_id());
//                    startActivity(mARviewIntent);
//                }else{
//                    //IF OTHER ENVIRONMENT OTHER THAN T CURATOR SHOULD OPEN DIRECTLY IN TO AR VIEW
//                    if(CommonMethods.isDeviceSupportAR(mActivity)) {
//                        Intent mARviewIntent = new Intent(mActivity, ARviewActivity.class);
//                        mARviewIntent.putExtra(Constants.AR_VIEW_URL,data.getGlb_model_url());
//                        mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_NAME, data.getProduct_name());
//                        mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_URL,  data.getVendor_url());
//                        startActivity(mARviewIntent);
//                    }else{
//                        CommonMethods.displayToast(mContext ,"Device not support AR camera");
//                    }
//                }
                //IF OTHER ENVIRONMENT OTHER THAN T CURATOR SHOULD OPEN DIRECTLY IN TO AR VIEW
                if(CommonMethods.isDeviceSupportAR(mActivity)) {
                    Intent mARviewIntent = new Intent(mActivity, ARviewActivity.class);
                    mARviewIntent.putExtra(Constants.AR_VIEW_URL,data.getGlb_model_url());
                    mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_NAME, data.getProduct_name());
                    mARviewIntent.putExtra(Constants.AR_VIEW_MODEL_URL,  data.getVendor_url());
                    startActivity(mARviewIntent);
                }else{
                    CommonMethods.displayToast(mContext ,"Device not support AR camera");
                }

            }
        });




        RelativeLayout relativeContentDialogView = (RelativeLayout) dialogView.findViewById(R.id.relativeContent);
        relativeContentDialogView.setVisibility(View.VISIBLE);


        //SET PRODUCT FEATURES AND TECHNICAL SPECIFICATIONS
        TextView txtProductDescript = (TextView)dialogView.findViewById(R.id.txtProductDescript);
        TextView txtProductFeatures = (TextView)dialogView.findViewById(R.id.txtProductFeatures);
        TextView txtProductTechnical = (TextView)dialogView.findViewById(R.id.txtProductTechnical);
        TextView txtProductDescriptionTitle = (TextView)dialogView.findViewById(R.id.txtProductDescriptionTitle);
        TextView txtProductFeaturesTitle = (TextView)dialogView.findViewById(R.id.txtProductFeaturesTitle);
        TextView txtProductTechnicalTitle = (TextView)dialogView.findViewById(R.id.txtProductTechnicalTitle);

        txtProductDescript.setText(data.getLong_desc());
        txtProductFeatures.setText(data.getFeatures());
        txtProductTechnical.setText(data.getManufacturer_product_id());

        //LINEAR DESCRIPTION
        LinearLayout linearDescription = (LinearLayout) dialogView.findViewById(R.id.linearProductDescription);
        linearDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtProductDescript.getVisibility() == View.VISIBLE){
                    txtProductDescript.setVisibility(View.GONE);
                }else{
                    txtProductDescript.setVisibility(View.VISIBLE);
                }
            }
        });


        //LINEAR PRODUCT FEATURES
        LinearLayout linearProductFeatures = (LinearLayout) dialogView.findViewById(R.id.linearProductFeatures);
        linearProductFeatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(txtProductFeatures.getVisibility() == View.VISIBLE){
                    txtProductFeatures.setVisibility(View.GONE);
                }else{
                    txtProductFeatures.setVisibility(View.VISIBLE);
                }
            }
        });


        //LINEAR TECHNICAL SPECIFICATIONS
        LinearLayout linearProductTechnical = (LinearLayout) dialogView.findViewById(R.id.linearProductTechnical);
        linearProductTechnical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(txtProductTechnical.getVisibility() == View.VISIBLE){
                    txtProductTechnical.setVisibility(View.GONE);
                }else{
                    txtProductTechnical.setVisibility(View.VISIBLE);
                }
            }
        });

        //LINEAR TECHNICAL SPECIFICATIONS
        LinearLayout linearFavorite = (LinearLayout) dialogView.findViewById(R.id.linearFavorite);
        linearFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openBottomBarForSavedList();
            }
        });



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

        if (ContextCompat.checkSelfPermission(ProductBuyingScreenActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProductBuyingScreenActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

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
                            ActivityCompat.requestPermissions(ProductBuyingScreenActivity.this, PERMISSIONS, REQUEST_PERMISSION);
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

        //DELETE OLD FILES
        //String root = Environment.getExternalStorageDirectory().toString();
        String root = mActivity.getApplicationContext().getFilesDir().getPath();
        File deletOldFiles = new File(root, mSessionManager.getPreference(Constants.SAVED_IMAGE_FILE_NAME));
        if (deletOldFiles.exists()) deletOldFiles.delete();

        //SHARE IMAGE FILE NAME IN SESSION MANAGER SO WE CAN USE IT FURTHER FOR SOCIAL MEDIA SHARING
        mSessionManager.updatePreferenceString(Constants.SAVED_IMAGE_FILE_NAME, "" + System.currentTimeMillis() + ".png");


        File file = new File(root, mSessionManager.getPreference(Constants.SAVED_IMAGE_FILE_NAME));
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            savedBitMap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayPopupWindow(View anchorView) {

        PopupWindow popupShareSocialMedia = new PopupWindow(ProductBuyingScreenActivity.this);
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
                    ShareDialog dialog = new ShareDialog(ProductBuyingScreenActivity.this);
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

                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        //File media = new File(mActivity.getApplicationContext().getFilesDir().getPath() + "/" + mSessionManager.getPreference(Constants.SAVED_IMAGE_FILE_NAME));
                        intent.putExtra(Intent.EXTRA_STREAM, getImageUri(getApplicationContext(),savedBitMap));
                        intent.setPackage("com.twitter.android");
                        startActivity(intent);
                    }catch (Exception e) {
                        CommonMethods.printLogE("TAG" ,"exeception : "+ e);
                    }


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

                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        //File media = new File(mActivity.getApplicationContext().getFilesDir().getPath() + "/" + mSessionManager.getPreference(Constants.SAVED_IMAGE_FILE_NAME));
                        intent.putExtra(Intent.EXTRA_STREAM, getImageUri(getApplicationContext(),savedBitMap));
                        intent.setPackage("com.instagram.android");
                        startActivity(intent);
                    }catch (Exception e) {
                        CommonMethods.printLogE("TAG" ,"exeception : "+ e);
                    }

                } else {
                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strInstagramNotInstalled));
                }

                //CLOSE POPUP WINDOW
                popupShareSocialMedia.dismiss();
            }
        });
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, UUID.randomUUID().toString() + ".png", "drawing");
        return Uri.parse(path);
    }

    private void openBottomBarForSavedList() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_user_saved_product_list);


        LinearLayout imgBackArrow =(LinearLayout) bottomSheetDialog.findViewById(R.id.imgBackArrow);

        //OPEN USER DEFAULT LIST
        openUserProductList(bottomSheetDialog );


        imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.cancel();


            }
        });
        bottomSheetDialog.show();
    }



    private void openUserProductList(BottomSheetDialog bottomSheetDialog) {

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

        Call<SavedProductListModel> call = patchService1.getUserSavedList(Constants.API_GET_USER_SAVED_LISTS);

        call.enqueue(new Callback<SavedProductListModel>() {
            @Override
            public void onResponse(Call<SavedProductListModel> call, retrofit2.Response<SavedProductListModel> response) {


                CommonMethods.printLogE("Response @ openUserProductList : ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ openUserProductList : ", "" + response.code());

                if (response.code() == Constants.API_CODE_200) {

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CommonMethods.printLogE("Response @ openUserProductList : ", "" + gson.toJson(response.body()));

                    //UPDATE UI
                    displaySavedProductList(response.body(), bottomSheetDialog);



                } else {
                    progressLoadData.setVisibility(View.GONE);

                    CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

                }

            }

            @Override
            public void onFailure(Call<SavedProductListModel> call, Throwable t) {

                progressLoadData.setVisibility(View.GONE);

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strSomethingWentWrong));

            }
        });
    }

    private void displaySavedProductList(SavedProductListModel body, BottomSheetDialog bottomSheetDialog) {

        RecyclerView recycleAccountList = (RecyclerView) bottomSheetDialog.findViewById(R.id.recycleSavedProductList);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        recycleAccountList.setLayoutManager(mLinearLayoutManager);

        ImageView imgCreateList = (ImageView) bottomSheetDialog.findViewById(R.id.imgCreateList);
        TextView txtCreateNewList = (TextView) bottomSheetDialog.findViewById(R.id.txtCreateNewList);

        if(body.getData().size() == 0){
            txtCreateNewList.setVisibility(View.VISIBLE);
            recycleAccountList.setVisibility(View.GONE);
        }else{

            //BIND RECYCLER VIEW
            SavedListNameAdapter mSavedListNameAdapter = new SavedListNameAdapter(mContext,ProductBuyingScreenActivity.this, body.getData(),recycleAccountList,strItemId, mSessionManager,bottomSheetDialog);
            recycleAccountList.setAdapter(mSavedListNameAdapter);


        }

        imgCreateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //CLOSE BOTTOM CREATED LIST BOTTOM BAR
                bottomSheetDialog.cancel();

                //OPEN ANOTHER DIALOGUE FROM BOTTOM BAR FOR CREATE NEW LIST
                openCreateListName();
            }
        });
    }

    private void openCreateListName() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_create_new_list);
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);


        EditText edtCreateNewList =(EditText) bottomSheetDialog.findViewById(R.id.edtCreateNewList);
        TextView txtCreateListEnabled =(TextView) bottomSheetDialog.findViewById(R.id.txtCreateListEnabled);
        TextView txtCreateListDisabled =(TextView) bottomSheetDialog.findViewById(R.id.txtCreateListDisabled);
        LinearLayout imgBackArrow = (LinearLayout) bottomSheetDialog.findViewById(R.id.imgBackArrow);

        edtCreateNewList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.length() != 0){
                    txtCreateListDisabled.setVisibility(View.GONE);
                    txtCreateListEnabled.setVisibility(View.VISIBLE);
                }else{
                    txtCreateListDisabled.setVisibility(View.VISIBLE);
                    txtCreateListEnabled.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        txtCreateListEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               callApiCreateNewlist(edtCreateNewList.getText().toString(),bottomSheetDialog);


            }
        });
        imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              bottomSheetDialog.cancel();


            }
        });
        bottomSheetDialog.show();
    }

    private void callApiCreateNewlist(String strListName, BottomSheetDialog bottomSheetDialog) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request original = chain.request();

                okhttp3.Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", mSessionManager.getPreference(Constants.AUTH_TOKEN_TYPE) + " " + mSessionManager.getPreference(Constants.AUTH_TOKEN))
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mSessionManager.getPreference(Constants.API_END_POINTS_MOBILE_KEY))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.newBuilder().connectTimeout(30000, TimeUnit.SECONDS).readTimeout(30000, TimeUnit.SECONDS).writeTimeout(30000, TimeUnit.SECONDS).build())
                .build();

        UpdateAllAPI patchService1 = retrofit.create(UpdateAllAPI.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("name", strListName);


        Call<JsonElement> call = patchService1.createProductList(paramObject);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ callApiCreateNewlist: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callApiCreateNewlist: ", "" + response.code());
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ callApiCreateNewlist: ", "" + gson.toJson(response.body()));

                if (response.code() == 200  ) {

                    //CANCEL BOTTOM BAR AND SAVE ITEM TO LITS
                    bottomSheetDialog.cancel();




                }else{

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        CommonMethods.buildDialog(mContext,""+jObjError.getString("message"));

                    } catch (Exception e) {

                        CommonMethods.buildDialog(mContext,""+e.getMessage());
                    }


                }


            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                CommonMethods.buildDialog(mContext, getResources().getString(R.string.strApiCallFailure));


            }
        });

    }

}
