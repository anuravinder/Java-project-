package com.lbads.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrifitAPI {
    @GET("/mobile-getalllocations.php")
    Call<List<LocDetails>> getLocDetails();
}

