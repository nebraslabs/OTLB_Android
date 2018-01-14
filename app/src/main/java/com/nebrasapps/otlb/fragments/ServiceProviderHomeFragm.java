package com.nebrasapps.otlb.fragments;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nebrasapps.otlb.R;
import com.nebrasapps.otlb.pojo.Data;
import com.nebrasapps.otlb.pojo.FCMResponse;
import com.nebrasapps.otlb.pojo.Sender;
import com.nebrasapps.otlb.pojo.Token;
import com.nebrasapps.otlb.utils.Constants;
import com.nebrasapps.otlb.utils.IGoogleDirectionsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */



public class ServiceProviderHomeFragm extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private ImageView mLogout, mSettings, mHistory;

    private Button mRideStatus;

    private Switch mWorkingSwitch;

    private int status = 0;

    private String customerId = "", destination;
    private LatLng destinationLatLng, pickupLatLng;
    private float rideDistance;

    private Boolean isLoggingOut = false;

    private SupportMapFragment mapFragment;

    private View mCustomerInfo;

    private ImageView mCustomerProfileImage;

    private TextView mCustomerName, mCustomerPhone, mCustomerDestination;
    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;

    private String userID;
    private TextView mCustomerPickUp;
    private String pickup;
    private Button mAccept;
    private Button mReject;
    private boolean functionCalled = false;
    private LatLngBounds latLngBounds;
    LatLngBounds.Builder boundsBuilder;
    private Marker userMarker;
    private String requestService = "";
    int count = 0;
    private View view;
    private DatabaseReference driveReqRef;
    private ValueEventListener driveReqStatusRefListener;
    private Handler handler;
    IGoogleDirectionsService mService;
    private long directionApiCalled = 0;
    private boolean requested = false;
    private View mTopLay;
    private View mBottomLay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        try {
            view = inflater.inflate(R.layout.serviceprovider_fragment, container, false);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            polylines = new ArrayList<>();
            mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mService = Constants.getGoogleDirectionsService();
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requested = true;
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                mapFragment.getMapAsync(this);
            }

            // initialize all the views
            mCustomerInfo = (View) view.findViewById(R.id.customerInfo);
            mTopLay = (View) view.findViewById(R.id.top_lay);
            mBottomLay = (View) view.findViewById(R.id.bottom_lay);

            mCustomerProfileImage = (ImageView) view.findViewById(R.id.customerProfileImage);

            mCustomerName = (TextView) view.findViewById(R.id.customerName);
            mCustomerPhone = (TextView) view.findViewById(R.id.customerPhone);
            mCustomerDestination = (TextView) view.findViewById(R.id.customerDestination);
            mCustomerPickUp = (TextView) view.findViewById(R.id.customerPickUp);
            AppCompatActivity act = (AppCompatActivity) getActivity();
            if (act != null) {
                mWorkingSwitch = (Switch) act.findViewById(R.id.workingSwitch);
                mWorkingSwitch.setVisibility(View.VISIBLE);
            }
            mSettings = (ImageView) view.findViewById(R.id.edit);
            mLogout = (ImageView) view.findViewById(R.id.logout);
            mHistory = (ImageView) view.findViewById(R.id.history);
            mRideStatus = (Button) view.findViewById(R.id.rideStatus);
            mAccept = (Button) view.findViewById(R.id.accept);
            mReject = (Button) view.findViewById(R.id.reject);
        } catch (InflateException e) {
            e.printStackTrace();
        }
        if (mWorkingSwitch != null) {
            mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //making driver available
                        connectDriver();
                    } else {
                        //disconnect driver from driversAvailable
                        disconnectDriver();
                    }
                }
            });
        }
        //check online status of the user working/not working
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
        checkDriverOnlineStatus();

        //update ridestatus dependingon service
        mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status) {
                    case 1:
                        if (requestService.equalsIgnoreCase("1") || requestService.equalsIgnoreCase("3") || requestService.equalsIgnoreCase("2")) {
                            status = 3;
                            erasePolylines();
                            if (destinationLatLng.latitude != 0.0 && destinationLatLng.longitude != 0.0) {
                                getRouteToMarker(destinationLatLng);
                            }
                            String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");

                            HashMap map = new HashMap();
                            map.put("status", "PickUp Done");
                            map.put("distance", mCustomerName.getText().toString());
                            map.put("duration", mCustomerPhone.getText().toString());
                            driverRef.updateChildren(map);
                            mRideStatus.setText("تم الوصول");

                        } else {
                            String driverId1 = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference driverRef1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId1).child("customerRequest");
                            HashMap map1 = new HashMap();
                            map1.put("status", "Completed");
                            driverRef1.updateChildren(map1);
                            completeRide();
                            erasePolylines();
                        }
                        break;
                    case 2:
                        updateRideStatus("Rejected");
                        endRide();
                        break;
                    case 3:
                        String driverId1 = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference driverRef1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId1).child("customerRequest");
                        HashMap map1 = new HashMap();
                        map1.put("status", "Completed");
                        driverRef1.updateChildren(map1);
                        updateRideStatus("Completed");
                        completeRide();
                        erasePolylines();
                        break;
                }
            }
        });
        // accept the request
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 1;
                //remove handler callbacks once request accept
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
                mAccept.setVisibility(View.GONE);
                mReject.setVisibility(View.GONE);
                mRideStatus.setVisibility(View.VISIBLE);
                mRideStatus.setText("تم الانطلاق");
                String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
                driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("service") != null) {
                            requestService = map.get("service").toString();
                            // recordRide("Accepted");
                            if (requestService.equalsIgnoreCase("1") || requestService.equalsIgnoreCase("3") || requestService.equalsIgnoreCase("2")) {
                                mRideStatus.setText("تم الانطلاق");
                            } else {
                                mRideStatus.setText("اكتمال الطلب");
                            }
                        } else {
                            requestService = "";
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // update stsatus as accepted
                final DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
                final String requestId = historyRef.push().getKey();
                HashMap map1 = new HashMap();
                map1.put("status", "accepted");
                map1.put("distance", mCustomerName.getText().toString());
                map1.put("duration", mCustomerPhone.getText().toString());
                map1.put("historyId", requestId);
                driverRef.updateChildren(map1);
                // move driver to driverWorking and remove from driversAvilable
                FirebaseDatabase.getInstance().getReference().child("Users/driversAvailable").child(userID).removeValue();
                FirebaseDatabase.getInstance().getReference().child("driversAvailable").child(userID).removeValue();
                DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                if (mLastLocation != null) {
                    GeoFire geoFireWorking = new GeoFire(refWorking);
                    geoFireWorking.setLocation(userID, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                }
                FirebaseDatabase.getInstance().getReference().child("Drivers").child(userID).child("customerRequest").removeValue();
                final DatabaseReference driverHistory = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID).child("history");
                final DatabaseReference customreHistory = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("history");
                //save history
                HashMap map = new HashMap();
                map.put("driver", userID);
                map.put("customer", customerId);
                map.put("rating", 0);
                map.put("status", "Accepted");
                map.put("timestamp", getCurrentTimestamp());
                map.put("pickup", pickup);
                HashMap location = new HashMap();
                if (pickupLatLng != null) {
                    HashMap from = new HashMap();
                    from.put("lat", pickupLatLng.latitude);
                    from.put("lng", pickupLatLng.longitude);
                    location.put("from", from);
                }
                if (requestService.equalsIgnoreCase("1") || requestService.equalsIgnoreCase("3") || requestService.equalsIgnoreCase("2"))

                {
                    if (destinationLatLng != null) {
                        HashMap to = new HashMap();
                        to.put("lat", destinationLatLng.latitude);
                        to.put("lng", destinationLatLng.longitude);
                        location.put("to", to);
                    }
                    map.put("destination", destination);
                }
                map.put("location", location);
                map.put("service", requestService);
                map.put("distance", rideDistance);

                driverHistory.child(requestId).setValue(true);
                customreHistory.child(requestId).setValue(true);
                historyRef.child(requestId).updateChildren(map);


            }
        });
        mReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 2;
                // recordRide("Rejected");
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
                //update status of request
                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID).child("customerRequest");
                HashMap map = new HashMap();
                map.put("status", "rejected");
                driverRef.updateChildren(map);
                // end ride
                endRide();
                // clear polylines from map
                erasePolylines();

            }
        });

        if (mLastLocation != null) {
            getAssignedCustomer();
        }

        return view;
    }

    private void updateRideStatus(String status) {


    }

    private void checkDriverOnlineStatus() {
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDriverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userID)) {
                    mWorkingSwitch.setChecked(true);
                } else {
                    DatabaseReference mDriverWorkingDatabase = FirebaseDatabase.getInstance().getReference().child("driversWorking");
                    mDriverWorkingDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(userID)) {
                                mWorkingSwitch.setChecked(true);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAssignedCustomer() {
        functionCalled = true;
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest").child("customerRideId");

        // DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("customerRequest").child(driverId);

        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    customerId = dataSnapshot.getValue().toString();
                    getAssignedCustomerPickupLocation();
                    getAssignedCustomerDestination();
                    getAssignedCustomerInfo();
                    getRequestStatus(customerId);
                } else {
                    //  endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getRequestStatus(String customerId) {

        if (driveReqRef != null && driveReqStatusRefListener != null) {
            driveReqRef.removeEventListener(driveReqStatusRefListener);
        }
        final String driverFoundID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        driveReqRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");

        driveReqStatusRefListener = driveReqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("status") != null && map.get("status").toString().equalsIgnoreCase("cancelled")) {
                        if (handler != null) {
                            handler.removeCallbacksAndMessages(null);
                        }
                        if (getActivity() != null) {
                            AlertDialog dialog = null;
                            AlertDialog.Builder subDialog = new AlertDialog.Builder(getActivity())
                                    .setMessage(getResources().getString(R.string.cancellled_request))
                                    .setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dlg2, int which) {
                                            dlg2.cancel();
                                        }
                                    });

                            if (subDialog != null) {
                                dialog = subDialog.create();
                                dialog.show();
                            }
                            if (dialog != null) {
                                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                                positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                positiveButtonLL.gravity = Gravity.CENTER;
                                positiveButton.setLayoutParams(positiveButtonLL);

                            }
                        }
                        updateData();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    Marker pickupMarker;
    private DatabaseReference assignedCustomerPickupLocationRef;
    private ValueEventListener assignedCustomerPickupLocationRefListener;

    private void getAssignedCustomerPickupLocation() {
        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !customerId.equals("")) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();

                    double locationLat = 0;
                    double locationLng = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    pickupLatLng = new LatLng(locationLat, locationLng);
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                    getRouteToMarker(pickupLatLng);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getRouteToMarker(LatLng pickupLatLng) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLng)
                .build();
        routing.execute();
    }

    private void getAssignedCustomerDestination() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("destination") != null) {
                        destination = map.get("destination").toString();
                        mCustomerDestination.setText("Destination: " + destination);
                    } else {
                        mCustomerDestination.setText("Destination: --");
                    }
                    if (map.get("pickup") != null) {
                        pickup = map.get("pickup").toString();
                        mCustomerPickUp.setText("PickUp: " + pickup);
                    } else {
                        mCustomerPickUp.setText("PickUp: --");
                    }

                    Double destinationLat = 0.0;
                    Double destinationLng = 0.0;
                    if (map.get("destinationLat") != null) {
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
                    }
                    if (map.get("destinationLng") != null) {
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        destinationLatLng = new LatLng(destinationLat, destinationLng);
                    }
                    if (map.get("service") != null) {
                        requestService = map.get("service").toString();
                    }
                    if (map.get("status") != null && map.get("status").toString().equalsIgnoreCase("accepted")) {
                        status = 1;
                        mAccept.setVisibility(View.GONE);
                        mReject.setVisibility(View.GONE);
                        mRideStatus.setVisibility(View.VISIBLE);
                        mRideStatus.setText("تم الانطلاق");
                    } else if (map.get("status") != null && map.get("status").toString().equalsIgnoreCase("pickup done")) {
                        status = 3;
                        mRideStatus.setText("تم الوصول");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void getAssignedCustomerInfo() {
        mCustomerInfo.setVisibility(View.VISIBLE);

        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (status == 0) {
                    mCustomerInfo.setVisibility(View.GONE);
                    mReject.callOnClick();


                }
                handler.removeCallbacks(this);
            }
        }, 55000);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    /*if(map.get("name")!=null){
                        mCustomerName.setText(map.get("name").toString());
                    }*/
                    /*if(map.get("phone")!=null){
                        mCustomerPhone.setText(map.get("phone").toString());
                    }*/
                    try {
                        if (map.get("profileImageUrl") != null) {
                            Glide.with(getContext().getApplicationContext()).load(map.get("profileImageUrl").toString()).into(mCustomerProfileImage);
                        } else {
                            Glide.with(getContext().getApplicationContext()).load(R.drawable.default_image).into(mCustomerProfileImage);

                        }
                    } catch (Exception e) {

                    }
                    if (map.get("status") != null && map.get("status").toString().equalsIgnoreCase("accepted")) {
                        status = 1;
                        mAccept.setVisibility(View.GONE);
                        mReject.setVisibility(View.GONE);
                        mRideStatus.setVisibility(View.VISIBLE);
                        mRideStatus.setText("تم الانطلاق");
                    } else if (map.get("status") != null && map.get("status").toString().equalsIgnoreCase("pickup done")) {
                        status = 3;
                        mRideStatus.setText("اكتمال الطلب");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void endRide() {
        mRideStatus.setText("picked customer");
        erasePolylines();

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
        driverRef.removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId);
        final DatabaseReference driverHistory = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
        final DatabaseReference customreHistory = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("history");

        HashMap map = new HashMap();
        map.put("driver", userId);
        map.put("customer", customerId);
        map.put("rating", 0);
        map.put("status", "Rejected");
        map.put("timestamp", getCurrentTimestamp());
        map.put("pickup", pickup);
        HashMap location = new HashMap();
        if (pickupLatLng != null) {
            HashMap from = new HashMap();
            from.put("lat", pickupLatLng.latitude);
            from.put("lng", pickupLatLng.longitude);
            location.put("from", from);
        }
        map.put("pickup", pickup);
        if (requestService.equalsIgnoreCase("1") || requestService.equalsIgnoreCase("3") || requestService.equalsIgnoreCase("2"))

        {
            if (destinationLatLng != null) {
                HashMap to = new HashMap();
                to.put("lat", destinationLatLng.latitude);
                to.put("lng", destinationLatLng.longitude);
                location.put("to", to);
            }
            map.put("destination", destination);
        }
        map.put("location", location);
        map.put("service", requestService);
        map.put("distance", rideDistance);
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
        String requestId = historyRef.push().getKey();
        driverHistory.child(requestId).setValue(true);
        customreHistory.child(requestId).setValue(true);
        historyRef.child(requestId).updateChildren(map);

        updateData();

    }

    private void completeRide() {

        erasePolylines();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
        driverRef.removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId);
        updateData();


    }

    private void updateData() {
        erasePolylines();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
        GeoFire geoFireWorking = new GeoFire(refWorking);
        geoFireWorking.removeLocation(userId);
        if (mWorkingSwitch.isChecked()) {
            DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
            GeoFire geoFireAvail = new GeoFire(refAvailable);
            if (mLastLocation != null) {
                geoFireAvail.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            }
        }
        customerId = "";
        rideDistance = 0;
        status = 0;
        if (pickupMarker != null) {
            pickupMarker.remove();
        }
        if (assignedCustomerPickupLocationRefListener != null) {
            assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
        }
        if (driveReqRef != null && driveReqStatusRefListener != null) {
            driveReqRef.removeEventListener(driveReqStatusRefListener);
        }
        mCustomerInfo.setVisibility(View.GONE);
        mRideStatus.setVisibility(View.VISIBLE);
        mRideStatus.setText("Call Uber");
        mCustomerName.setText("");
        mCustomerPhone.setText("");
        mCustomerDestination.setText("Destination: --");
        mRideStatus.setVisibility(View.GONE);
        mAccept.setVisibility(View.VISIBLE);
        mReject.setVisibility(View.VISIBLE);
        mCustomerProfileImage.setImageResource(R.mipmap.ic_default_user);
        if (mLastLocation != null) {
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        }
    }


    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis() / 1000;
        return timestamp;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        if (getActivity() != null) {
            if (!customerId.equals("")) {
                rideDistance += mLastLocation.distanceTo(location) / 1000;
            }

            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (userMarker != null)
                userMarker.remove();
            if (mMap != null) {
                userMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("You are here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
            }
            if (customerId != null && customerId.length() > 0) {
                /*int routePadding = 100;
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));*/
              /*  if(status==1)
                {
                    getRouteToMarker(pickupLatLng);
                }else  if(status==3)
                {
                    getRouteToMarker(destinationLatLng);
                }
*/
                if (latLng != null && pickupLatLng != null) {
                    if (directionApiCalled != 0) {
                        long different = System.currentTimeMillis() - directionApiCalled;
                        long secondsInMilli = 1000;
                        long elapsedSeconds = different / secondsInMilli;
                        if (elapsedSeconds > 45) {
                            directionApiCalled = System.currentTimeMillis();
                            getDirectionDistance(latLng, pickupLatLng);
                        }

                    } else {
                        directionApiCalled = System.currentTimeMillis();
                        getDirectionDistance(latLng, pickupLatLng);
                    }
                }
                if (status >= 1) {
                    if (mMap != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                    }
                }
            } else {
                if (polylines.size() > 0) {
                    erasePolylines();
                }
                if (mMap != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                }
            }
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
            DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
            GeoFire geoFireAvailable = new GeoFire(refAvailable);
            GeoFire geoFireWorking = new GeoFire(refWorking);

            switch (customerId) {
                case "":
                    geoFireWorking.removeLocation(userId);
                    geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;

                default:
                    geoFireAvailable.removeLocation(userId);
                    geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;
            }
        }
        if (mLastLocation != null) {
            if (!functionCalled) {
                getAssignedCustomer();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void connectDriver() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!requested) {
                requested = true;
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                return;
            }

        }
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            buildGoogleApiClient();
        }
    }

    public void disconnectDriver() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }


    final int LOCATION_REQUEST_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapFragment.getMapAsync(this);
                    if (mWorkingSwitch != null && mWorkingSwitch.isChecked()) {
                        connectDriver();
                    }
                    requested = false;
                } else {
                    requested = false;
                    Toast.makeText(getActivity(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        polylines = new ArrayList<>();
        boundsBuilder = new LatLngBounds.Builder();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
            for (LatLng latLngPoint : polyline.getPoints())
                boundsBuilder.include(latLngPoint);

            mCustomerName.setText("Distance -- " + route.get(i).getDistanceText());
            mCustomerPhone.setText(" ( " + route.get(i).getDurationText() + " )");
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
            HashMap map1 = new HashMap();
            map1.put("distance", mCustomerName.getText().toString());
            map1.put("duration", mCustomerPhone.getText().toString());
            driverRef.updateChildren(map1);
            // Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }
        int routePadding = 100;
        latLngBounds = boundsBuilder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
        if (mCustomerInfo.getVisibility() != View.VISIBLE) {
            erasePolylines();
        }

    }

    @Override
    public void onRoutingCancelled() {
    }

    private void erasePolylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }


    private void getDirectionDistance(LatLng source, LatLng destination) {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mService.getJson("driving", source.latitude + "," + source.longitude, destination.latitude + "," + destination.longitude, getResources().getString(R.string.google_maps_key))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response != null) {
                            try {
                                String retrofitRes1 = response.body().string();
                                if (response != null) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(retrofitRes1);
                                        JSONArray routes = jsonObject.getJSONArray("routes");
                                        JSONObject object = routes.getJSONObject(0);
                                        JSONArray legs = object.getJSONArray("legs");
                                        JSONObject legsObject = legs.getJSONObject(0);
                                        JSONObject distance = legsObject.getJSONObject("distance");
                                        JSONObject duration = legsObject.getJSONObject("duration");
                                        String distanceVal = distance.getString("text");
                                        String durationVal = duration.getString("text");
                                        mCustomerName.setText("Distance -- " + distanceVal);
                                        mCustomerPhone.setText(" ( " + durationVal + " )");
                                        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
                                        HashMap map1 = new HashMap();
                                        map1.put("distance", mCustomerName.getText().toString());
                                        map1.put("duration", mCustomerPhone.getText().toString());
                                        driverRef.updateChildren(map1);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }


                });


    }

    @Override
    public void onCameraMoveStarted(int reason) {
        switch (reason) {
            case GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE:
                if (mCustomerInfo.getVisibility() == View.VISIBLE) {
                    SlideToAbove();
                    SlideToBelow();
                }
                break;
        }
    }

    @Override
    public void onCameraIdle() {
        if (customerId != null && customerId.length() > 0) {
            if (mCustomerInfo.getVisibility() != View.VISIBLE) {

                mCustomerInfo.setVisibility(View.VISIBLE);
            }
        } else {
            mCustomerInfo.setVisibility(View.GONE);
        }
    }

    public void SlideToBelow() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 5.2f);

        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        mBottomLay.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mBottomLay.clearAnimation();

                mCustomerInfo.setVisibility(View.GONE);

            }

        });

    }

    public void SlideToAbove() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -5.0f);

        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        mTopLay.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mTopLay.clearAnimation();

                mCustomerInfo.setVisibility(View.GONE);

            }

        });

    }

}