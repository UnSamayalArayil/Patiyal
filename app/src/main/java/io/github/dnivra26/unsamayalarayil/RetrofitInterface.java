package io.github.dnivra26.unsamayalarayil;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface RetrofitInterface {
    @POST("/register")
    void registerDevice(@Body RegistrationMessage registrationMessage, Callback<RegistrationResponse> responseCallback);

    @POST("/list")
    void getAllItems(@Body String user_id, Callback<ListResponse> cb);

    @POST("/newdevice")
    void addItem(@Body NewDevice newDevice, Callback<RegistrationResponse> responseCallback);


}
