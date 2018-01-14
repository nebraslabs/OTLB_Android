package com.nebrasapps.otlb.pojo;

import com.nebrasapps.otlb.historyRecyclerView.HistoryObject;

/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */



public class GeneralItem extends ListItem {

    private HistoryObject pojoOfJsonArray;

    public HistoryObject getPojoOfJsonArray() {
        return pojoOfJsonArray;
    }

    public void setPojoOfJsonArray(HistoryObject pojoOfJsonArray) {
        this.pojoOfJsonArray = pojoOfJsonArray;
    }

    @Override
    public int getType() {
        return TYPE_GENERAL;
    }
}