package com.nebrasapps.otlb.utils;

import com.nebrasapps.otlb.pojo.FCMResponse;
import com.nebrasapps.otlb.pojo.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * ============================================================================
 *                                   ⁽(◍˃̵͈̑ᴗ˂̵͈̑)⁽
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */


public interface IFcmService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAcueO6Yw:APA91bGm558mCeJocqVNN8nO8TV-P_nBVxYQUqxRzZ_Nzm0f5aXs_P9-iZ8ZsqJBcAYpA1cRmvGAfy_93bWsEmgKA4jT6RTd2OOkgYI1jpUC8D8uE-Irrj93k3xfPvGZXWTiqOLUFQF9"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
