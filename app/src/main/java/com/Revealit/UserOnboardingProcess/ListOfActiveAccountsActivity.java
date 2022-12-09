package com.Revealit.UserOnboardingProcess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ImportAccountFromPrivateKey;
import com.Revealit.Adapter.SilosAvailableAccountsListAdapter;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.KeyStoreServerInstancesModel;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

import java.util.ArrayList;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_active_accounts);

        setIds();
        setOnClicks();

    }


    private void setIds() {

        mActivity = ListOfActiveAccountsActivity.this;
        mContext = ListOfActiveAccountsActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        linearImgCancel =(LinearLayout)findViewById(R.id.linearImgCancel);
        recycleAccountList =(RecyclerView)findViewById(R.id.recycleAccountList);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        recycleAccountList.setLayoutManager(mLinearLayoutManager);

        relativeImportKey =(RelativeLayout)findViewById(R.id.relativeImportKey);
        relativeRestoreFromCloud =(RelativeLayout)findViewById(R.id.relativeRestoreFromCloud);

        //FETCH USER'S SELECTED SILOS DATA
        //selectedSilosAccountsList = CommonMethods.fetchUserSelectedSilosDataInList(mSessionManager,mActivity);
        try {
            selectedSilosAccountsList = new FetchDataFromAndroidKeyStoreTask().execute(mSessionManager).get();

            //CHECK IF THERE IS DATA AVAILABLE FOR SELECTED SILOS
            if(selectedSilosAccountsList != null && selectedSilosAccountsList.size() != 0){
                SilosAvailableAccountsListAdapter mSilosAvailableAccountsListAdapter = new SilosAvailableAccountsListAdapter(mContext, this, selectedSilosAccountsList, mSessionManager);
                recycleAccountList.setAdapter(mSilosAvailableAccountsListAdapter);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //CHECK USER ACCOUNT IN TO THE PROTON





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
