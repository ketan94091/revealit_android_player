package com.Revealit.RetrofitClass;


import com.Revealit.ModelClasses.CoursesModel;
import com.Revealit.ModelClasses.Test;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;


/**
 * Created by baps on 29-03-2017.
 */

public interface UpdateAllAPI {

    @GET()
    Call<CoursesModel> callGetCourses(@Url String url);

}
