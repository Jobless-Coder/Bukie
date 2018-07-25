package com.example.krishna.bukie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RESTapiinterface {
    String BASE_URL="https://us-central1-booksapp-e588d.cloudfunctions.net/";

    @POST("searchfunction/")
    Call<List<String>> searchBook(@Body String query);

}
