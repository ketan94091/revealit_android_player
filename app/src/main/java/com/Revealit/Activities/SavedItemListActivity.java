package com.Revealit.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Adapter.SavedItemsListsAdapter;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.SavedProductListModel;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

import java.util.ArrayList;

public class SavedItemListActivity extends AppCompatActivity implements View.OnClickListener {

    private SavedItemListActivity mActivity;
    private SavedItemListActivity mContext;
    private SessionManager mSessionManager;
    private DatabaseHelper mDatabaseHelper;
    private ImageView imgBackArrow;
    private RecyclerView recycleAccountList;
    private TextView txtCreateNewList;
   private ArrayList<SavedProductListModel.Item> dataList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_items_list);

        setId();
        setOnClicks();



    }


    private void setId() {

        mActivity = SavedItemListActivity.this;
        mContext = SavedItemListActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        imgBackArrow =(ImageView) findViewById(R.id.imgBackArrow);


        recycleAccountList = (RecyclerView) findViewById(R.id.recycleSavedProductList);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        recycleAccountList.setLayoutManager(mLinearLayoutManager);

        txtCreateNewList = (TextView)findViewById(R.id.txtCreateNewList);

        dataList =(ArrayList<SavedProductListModel.Item>)getIntent().getSerializableExtra("data");


        displaySavedItemList(dataList);

    }

    private void setOnClicks() {

        imgBackArrow.setOnClickListener(this);

    }

    private void displaySavedItemList(ArrayList<SavedProductListModel.Item> dataList) {



        if(dataList.size() == 0){
            txtCreateNewList.setVisibility(View.VISIBLE);
            recycleAccountList.setVisibility(View.GONE);
        }else{

            //BIND RECYCLER VIEW
            SavedItemsListsAdapter mUserSavedListsAdapter = new SavedItemsListsAdapter(mContext, SavedItemListActivity.this, dataList);
            recycleAccountList.setAdapter(mUserSavedListsAdapter);

        }


    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.imgBackArrow:

                finish();

                break;
        }
    }
}