package com.nebrasapps.otlb.utils;

import com.nebrasapps.otlb.pojo.FCMResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * ============================================================================
 *                                   ⁽(◍˃̵͈̑ᴗ˂̵͈̑)⁽
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */


public interface IGoogleDirectionsService {
    @GET("/maps/api/directions/json")
    Call<ResponseBody> getJson(@Query("mode") String mode, @Query("origin") String origin, @Query("destination") String destination, @Query("key") String key);

}
