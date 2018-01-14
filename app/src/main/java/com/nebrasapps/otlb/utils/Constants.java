package com.nebrasapps.otlb.utils;

/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */

public class Constants {
    public static String NEW_REQUEST = "new_request";
    public static String fcmURL = "https://fcm.googleapis.com/";
    public static String googleDirectionsUrl = "https://maps.googleapis.com/";
    public static IFcmService getFCMService()
    {
        return FcmClient.getClient(fcmURL).create(IFcmService.class);
    }
    public static IGoogleDirectionsService getGoogleDirectionsService()
    {
        return FcmClient.getClient(googleDirectionsUrl).create(IGoogleDirectionsService.class);
    }
}