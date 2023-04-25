package com.Revealit.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ProductBuyingScreenActivity;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.ModelClasses.SavedProductListModel;
import com.Revealit.R;
import com.Revealit.RetrofitClass.UpdateAllAPI;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SavedListNameAdapter extends RecyclerView.Adapter<SavedListNameAdapter.ViewHolder> {


    private View view;
    private Context mContext;
    private ProductBuyingScreenActivity mActivity;
    private ViewHolder viewHolder;
    public List<SavedProductListModel.Data> data;
    private int selectedPosition = -1;
    private RecyclerView recycleAccountList;
    private String strItemId;
    private SessionManager mSessionManager;
    private BottomSheetDialog bottomSheetDialog;


    public SavedListNameAdapter(Context mContext, ProductBuyingScreenActivity mActivity, List<SavedProductListModel.Data> data, RecyclerView recycleAccountList, String strItemId, SessionManager mSessionManager, BottomSheetDialog bottomSheetDialog) {

        this.mContext = mContext;
        this.mActivity = mActivity;
        this.data = data;
        this.recycleAccountList =recycleAccountList;
        this.strItemId =strItemId;
        this.mSessionManager = mSessionManager;
        this.bottomSheetDialog= bottomSheetDialog;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtListName,txtTotalItemsInList;
        private final CheckBox checkListName;


        public ViewHolder(View mView) {

            super(mView);

            txtListName = (TextView) mView.findViewById(R.id.txtListName);
            txtTotalItemsInList = (TextView) mView.findViewById(R.id.txtTotalItemsInList);
            checkListName = (CheckBox) mView.findViewById(R.id.checkListName);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_saved_list_names, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.txtListName.setText(data.get(position).getName());
        holder.txtTotalItemsInList.setText(data.get(position).getItems().size() + ": Items");


        if(selectedPosition == position){
            holder.checkListName.setChecked(true);
        }
        else{
            holder.checkListName.setChecked(false);
        }



        holder.checkListName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last status
                selectedPosition = holder.getAdapterPosition();

                recycleAccountList.post(new Runnable()
                {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

                //API CALL TO SAVE ITEM IN LIST
                callApiAddItemTolist(strItemId, data.get(position));
            }
        });



    }
    private void callApiAddItemTolist(String strItemId, SavedProductListModel.Data objectData) {

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
        paramObject.addProperty("list_id", objectData.getId());
        paramObject.addProperty("item_id", Long.valueOf(strItemId));


        Call<JsonElement> call = patchService1.addItemToTheList(paramObject);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                CommonMethods.printLogE("Response @ callApiAddItemTolist: ", "" + response.isSuccessful());
                CommonMethods.printLogE("Response @ callApiAddItemTolist: ", "" + response.code());
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .serializeNulls()
                        .create();

                CommonMethods.printLogE("Response @ callApiAddItemTolist: ", "" + gson.toJson(response.body()));

                if (response.code() == 200  ) {

                    //CANCEL BOTTOM SHEET DIALOGUE AND DISPLAY SUCCESS MESSAGE
                    bottomSheetDialog.cancel();

                    //DISPLAY ITEM SAVED MSG
                    CommonMethods.displayToast(mContext,"Items saved to "+objectData.getName()+" ! ");




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

                CommonMethods.buildDialog(mContext, mActivity.getResources().getString(R.string.strApiCallFailure));


            }
        });

    }



    @Override
    public int getItemCount() {

        return data.size();
    }


}