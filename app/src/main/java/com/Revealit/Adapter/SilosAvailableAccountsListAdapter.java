package com.Revealit.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.KeyStoreServerInstancesModel;
import com.Revealit.R;
import com.Revealit.UserOnboardingProcess.ListOfActiveAccountsActivity;
import com.Revealit.UserOnboardingProcess.NewAuthBiomatricAuthenticationActivity;

import java.util.ArrayList;

public class SilosAvailableAccountsListAdapter extends RecyclerView.Adapter<SilosAvailableAccountsListAdapter.ViewHolder> {



    private View view;
    private Context mContext;
    private ListOfActiveAccountsActivity mActivity;
    private ViewHolder viewHolder;
    private ArrayList<KeyStoreServerInstancesModel.Data> itemListData;
    private SessionManager mSessionManager;


    public SilosAvailableAccountsListAdapter(Context mContext, ListOfActiveAccountsActivity mActivity, ArrayList<KeyStoreServerInstancesModel.Data> itemListData, SessionManager mSessionManager) {
        this.mContext= mContext;
        this.mActivity= mActivity;
        this.itemListData =itemListData;
        this.mSessionManager =mSessionManager;

    }
    public void updateListData(ArrayList<KeyStoreServerInstancesModel.Data> itemListData) {

        //CLEAR CURRENT LIST
        this.itemListData = itemListData;

        //NOTIFY LISTENER
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtAccountUsername,txtAccountName;
        private final RelativeLayout relativeMain;


        public ViewHolder(View mView) {

            super(mView);

            txtAccountUsername = (TextView) mView.findViewById(R.id.txtAccountUsername);
            txtAccountName = (TextView) mView.findViewById(R.id.txtAccountName);
            relativeMain =(RelativeLayout)mView.findViewById(R.id.relativeMain);


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

           //SET ITEM TEXT
        holder.txtAccountUsername.setText(itemListData.get(position).getSubmitProfileModel().getProton().getAccountName());
        holder.txtAccountName.setText("@"+itemListData.get(position).getSubmitProfileModel().getProton().getAccountName());

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
                        mSessionManager.updatePreferenceString(Constants.KEY_MOBILE_NUMBER,itemListData.get(position).getMobileNumber());

                        //STORE DATA IN TO KEYSTORE
                        CommonMethods.encryptKey(itemListData.get(position).getSubmitProfileModel().getProton().getPrivateKey(), Constants.KEY_PRIVATE_KEY,itemListData.get(position).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);
                        CommonMethods.encryptKey(itemListData.get(position).getSubmitProfileModel().getProton().getPublicKey(),Constants.KEY_PUBLIC_KEY, itemListData.get(position).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);
                        CommonMethods.encryptKey(itemListData.get(position).getSubmitProfileModel().getProton().getMnemonic(),Constants.KEY_MNEMONICS, itemListData.get(position).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);
                        CommonMethods.encryptKey(itemListData.get(position).getSubmitProfileModel().getProton().getPrivate_pem(),Constants.KEY_PRIVATE_KEY_PEM, itemListData.get(position).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);
                        CommonMethods.encryptKey(itemListData.get(position).getSubmitProfileModel().getProton().getPublic_pem(), Constants.KEY_PUBLIC_KEY_PEM,itemListData.get(position).getSubmitProfileModel().getrevealit_private_key(), mSessionManager);


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

                        //GO TO BIOMETRIC CONFIRMATION ACTIVITY
                        Intent mIntent = new Intent(mActivity, NewAuthBiomatricAuthenticationActivity.class);
                        mIntent.putExtra(Constants.KEY_NEW_AUTH_USERNAME, itemListData.get(position).getSubmitProfileModel().getProton().getAccountName());
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