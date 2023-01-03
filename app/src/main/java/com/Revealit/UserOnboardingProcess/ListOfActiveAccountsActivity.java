package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ImportAccountFromPrivateKey;
import com.Revealit.Adapter.SilosAvailableAccountsListAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.CommonClasse.SwipeHelper;
import com.Revealit.ModelClasses.KeyStoreServerInstancesModel;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ListOfActiveAccountsActivity extends AppCompatActivity implements View.OnClickListener {
    private Activity mActivity;
    private Context mContext;
    private static final String TAG = "ListOfActiveAccountsActivity";

    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private LinearLayout linearImgCancel;
    private RecyclerView recycleAccountList;
    ArrayList<KeyStoreServerInstancesModel.Data> selectedSilosAccountsList = new ArrayList<>();
    private RelativeLayout relativeRestoreFromCloud,relativeImportKey;
    private SilosAvailableAccountsListAdapter mSilosAvailableAccountsListAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_active_accounts);



    }

    @Override
    protected void onResume() {
        setIds();
        setOnClicks();
        super.onResume();
    }

    private void setIds() {

        mActivity = ListOfActiveAccountsActivity.this;
        mContext = ListOfActiveAccountsActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        linearImgCancel = (LinearLayout) findViewById(R.id.linearImgCancel);
        recycleAccountList = (RecyclerView) findViewById(R.id.recycleAccountList);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        recycleAccountList.setLayoutManager(mLinearLayoutManager);

        relativeImportKey = (RelativeLayout) findViewById(R.id.relativeImportKey);
        relativeRestoreFromCloud = (RelativeLayout) findViewById(R.id.relativeRestoreFromCloud);

        //FETCH USER'S SELECTED SILOS DATA
        bindRecyclerView();


        //CHECK IF USER IS ADMIN
        if (!mSessionManager.getPreferenceBoolean(Constants.KEY_IS_USER_IS_ADMIN)) {
            relativeRestoreFromCloud.setVisibility(View.GONE);
        }

    }

    private void bindRecyclerView() {


        ArrayList<KeyStoreServerInstancesModel.Data> selectedSilosAccountsList  = null;
        try {
            selectedSilosAccountsList = new FetchDataFromAndroidKeyStoreTask().execute(mSessionManager).get();
            //CHECK IF THERE IS DATA AVAILABLE FOR SELECTED SILOS
            if(selectedSilosAccountsList != null && selectedSilosAccountsList.size() != 0){

                if(mSilosAvailableAccountsListAdapter == null){


                    mSilosAvailableAccountsListAdapter = new SilosAvailableAccountsListAdapter(mContext, this, selectedSilosAccountsList, mSessionManager);
                    recycleAccountList.setAdapter(mSilosAvailableAccountsListAdapter);


                    //SWIPE HELPER SHOULD ATTACH FIRST TIME
                    ArrayList<KeyStoreServerInstancesModel.Data> mSelectedSilosAccountsList = selectedSilosAccountsList;

                    new SwipeHelper(mActivity, recycleAccountList) {
                        @Override
                        public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {

                            underlayButtons.add(new SwipeHelper.UnderlayButton(
                                    R.mipmap.icn_delete_with_text,
                                    mActivity,
                                    new SwipeHelper.UnderlayButtonClickListener() {
                                        @Override
                                        public void onClick(int pos) {
                                            displayWarningDialogue(mSelectedSilosAccountsList.get(pos).getSubmitProfileModel().getrevealit_private_key(),mSelectedSilosAccountsList.get(pos).getSubmitProfileModel().getProton().getPrivateKey(),mSelectedSilosAccountsList.get(pos).getSubmitProfileModel().getProton().getAccountName());

                                        }
                                    }
                            ));

                        }
                    };


                } else {
                    mSilosAvailableAccountsListAdapter.updateListData(selectedSilosAccountsList);
                }

            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }




    private void displayWarningDialogue(String revealitPrivateKey,String strPrivateKey, String strAccountName) {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_user_warning_dailoague, null);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBuilder.setView(dialogView);


        final AlertDialog mAlertDialog = dialogBuilder.create();
        TextView txtOk = (TextView) dialogView.findViewById(R.id.txtOk);
        TextView txtCancel = (TextView) dialogView.findViewById(R.id.txtCancel);
        LinearLayout linearCancel = (LinearLayout) dialogView.findViewById(R.id.linearCancel);


        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

                Intent mIntent = new Intent(ListOfActiveAccountsActivity.this, BiomatricAuthenticationDeleteAccontActivity.class);
                mIntent.putExtra(Constants.KEY_REVEALIT_PRIVATE_KEY, revealitPrivateKey);
                mIntent.putExtra(Constants.KEY_PRIVATE_KEY, strPrivateKey);
                mIntent.putExtra(Constants.KEY_PROTON_ACCOUNTNAME, strAccountName);
                startActivity(mIntent);



            }
        });


        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

            }
        });

        linearCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertDialog.dismiss();

            }
        });
        mAlertDialog.show();
    }

    private void setOnClicks() {

        linearImgCancel.setOnClickListener(this);
        relativeRestoreFromCloud.setOnClickListener(this);
        relativeImportKey.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {
        switch (mView.getId()){

            case R.id.linearImgCancel:
                finish();
                break;
            case R.id.relativeRestoreFromCloud:
                break;
            case R.id.relativeImportKey:
                Intent mIntent = new Intent(this, ImportAccountFromPrivateKey.class);
                startActivity(mIntent);
                break;
        }

    }
}
class FetchDataFromAndroidKeyStoreTask extends AsyncTask<SessionManager, Integer, ArrayList<KeyStoreServerInstancesModel.Data>> {


    @Override
    protected ArrayList<KeyStoreServerInstancesModel.Data> doInBackground(SessionManager... mSessionManager) {
        return CommonMethods.fetchUserSelectedSilosDataInList(mSessionManager[0]);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(ArrayList<KeyStoreServerInstancesModel.Data> searchResults) {
        super.onPostExecute(searchResults);

    }
}
