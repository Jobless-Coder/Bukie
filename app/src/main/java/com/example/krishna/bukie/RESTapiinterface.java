package com.example.krishna.bukie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RESTapiinterface {
    String BASE_URL="https://us-central1-booksapp-e588d.cloudfunctions.net/";
    @POST("searchfunction/")
    Call<List<String>> searchBook(@Body com.example.krishna.bukie.Query query);


}
