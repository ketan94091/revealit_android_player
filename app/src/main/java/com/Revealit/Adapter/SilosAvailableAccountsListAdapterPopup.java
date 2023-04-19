package com.Revealit.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.HomeScreenTabLayout;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.KeyStoreServerInstancesModel;
import com.Revealit.R;
import com.Revealit.UserOnboardingProcess.NewAuthBiomatricAuthenticationActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class SilosAvailableAccountsListAdapterPopup extends RecyclerView.Adapter<SilosAvailableAccountsListAdapterPopup.ViewHolder> {


    private View view;
    private Context mContext;
    private HomeScreenTabLayout mActivity;
    private ViewHolder viewHolder;
    private ArrayList<KeyStoreServerInstancesModel.Data> itemListData;
    private SessionManager mSessionManager;
    private String strLoggedInUserName;


    public SilosAvailableAccountsListAdapterPopup(Context mContext, HomeScreenTabLayout mActivity, ArrayList<KeyStoreServerInstancesModel.Data> itemListData, SessionManager mSessionManager, String strLoggedInUserName) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.itemListData = itemListData;
        this.mSessionManager = mSessionManager;
        this.strLoggedInUserName =strLoggedInUserName;

    }

    public void updateListData(ArrayList<KeyStoreServerInstancesModel.Data> itemListData) {

        //CLEAR CURRENT LIST
        this.itemListData = itemListData;

        //NOTIFY LISTENER
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtAccountUsername, txtAccountName;
        private final RelativeLayout relativeMain;
        private final ImageView imgProfile;


        public ViewHolder(View mView) {

            super(mView);

            txtAccountUsername = (TextView) mView.findViewById(R.id.txtAccountUsername);
            txtAccountName = (TextView) mView.findViewById(R.id.txtAccountName);
            relativeMain = (RelativeLayout) mView.findViewById(R.id.relativeMain);
            imgProfile=(ImageView)mView.findViewById(R.id.imgProfile);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_silos_account_list_items, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if(itemListData.get(position).getUserProfile() != null){

            //SET USERNAME
            holder.txtAccountUsername.setText(itemListData.get(position).getUserProfile().getName());


            //SET USER PROFILE
            Glide.with(mContext)
                    .load("" + itemListData.get(position).getUserProfile().getProfile_image())
                    .apply(RequestOptions.circleCropTransform())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(holder.imgProfile);


        }else{
            holder.txtAccountUsername.setText(itemListData.get(position).getSubmitProfileModel().getProton().getAccountName());
        }
        holder.txtAccountName.setText("@" + itemListData.get(position).getSubmitProfileModel().getProton().getAccountName());

        if(strLoggedInUserName.equals(itemListData.get(position).getSubmitProfileModel().getProton().getAccountName())){
            holder.relativeMain.setBackground(mActivity.getResources().getDrawable(R.drawable.round_corner_selected_currency_screen));
        }

        holder.relativeMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //CHECK IF USER DATA IS NOT NULL
                //IF TRUE -> CONTINUE FETCHING DATA
                //ELSE -> DISPLAY NOT FOUND MSG
                if (itemListData.get(position) != null) {
                    mSessionManager.updatePreferenceString(Constants.KEY_USER_DATA, mSessionManager.getPreference("" + mSessionManager.getPreferenceInt(Constants.TESTING_ENVIRONMENT_ID)));
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN, itemListData.get(position).getSubmitProfileModel().getauth_token());
                    mSessionManager.updatePreferenceString(Constants.AUTH_TOKEN_TYPE, mContext.getResources().getString(R.string.strTokenBearer));
                    mSessionManager.updatePreferenceString(Constants.PROTON_ACCOUNT_NAME, itemListData.get(position).getSubmitProfileModel().getProton().getAccountName());
                    //mSessionManager.updatePreferenceString(Constants.KEY_PROTON_WALLET_DETAILS,gson.toJson(body.getProton()));
                    mSessionManager.updatePreferenceString(Constants.KEY_REVEALIT_PRIVATE_KEY, itemListData.get(position).getSubmitProfileModel().getrevealit_private_key());
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_REGISTRATION_DONE, true);
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_CANCEL_REFERRAL, false);
                    mSessionManager.updatePreferenceString(Constants.KEY_MOBILE_NUMBER, itemListData.get(position).getMobileNumber());

                    //STORE DATA IN TO KEYSTORE
                    CommonMethods.encryptKey(itemListData.get(position).getSubmitProfileModel().getProton().getPrivateKey(), Constants.KEY_PRIVATE_KEY, itemListData.get(position).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);
                    CommonMethods.encryptKey(itemListData.get(position).getSubmitProfileModel().getProton().getPublicKey(), Constants.KEY_PUBLIC_KEY, itemListData.get(position).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);
                    CommonMethods.encryptKey(itemListData.get(position).getSubmitProfileModel().getProton().getMnemonic(), Constants.KEY_MNEMONICS, itemListData.get(position).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);
                    CommonMethods.encryptKey(itemListData.get(position).getSubmitProfileModel().getProton().getPrivate_pem(), Constants.KEY_PRIVATE_KEY_PEM, itemListData.get(position).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);
                    CommonMethods.encryptKey(itemListData.get(position).getSubmitProfileModel().getProton().getPublic_pem(), Constants.KEY_PUBLIC_KEY_PEM, itemListData.get(position).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);


                    //UPDATE FLAG IF USER IS ADMIN OR NOT
                    if (itemListData.get(position).getSubmitProfileModel().getRole().equals(mContext.getResources().getString(R.string.strAdmin))) {
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN, true);
                    } else {
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN, false);
                    }

                    //UPDATE ACTIVE FLAG
                    if (itemListData.get(position).getSubmitProfileModel().getIs_activated().equals("1")) {
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE, true);
                    } else {
                        mSessionManager.updatePreferenceBoolean(Constants.KEY_IS_USER_ACTIVE, false);
                    }
                    //UPDATE FLAG FOR APPLICATION MODE
                    mSessionManager.updatePreferenceBoolean(Constants.KEY_APP_MODE, true);

                    //STORE NECESSARY DATA IN CASE USER NOT AVAILABLE TO DP
                    mSessionManager.updatePreferenceString(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_USERNAME, itemListData.get(position).getSubmitProfileModel().getProton().getAccountName());
                    mSessionManager.updatePreferenceString(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PUBLICKEY, itemListData.get(position).getSubmitProfileModel().getProton().getPublicKey());
                    mSessionManager.updatePreferenceString(Constants.KEY_USER_NOT_FOUND_IMPORT_KEY_PRIVATEKEY, itemListData.get(position).getSubmitProfileModel().getProton().getPrivateKey());

                    //SAVE USER PROFILE PICS TO SESSION MANAGER
                    if(itemListData.get(position).getUserProfile() != null)
                    mSessionManager.updatePreferenceString(Constants.KEY_USERPROFILE_PIC, itemListData.get(position).getUserProfile().getProfile_image());


                    //GO TO BIOMETRIC CONFIRMATION ACTIVITY
                    Intent mIntent = new Intent(mActivity, NewAuthBiomatricAuthenticationActivity.class);
                    mIntent.putExtra(Constants.KEY_NEW_AUTH_USERNAME, itemListData.get(position).getSubmitProfileModel().getProton().getAccountName());
                    mIntent.putExtra(Constants.KEY_PRIVATE_KEY, itemListData.get(position).getSubmitProfileModel().getProton().getPrivateKey());
                    mIntent.putExtra(Constants.KEY_PROTON_ACCOUNTNAME, itemListData.get(position).getSubmitProfileModel().getProton().getAccountName());
                    mIntent.putExtra(Constants.KEY_USER_ROLE, itemListData.get(position).getSubmitProfileModel().getRole());
                    mIntent.putExtra(Constants.KEY_ISFROM_LOGIN, true);
                    mActivity.startActivity(mIntent);
                    mActivity.finishAffinity();


                }
            }
        });


    }


    @Override
    public int getItemCount() {

        return itemListData.size();
    }
}