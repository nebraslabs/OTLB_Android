package com.nebrasapps.otlb.fragments;

/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nebrasapps.otlb.R;
import com.nebrasapps.otlb.historyRecyclerView.HistoryObject;
import com.nebrasapps.otlb.historyRecyclerView.HistoryRecyclerAdapter;
import com.nebrasapps.otlb.pojo.DateItem;
import com.nebrasapps.otlb.pojo.GeneralItem;
import com.nebrasapps.otlb.pojo.ListItem;
import com.nebrasapps.otlb.storage.SharedData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;


public class HistoryFragment extends Fragment {
    private String customerOrDriver, userId;
    private RecyclerView mHistoryRecyclerView;
    private RecyclerView.LayoutManager mHistoryLayoutManager;
    private LinearLayout mNoDataLay;
    private TreeMap<String, List<HistoryObject>> historyData;
    private HistoryRecyclerAdapter adapter;
    private long totallcount = 0;
    private LinearLayout progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.history_lay, container, false);
        //views initialization
        progressBar = (LinearLayout) view.findViewById(R.id.lay_progress);
        mHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.historyRecyclerView);
        mNoDataLay = (LinearLayout) view.findViewById(R.id.no_data);
        mHistoryRecyclerView.setNestedScrollingEnabled(false);
        mHistoryRecyclerView.setHasFixedSize(true);
        mHistoryLayoutManager = new LinearLayoutManager(getActivity());
        mHistoryRecyclerView.setLayoutManager(mHistoryLayoutManager);
        progressBar.setVisibility(View.GONE);
        //getting user role to query Firebase based on user role
        customerOrDriver = SharedData.getPref("role", "");
        if (customerOrDriver.equalsIgnoreCase("customer")) {
            customerOrDriver = "Customers";
        } else {
            customerOrDriver = "Drivers";
        }
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressBar.setVisibility(View.VISIBLE);
        getUserHistoryIds();
        return view;
    }

    public void getUserHistoryIds() {
        //getting latest last 50records
        progressBar.setVisibility(View.VISIBLE);
        if(resultsHistory!=null && resultsHistory.size()>0)
        {
            resultsHistory.clear();
        }
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(customerOrDriver).child(userId).child("history");
        userHistoryDatabase.limitToLast(50);
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    totallcount = dataSnapshot.getChildrenCount();
                    for (DataSnapshot history : dataSnapshot.getChildren()) {
                        // getting details based on id
                        FetchRideInformation(history.getKey());
                    }
                    if (dataSnapshot.getChildrenCount() <= 0) {
                        //if no history showing noData Layout and hiding loading bar
                        mNoDataLay.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                    } else {
                        mNoDataLay.setVisibility(View.GONE);

                    }
                } else {
                    //if no history showing noData Layout and hiding loading bar
                    mNoDataLay.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //hiding loading bar and if history data >0 grouping array data to Tree map  to save date as key and data as values.
                progressBar.setVisibility(View.GONE);
                if (resultsHistory.size() > 0) {
                    Collections.reverse(resultsHistory);
                    if(historyData!=null && historyData.size()>0)
                    {
                        historyData.clear();
                    }
                    historyData = groupDataIntoHashMap(resultsHistory);
                    // converting hashmap to arraylist to sort data
                    filterHistory();
                }
            }
        });
    }

    private TreeMap<String, List<HistoryObject>> groupDataIntoHashMap(List<HistoryObject> listOfNotificationsJsonArray) {

        TreeMap<String, List<HistoryObject>> groupedHashMap = new TreeMap<>(Collections.reverseOrder());

        for (HistoryObject notificationArray : listOfNotificationsJsonArray) {

            String hashMapKey = notificationArray.getTime();

            if (groupedHashMap.containsKey(hashMapKey)) {
                // The key is already in the HashMap; add the pojo object
                // against the existing key.
                groupedHashMap.get(hashMapKey).add(notificationArray);
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                List<HistoryObject> list = new ArrayList<>();
                list.add(notificationArray);
                groupedHashMap.put(hashMapKey, list);
            }
        }

        return groupedHashMap;
    }

    private void FetchRideInformation(String rideKey) {
        DatabaseReference historyDatabase = FirebaseDatabase.getInstance().getReference().child("history").child(rideKey);
        historyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String rideId = dataSnapshot.getKey();
                    Long timestamp = 0L;
                    String distance = "";
                    String pickup = "";
                    String destination = "";
                    String status = "";
                    Double ridePrice = 0.0;
                    String service = "";
                    if (dataSnapshot.child("timestamp").getValue() != null) {
                        timestamp = Long.valueOf(dataSnapshot.child("timestamp").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("pickup") && dataSnapshot.child("pickup").getValue() != null) {
                        pickup = dataSnapshot.child("pickup").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("destination") && dataSnapshot.child("destination").getValue() != null) {
                        destination = dataSnapshot.child("destination").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("status") && dataSnapshot.child("status").getValue() != null) {
                        status = dataSnapshot.child("status").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("service") && dataSnapshot.child("service").getValue() != null) {
                        service = dataSnapshot.child("service").getValue().toString();
                    }

                    HistoryObject obj = new HistoryObject(rideId, getDate(timestamp), pickup, destination, status, getDateTime(timestamp), service);
                    resultsHistory.add(obj);
                    if (totallcount == resultsHistory.size()) {
                        Collections.reverse(resultsHistory);
                        historyData = groupDataIntoHashMap(resultsHistory);
                        filterHistory();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private String getDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("yyyy-MM-dd", cal).toString();
        return date;
    }

    private String getDateTime(Long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("hh:mm a", cal).toString();
        return date;
    }

    private ArrayList<HistoryObject> resultsHistory = new ArrayList<HistoryObject>();

    private ArrayList<HistoryObject> getDataSetHistory() {
        return resultsHistory;
    }

    private void filterHistory() {

        if (historyData.size() > 0) {
            List<ListItem> consolidatedList = new ArrayList<>();
            // setting Data to arraylist based on date from TreeMap
            for (String date : historyData.keySet()) {
                DateItem dateItem = new DateItem();
                dateItem.setDate(date);
                consolidatedList.add(dateItem);
                //adding data inside date as key
                for (HistoryObject pojoOfJsonArray : historyData.get(date)) {

                    GeneralItem generalItem = new GeneralItem();
                    generalItem.setPojoOfJsonArray(pojoOfJsonArray);
                    consolidatedList.add(generalItem);

                }
            }
            if (consolidatedList.size() <= 0) {
                mNoDataLay.setVisibility(View.VISIBLE);

            } else {
                mNoDataLay.setVisibility(View.GONE);

            }
            //  setting adapter to historyRecyclerView
            adapter = new HistoryRecyclerAdapter(getActivity(), consolidatedList);
            mHistoryRecyclerView.setAdapter(adapter);
        }

    }

}
