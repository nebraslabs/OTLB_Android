package com.nebrasapps.otlb.historyRecyclerView;

/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */




public class HistoryObject {
    private  String datetime;
    private String rideId;
    private String time;
    private String status;
    private String service;
    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    private String pickup;

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    private String drop;
    public HistoryObject(String rideId, String time,String pickup,String drop,String status,String datetime,String service){
        this.rideId = rideId;
        this.time = time;
        this.datetime = datetime;
        this.pickup = pickup;
        this.drop = drop;
        this.status = status;
        this.service = service;
    }

    public String getRideId(){return rideId;}
    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getTime(){return time;}
    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
