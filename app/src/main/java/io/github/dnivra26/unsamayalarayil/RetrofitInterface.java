package io.github.dnivra26.unsamayalarayil;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface RetrofitInterface {
    @POST("/register")
    void registerDevice(@Body String registrationId, Callback<String> cb);

}
