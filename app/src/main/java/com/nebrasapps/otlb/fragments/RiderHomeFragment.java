package com.nebrasapps.otlb.fragments;


/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */



import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nebrasapps.otlb.BuildConfig;
import com.nebrasapps.otlb.utils.Constants;
import com.nebrasapps.otlb.utils.IFcmService;
import com.nebrasapps.otlb.MainApp;
import com.nebrasapps.otlb.R;
import com.nebrasapps.otlb.ServiceTypesAdapter;
import com.nebrasapps.otlb.components.MapsActivity;
import com.nebrasapps.otlb.pojo.Data;
import com.nebrasapps.otlb.pojo.FCMResponse;
import com.nebrasapps.otlb.pojo.Sender;
import com.nebrasapps.otlb.pojo.Services;
import com.nebrasapps.otlb.pojo.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;

public class RiderHomeFragment extends Fragment implements ServiceTypesAdapter.OnItemClickListener,GoogleMap.OnMapClickListener,OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private ImageView mLogout,mSettings,mHistory;
    private Button  mRequest ;
    private LatLng pickupLocation;
    private Boolean requestBol = false;
    private Marker pickupMarker;
    private SupportMapFragment mapFragment;
    private String destination, requestService;
    private LatLng destinationLatLng;
    private View mDriverInfo;
    private ImageView mDriverProfileImage;
    private TextView mDriverName, mDriverPhone;
    private RadioGroup mRadioGroup;
    private RatingBar mRatingBar;
    private TextView mTxtPickup;
    private TextView mTxtDrop;
    private LinearLayout mCabLay;
    private String pickup;
    private LinearLayout mProgressLay;
    private IFcmService mService;
    private View mDropView;
    private String serviceType="1";
    private Marker dropMarker;
    private View mPickUpView;
    private boolean pickupSel=true;
    private LatLng pickupLatLng;
    View view;
    private RecyclerView rcServices;
    private List<Services> serviceTypesList=new ArrayList<Services>();
    private ServiceTypesAdapter serviceTypeAdapter;
    private int serviceTypePos=0;
    private LinearLayout mPickUpLay;
    private LinearLayout mDropLay;
    private View mImgResetPick;
    private View mImgResetDrop;
    int PLACE_PICKER_REQUEST = 1;
    private String historyId="";
    private ImageView mCurrentLoc;
    private TextView mDistance;
    private View mCancel;
    private View mTxtOnBoard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //clearing view if not null as it contains MapFragment and recreating view
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        try {
            view = inflater.inflate(R.layout.rider_fragm, container, false);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mService = Constants.getFCMService();
            // checking for location permission if not asking for location permission
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions( new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                //loading map asyncronously
                mapFragment.getMapAsync(this);
            }
            //getting reference of currentLoc button from MainActivity
            AppCompatActivity act=(AppCompatActivity)getActivity();
            if(act!=null) {
                mCurrentLoc = (ImageView) act.findViewById(R.id.currentLoc);
                mCurrentLoc.setVisibility(View.VISIBLE);
            }
            //view Initialization
            mPickUpLay = (LinearLayout) view.findViewById(R.id.pickupLay);
            mDropLay = (LinearLayout) view.findViewById(R.id.dropLay);
            mDropView = (View) view.findViewById(R.id.dropView);
            mPickUpView = (View) view.findViewById(R.id.pickup);
            mDriverInfo = (View) view.findViewById(R.id.driverInfo);
            mCabLay = (LinearLayout) view.findViewById(R.id.cab_lay);
            mImgResetPick = (View) view.findViewById(R.id.reset_pickup);
            mImgResetDrop = (View) view.findViewById(R.id.reset_drop);
            mProgressLay = (LinearLayout) view.findViewById(R.id.progress_lay);
            mDriverProfileImage = (ImageView) view.findViewById(R.id.driverProfileImage);
            mDriverName = (TextView) view.findViewById(R.id.driverName);
            mDriverPhone = (TextView) view.findViewById(R.id.driverPhone);
            mDistance = (TextView) view.findViewById(R.id.distance);
            mTxtPickup = (TextView) view.findViewById(R.id.txt_pickup);
            mTxtDrop = (TextView) view.findViewById(R.id.txt_drop);
            mTxtOnBoard = (View) view.findViewById(R.id.onboard);
            mRatingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            mRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
            mRadioGroup.check(R.id.taxi);
            mHistory = (ImageView) view.findViewById(R.id.history);
            mLogout = (ImageView) view.findViewById(R.id.logout);
            mRequest = (Button) view.findViewById(R.id.request);
            mSettings = (ImageView) view.findViewById(R.id.edit);
            mCancel = (View) view.findViewById(R.id.cancel);
            //setting LinearLayoutManager for recyclerView
            LinearLayoutManager countryLayoutManager =new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,true);
            rcServices = (RecyclerView) view.findViewById(R.id.rc_services);
            rcServices.setLayoutManager(countryLayoutManager);
            //setting serviceTypesAdapter
            //setting Drop & Pickup dynamically layout params
            mPickUpLay.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.white_color));
            mDropLay.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.desc_color));
            mTxtPickup.setTextColor(ContextCompat.getColor(getActivity(),R.color.grey_clr));
            mTxtDrop.setTextColor(ContextCompat.getColor(getActivity(),R.color.grey_clr));
            mTxtPickup.setClickable(true);
            mTxtDrop.setClickable(false);
            pickupSel=true;
            setAdapter();
        }  catch (InflateException e) {
        e.printStackTrace();
         }

        mCurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(mLastLocation!=null)
               {
                   LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                   CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                   mMap.animateCamera(cameraUpdate);
               }
            }
        });
        mImgResetPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCancel.getVisibility()!=View.VISIBLE) {
                    pickupLatLng = null;
                    mTxtPickup.setText("");
                    mImgResetPick.setVisibility(View.INVISIBLE);
                    if (pickupMarker != null) {
                        pickupMarker.remove();
                        pickupMarker = null;
                    }
                }
            }
        });
        mImgResetDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCancel.getVisibility()!=View.VISIBLE) {
                    destinationLatLng = null;
                    mTxtDrop.setText("");
                    mImgResetDrop.setVisibility(View.INVISIBLE);
                    if (dropMarker != null) {
                        dropMarker.remove();
                        dropMarker = null;
                    }
                }
            }
        });
        mDropView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   mDropView.bringToFront();
                mDropView.invalidate();
                mPickUpView.invalidate();*/
               mDropLay.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.white_color));
                mPickUpLay.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.desc_color));
                mTxtPickup.setTextColor(ContextCompat.getColor(getActivity(),R.color.grey_clr));
                mTxtDrop.setTextColor(ContextCompat.getColor(getActivity(),R.color.grey_clr));
                mTxtPickup.setClickable(false);
                mTxtDrop.setClickable(true);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.setMargins(60,0,60,0);
                mPickUpLay.setLayoutParams(params);
                LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params1.setMargins(10,0,10,0);
                mDropLay.setLayoutParams(params1);

                pickupSel=false;
                if(destinationLatLng!=null && destinationLatLng.latitude!=0.0)
                {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(destinationLatLng, 16);
                    mMap.animateCamera(cameraUpdate);
                }
               /* mImgResetPick.setVisibility(View.GONE);
                if(mTxtDrop.length()<=0)
                {
                    mImgResetDrop.setVisibility(View.INVISIBLE);
                }else
                {
                    mImgResetDrop.setVisibility(View.VISIBLE);
                }*/
            }
        });
        mPickUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
         /*       mPickUpView.bringToFront();
                mPickUpView.invalidate();
                mDropView.invalidate();*/
               mPickUpLay.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.white_color));
                mDropLay.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.desc_color));
                mTxtPickup.setTextColor(ContextCompat.getColor(getActivity(),R.color.grey_clr));
                mTxtDrop.setTextColor(ContextCompat.getColor(getActivity(),R.color.grey_clr));
                mTxtPickup.setClickable(true);
                mTxtDrop.setClickable(false);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.setMargins(60,0,60,0);
                mDropLay.setLayoutParams(params);
                LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params1.setMargins(10,0,10,0);
                mPickUpLay.setLayoutParams(params1);
                pickupSel=true;
                if(pickupLatLng!=null && pickupLatLng.latitude!=0.0)
                {

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pickupLatLng, 16);
                    mMap.animateCamera(cameraUpdate);
                }
               /* mImgResetDrop.setVisibility(View.GONE);
                if(mTxtPickup.length()<=0)
                {
                    mImgResetPick.setVisibility(View.INVISIBLE);
                }else
                {
                    mImgResetPick.setVisibility(View.VISIBLE);
                }*/

            }
        });
       mTxtPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCancel.getVisibility()!=View.VISIBLE) {
                    if (mLastLocation != null) {
                        Intent intent = new Intent(getActivity(), MapsActivity.class);
                        intent.putExtra("lat", mLastLocation.getLatitude());
                        intent.putExtra("lon", mLastLocation.getLongitude());
                        startActivityForResult(intent, 20);
                    }
                }
            }
        });
        mTxtDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCancel.getVisibility()!=View.VISIBLE) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    try {
                        startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        mTxtPickup.setClickable(true);
        mTxtDrop.setClickable(false);
        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainApp.isNetworkAvailable(getActivity()))
                {
                if (requestBol){
                   endRide();


                }else {
                    if (pickupLatLng != null) {
                        if (destinationLatLng == null) {
                            if (serviceType.equalsIgnoreCase("1") || serviceType.equalsIgnoreCase("3") || serviceType.equalsIgnoreCase("2"))
                            {
                                AlertDialog dialog=null;
                                AlertDialog.Builder subDialog = new AlertDialog.Builder(getActivity())
                                        .setMessage(getResources().getString(R.string.loc_error))
                                        .setCancelable(false)
                                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dlg2, int which) {
                                                dlg2.cancel();
                                            }
                                        });
                                if(subDialog!=null) {
                                    dialog = subDialog.create();
                                    dialog.show();
                                }
                                if(dialog!=null) {
                                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                                    positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                    positiveButtonLL.gravity = Gravity.CENTER;
                                    positiveButton.setLayoutParams(positiveButtonLL);

                                }
                                return;
                            }

                        }
                        if (serviceType == null) {
                            return;
                        }
                        if (serviceType.length() <= 0) {
                            AlertDialog dialog=null;
                            AlertDialog.Builder subDialog = new AlertDialog.Builder(getActivity())
                                    .setMessage(getResources().getString(R.string.service_type))
                                    .setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dlg2, int which) {
                                            dlg2.cancel();
                                        }
                                    });
                            if(subDialog!=null) {
                                dialog = subDialog.create();
                                dialog.show();
                            }
                            if(dialog!=null) {
                                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                                positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                positiveButtonLL.gravity = Gravity.CENTER;
                                positiveButton.setLayoutParams(positiveButtonLL);

                            }
                            return;
                        }
                        requestService = serviceType;

                        requestBol = true;
                        historyId="";
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.setLocation(userId, new GeoLocation(pickupLatLng.latitude, pickupLatLng.longitude));

                        pickupLocation = new LatLng(pickupLatLng.latitude, pickupLatLng.longitude);

                        pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here"));

                        //mRequest.setText("Getting your Driver....");

                        getClosestDriver();
                    } else {
                        if (serviceType.equalsIgnoreCase("1") || serviceType.equalsIgnoreCase("3") || serviceType.equalsIgnoreCase("2"))
                        {AlertDialog dialog=null;
                            AlertDialog.Builder subDialog = new AlertDialog.Builder(getActivity())
                                    .setMessage(getResources().getString(R.string.loc_error))
                                    .setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dlg2, int which) {
                                            dlg2.cancel();
                                        }
                                    });
                            if(subDialog!=null) {
                                dialog = subDialog.create();
                                dialog.show();
                            }
                            if(dialog!=null) {
                                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                                positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                positiveButtonLL.gravity = Gravity.CENTER;
                                positiveButton.setLayoutParams(positiveButtonLL);

                            }
                        } else {
                            AlertDialog dialog=null;
                            AlertDialog.Builder subDialog = new AlertDialog.Builder(getActivity())
                                    .setMessage(getResources().getString(R.string.maintenance_error))
                                    .setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dlg2, int which) {
                                            dlg2.cancel();
                                        }
                                    });
                            if(subDialog!=null) {
                                dialog = subDialog.create();
                                dialog.show();
                            }
                            if(dialog!=null) {
                                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                                positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                positiveButtonLL.gravity = Gravity.CENTER;
                                positiveButton.setLayoutParams(positiveButtonLL);

                            }
                        }
                    }
                }
                }else
                {
                    AlertDialog dialog=null;
                    AlertDialog.Builder subDialog = new AlertDialog.Builder(getActivity())
                            .setMessage(getResources().getString(R.string.network_error))
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dlg2, int which) {
                                    dlg2.cancel();
                                }
                            });
                    if(subDialog!=null) {
                        dialog = subDialog.create();
                        dialog.show();
                    }
                    if(dialog!=null) {
                        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                        positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        positiveButtonLL.gravity = Gravity.CENTER;
                        positiveButton.setLayoutParams(positiveButtonLL);

                    }
                }
            }
        });
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomerProfileFragment.class);
                startActivity(intent);
                return;
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(driverFoundID!=null && driverFoundID.length()>0)
                {
                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                    HashMap map = new HashMap();
                    map.put("status", "Cancelled");
                    driverRef.updateChildren(map);
                    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference driverRef1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                    driverRef1.removeValue();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.removeLocation(userId);
                    endRide();
                    if (historyId != null && historyId.length() > 0) {
                        DatabaseReference driverRef2 = FirebaseDatabase.getInstance().getReference().child("history").child(historyId);
                        HashMap map1 = new HashMap();
                        map1.put("status", "Cancelled");
                        driverRef2.updateChildren(map1);
                    }else
                    {
                        recordRide("Cancelled");
                    }
                    mProgressLay.setVisibility(View.GONE);
                    mRequest.setVisibility(View.VISIBLE);
                    mCancel.setVisibility(View.GONE);
                    serviceTypeAdapter.selected=false;
                    requestBol=false;
                    mRequest.setText(getResources().getString(R.string.order_now));
                }else
                {
                    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.removeLocation(userId);
                    mProgressLay.setVisibility(View.GONE);
                    mRequest.setVisibility(View.VISIBLE);
                    mCancel.setVisibility(View.GONE);
                    serviceTypeAdapter.selected=false;
                    requestBol=false;
                    if(geoQuery!=null)
                    {
                        geoQuery.removeAllListeners();
                    }
                    mRequest.setText(getResources().getString(R.string.order_now));
                    if(driverFoundID!=null && driverFoundID.length()>0)
                    {
                        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                        HashMap map = new HashMap();
                        map.put("status", "Cancelled");
                        driverRef.updateChildren(map);
                        DatabaseReference driverRef1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                        driverRef1.removeValue();
                        DatabaseReference ref1= FirebaseDatabase.getInstance().getReference("customerRequest");
                        GeoFire geoFire1 = new GeoFire(ref1);
                        geoFire1.removeLocation(userId);
                        endRide();
                    }
                }
            }
        });



       return view;

    }
    private void recordRide(String status){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
        String requestId = historyRef.push().getKey();
        customerRef.child(requestId).setValue(true);
        HashMap map = new HashMap();
        map.put("driver", "");
        map.put("customer", userId);
        map.put("rating", 0);
        map.put("status", status);
        map.put("timestamp", getCurrentTimestamp());
        map.put("pickup", pickup);
        if(pickupLatLng!=null) {
            map.put("location/from/lat", pickupLatLng.latitude);
            map.put("location/from/lng", pickupLatLng.longitude);
        }
        if (requestService.equalsIgnoreCase("1") || requestService.equalsIgnoreCase("3") || requestService.equalsIgnoreCase("2"))
        {            if (destinationLatLng != null) {
            map.put("location/to/lat", destinationLatLng.latitude);
            map.put("location/to/lng", destinationLatLng.longitude);
        }
            map.put("destination", destination);
        }
        map.put("service", requestService);
       // map.put("distance", rideDistance);
        historyRef.child(requestId).updateChildren(map);
    }
    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }
    private void setAdapter() {
        serviceTypesList=new ArrayList<>();
        //PICKUP & DROP SERVICES
        //Taxi,Two Trucks,Delivery
        //PICKUP SERVICES
        //Car Repair,Car Battery,Car Cleaning,Car Unlock,House Keeper,Oil Change,Maintenance,Wheel
        String[] servicetypes_array = getResources().getStringArray(R.array.service_types);
        serviceTypesList.add(new Services(servicetypes_array[0],R.drawable.taxi,R.drawable.taxi));
        serviceTypesList.add(new Services(servicetypes_array[1],R.drawable.delivery,R.drawable.delivery));
        serviceTypesList.add(new Services(servicetypes_array[2],R.drawable.two_trucks,R.drawable.two_trucks));
        serviceTypesList.add(new Services(servicetypes_array[3],R.drawable.power,R.drawable.power));
        serviceTypesList.add(new Services(servicetypes_array[4],R.drawable.plumbing,R.drawable.plumbing));
        serviceTypesList.add(new Services(servicetypes_array[5],R.drawable.housekeeper,R.drawable.housekeeper));
        serviceTypesList.add(new Services(servicetypes_array[6],R.drawable.petrol,R.drawable.petrol));

        serviceTypesList.add(new Services(servicetypes_array[7],R.drawable.wheel,R.drawable.wheel));
        serviceTypesList.add(new Services(servicetypes_array[8],R.drawable.oil_change,R.drawable.oil_change));
        serviceTypesList.add(new Services(servicetypes_array[9],R.drawable.car_cleaning,R.drawable.car_cleaning));
        serviceTypesList.add(new Services(servicetypes_array[10],R.drawable.car_battery,R.drawable.car_battery));
        serviceTypesList.add(new Services(servicetypes_array[11],R.drawable.car_unlock,R.drawable.car_unlock));
        serviceTypesList.add(new Services(servicetypes_array[12],R.drawable.car_repair,R.drawable.car_repair));
        serviceTypesList.add(new Services(servicetypes_array[13],R.drawable.water_trucks,R.drawable.water_trucks));
        serviceTypeAdapter=new ServiceTypesAdapter(getActivity(),serviceTypesList);
        rcServices.setAdapter(serviceTypeAdapter);
        serviceTypeAdapter.setOnItemClickListener(this);
        serviceTypeAdapter.selectedItem=0;
        rcServices.setHasFixedSize(true);

    }

    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;

    GeoQuery geoQuery;
    private void getClosestDriver(){
        driverFound=false;
        driverFoundID="";
        radius=1;
        mCancel.setVisibility(View.VISIBLE);
        mRequest.setVisibility(View.GONE);
        serviceTypeAdapter.selected=true;
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestBol){
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (driverFound){
                                    return;
                                }

                                boolean service=false;
                                if(driverMap.containsKey("service") ){
                                    String mService = driverMap.get("service").toString();
                                    if(mService.contains(","))
                                    {
                                        String[] split=mService.split(",");
                                        for (int j = 0; j < split.length; j++) {
                                           if(requestService.equalsIgnoreCase(split[j]))
                                           {
                                             service=true;
                                           }
                                        }
                                    }else if(mService.length()>0){
                                        if(mService.equalsIgnoreCase(requestService))
                                        {
                                            service=true;
                                        }
                                    }
                                    if(service) {

                                        driverFound = true;
                                        driverFoundID = dataSnapshot.getKey();
                                        if(requestBol) {
                                            sendFcmPushToDriver(driverFoundID);
                                        }else
                                        {
                                            driverFound = false;
                                            driverFoundID ="";
                                            geoQuery.removeAllListeners();
                                            mProgressLay.setVisibility(View.GONE);
                                            mRequest.setVisibility(View.VISIBLE);
                                            mCancel.setVisibility(View.GONE);
                                            serviceTypeAdapter.selected=false;

                                        }
                                    }

                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound)
                {
                    if(radius<10) {
                        radius++;
                        getClosestDriver();
                    }else
                    {
                        geoQuery.removeAllListeners();
                        endRide();
                        if(getActivity()!=null) {
                            AlertDialog dialog = null;
                            AlertDialog.Builder subDialog = new AlertDialog.Builder(getActivity())
                                    .setMessage(getResources().getString(R.string.no_pvdr_fnd))
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
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    private DatabaseReference driveReqRef;
    private ValueEventListener driveReqStatusRefListener;
    private void getAssignedDriver(String driverFoundID, String customerId){
        if(driveReqRef!=null && driveReqStatusRefListener!=null)
        {
            driveReqRef.removeEventListener(driveReqStatusRefListener);
        }
        driveReqRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");

        driveReqStatusRefListener=driveReqRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("status")!=null && map.get("status").toString().equalsIgnoreCase("accepted")){
                     if(mPickUpView.getVisibility()==View.GONE)
                     {
                         String distance = "";
                         if (map.containsKey("distance") && map.get("distance") != null) {
                             distance = map.get("distance").toString();
                         }
                         if (map.containsKey("duration") && map.get("duration") != null) {
                             distance += " " + map.get("duration").toString() + " ";
                         }
                         mDistance.setText(distance);
                     }
                     else {
                         if (map.get("historyId") != null) {
                             historyId = map.get("historyId").toString();
                         }

                         String distance = "";
                         if (map.containsKey("distance") && map.get("distance") != null) {
                             distance = map.get("distance").toString();
                         }
                         if (map.containsKey("duration") && map.get("duration") != null) {
                             distance += " " + map.get("duration").toString() + " ";
                         }
                         mDistance.setText(distance);
                         getDriverInfo();
                         getHasRideEnded();
                         mRequest.setVisibility(View.GONE);
                         rcServices.setVisibility(View.GONE);
                         if (pickupMarker != null) {
                             pickupMarker.remove();
                         }
                         if (dropMarker != null) {
                             dropMarker.remove();
                         }
                         mPickUpView.setVisibility(View.GONE);
                         mDropView.setVisibility(View.GONE);
                         mCancel.setVisibility(View.VISIBLE);
                         serviceTypeAdapter.selected=true;
                         mTxtOnBoard.setVisibility(View.GONE);
                     }
                    } else if(map.get("status")!=null && map.get("status").toString().equalsIgnoreCase("PickUp Done")){

                        String distance="";
                        if(map.containsKey("distance") && map.get("distance")!=null )
                        {
                            distance= map.get("distance").toString();
                        }
                        if(map.containsKey("duration") && map.get("duration")!=null )
                        {
                            distance+= " "+map.get("duration").toString()+" ";
                        }
                        mDistance.setText(distance);
                        mCancel.setVisibility(View.GONE);
                        serviceTypeAdapter.selected=false;
                        mTxtOnBoard.setVisibility(View.VISIBLE);

                    }/* else if(map.get("status")!=null && map.get("status").toString().equalsIgnoreCase("Completed")){

                     endRide();


                    }*/else if(map.get("status")!=null && map.get("status").toString().equalsIgnoreCase("rejected")){
                        endRide();
                        mCancel.setVisibility(View.GONE);
                        serviceTypeAdapter.selected=false;
                        mRequest.setVisibility(View.VISIBLE);
                        if(serviceType.equalsIgnoreCase("1")|| serviceType.equalsIgnoreCase("2")|| serviceType.equalsIgnoreCase("3")) {
                            mPickUpView.setVisibility(View.VISIBLE);
                            mDropView.setVisibility(View.VISIBLE);
                        }else
                        {
                            mPickUpView.setVisibility(View.VISIBLE);
                        }
                        rcServices.setVisibility(View.VISIBLE);
                        mRequest.setText(getResources().getString(R.string.order_now));
                        if(getActivity()!=null) {
                            AlertDialog dialog=null;
                            AlertDialog.Builder subDialog = new AlertDialog.Builder(getActivity())
                                    .setMessage(getResources().getString(R.string.no_pvdr_fnd))
                                    .setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dlg2, int which) {
                                            dlg2.cancel();
                                        }
                                    });
                            if(subDialog!=null) {
                                dialog = subDialog.create();
                               dialog.show();
                            }
                            if(dialog!=null) {
                                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                                positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                positiveButtonLL.gravity = Gravity.CENTER;
                                positiveButton.setLayoutParams(positiveButtonLL);

                            }
                        }

                       // Toast.makeText(getActivity(), "No Service Provider found at this time", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    /*-------------------------------------------- Map specific functions -----
    |  Function(s) getDriverLocation
    |
    |  Purpose:  Get's most updated driver location and it's always checking for movements.
    |
    |  Note:
    |	   Even tho we used geofire to push the location of the driver we can use a normal
    |      Listener to get it's location with no problem.
    |
    |      0 -> Latitude
    |      1 -> Longitudde
    |
    *-------------------------------------------------------------------*/
    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    private void getDriverLocation(){
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat,locationLng);
                    if(mDriverMarker != null){
                        mDriverMarker.remove();
                    }
                   /* Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);
                    mDistance.setText(""+distance);*/
                   if(mMap!=null) {

                       mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("your driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));

                       CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(driverLatLng, 16);
                       if (mMap != null) {
                           mMap.animateCamera(cameraUpdate);
                       }
                   }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /*-------------------------------------------- getDriverInfo -----
    |  Function(s) getDriverInfo
    |
    |  Purpose:  Get all the user information that we can get from the user's database.
    |
    |  Note: --
    |
    *-------------------------------------------------------------------*/
    private void getDriverInfo(){
        mDriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.hasChild("name") && dataSnapshot.child("name")!=null){
                        mDriverName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if(dataSnapshot.hasChild("phone") &&dataSnapshot.child("phone")!=null){
                        mDriverPhone.setText(dataSnapshot.child("phone").getValue().toString());
                    }

                    if(dataSnapshot.hasChild("profileImageUrl") &&dataSnapshot.child("profileImageUrl")!=null){
                        Glide.with(getActivity()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(mDriverProfileImage);
                    }else
                    {
                        Glide.with(getActivity()).load(R.drawable.default_image).into(mDriverProfileImage);

                    }

                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if(ratingsTotal!= 0){
                        ratingsAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingsAvg);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    private void getHasRideEnded(){
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest").child("customerRideId");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void endRide(){
        try {
            mTxtOnBoard.setVisibility(View.GONE);
        requestBol = false;
        geoQuery.removeAllListeners();
        if(driverLocationRef!=null) {
            driverLocationRef.removeEventListener(driverLocationRefListener);
        }
        if(driveHasEndedRef!=null) {
            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
        }
        if(driveReqRef!=null && driveReqStatusRefListener!=null)
        {
            driveReqRef.removeEventListener(driveReqStatusRefListener);
        }
       /* if (driverFoundID != null){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
            driverRef.removeValue();
            driverFoundID = null;

        }*/
        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if(pickupMarker != null){
            pickupMarker.remove();
            pickupMarker=null;
        }
        if(dropMarker != null){
            dropMarker.remove();
            dropMarker=null;
        }
        if (mDriverMarker != null){
            mDriverMarker.remove();
            mDriverMarker=null;
        }
        mRequest.setText(getResources().getString(R.string.order_now));
        mRequest.setVisibility(View.VISIBLE);
            mCancel.setVisibility(View.GONE);
            serviceTypeAdapter.selected=false;
        if(serviceType.equalsIgnoreCase("1")|| serviceType.equalsIgnoreCase("2")|| serviceType.equalsIgnoreCase("3")) {
            mPickUpView.setVisibility(View.VISIBLE);
            mDropView.setVisibility(View.VISIBLE);
        }else
        {
            mPickUpView.setVisibility(View.VISIBLE);
        }
        rcServices.setVisibility(View.VISIBLE);
        mDriverInfo.setVisibility(View.GONE);
        mDriverName.setText("");
        mDriverPhone.setText("");
        mDistance.setText("");
        mDriverProfileImage.setImageResource(R.mipmap.ic_default_user);
        mTxtPickup.setText("");
        mTxtDrop.setText("");
        mImgResetPick.setVisibility(View.INVISIBLE);
        mImgResetDrop.setVisibility(View.INVISIBLE);
        pickupLatLng=null;
        destinationLatLng=null;
        pickupSel=true;
        mPickUpLay.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.white_color));
        mDropLay.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.desc_color));
        if(serviceTypeAdapter!=null && serviceTypeAdapter.selectedItem==0)
        {
            serviceTypeAdapter.notifyDataSetChanged();
        }
        if(mMap!=null)
        {
            mMap.clear();
        }
        mDriverInfo.setVisibility(View.GONE);
        if(mLastLocation!=null && mMap!=null)
        {
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
            mMap.animateCamera(cameraUpdate);
        }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*-------------------------------------------- Map specific functions -----
    |  Function(s) onMapReady, buildGoogleApiClient, onLocationChanged, onConnected
    |
    |  Purpose:  Find and update user's location.
    |
    |  Note:
    |	   The update interval is set to 1000Ms and the accuracy is set to PRIORITY_HIGH_ACCURACY,
    |      If you're having trouble with battery draining too fast then change these to lower values
    |
    |
    *-------------------------------------------------------------------*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        if(mMap!=null)
        {
            mMap.setOnMapClickListener(this);
        }
        if(mMap!=null)
        {
            mMap.clear();
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(getActivity()!=null){


            if(mLastLocation==null && location!=null) {
                mLastLocation = location;

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                mMap.animateCamera(cameraUpdate);

            }else if(mLastLocation!=null && location!=null)
            {
                if (mLastLocation.getLatitude() != location.getLatitude()) {
                    mLastLocation = location;

                }
            }
        }
        /*if(mLastLocation!=null && !neardriverrequest)
        {
            getNearestDrivers();
        }*/
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    /*-------------------------------------------- onRequestPermissionsResult -----
    |  Function onRequestPermissionsResult
    |
    |  Purpose:  Get permissions for our app if they didn't previously exist.
    |
    |  Note:
    |	requestCode: the nubmer assigned to the request that we've made. Each
    |                request has it's own unique request code.
    |
    *-------------------------------------------------------------------*/
    final int LOCATION_REQUEST_CODE = 1;
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mapFragment.getMapAsync(this);

                } else {
                    Toast.makeText(getActivity(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==20)
        {
            if(data!=null)
            {
                LatLng loc=new LatLng(data.getDoubleExtra("lat",0.0),data.getDoubleExtra("lon",0.0));
                pickupLatLng=loc;
               if(pickupMarker!=null)
               {
                   pickupMarker.setPosition(pickupLatLng);
                   getStaticAddress(mTxtPickup,pickupLatLng,"pickup");
                   mPickUpLay.setVisibility(View.VISIBLE);
                   CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pickupLatLng, 16);
                   mMap.animateCamera(cameraUpdate);
                   mImgResetPick.setVisibility(View.VISIBLE);
               }else
               {
                   pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup Here").icon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN)));
                   CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pickupLatLng, 16);
                   mMap.animateCamera(cameraUpdate);
                   getStaticAddress(mTxtPickup,pickupLatLng,"pickup");
                   mPickUpLay.setVisibility(View.VISIBLE);
                   mImgResetPick.setVisibility(View.VISIBLE);

               }
            }
        }
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                if(dropMarker!=null)
                {
                    destinationLatLng = place.getLatLng();
                    dropMarker.setPosition(destinationLatLng);
                    mTxtDrop.setText(place.getAddress());
                    mDropLay.setVisibility(View.VISIBLE);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(destinationLatLng, 16);
                    mMap.animateCamera(cameraUpdate);
                    mImgResetPick.setVisibility(View.VISIBLE);
                }else
                {
                    destinationLatLng = place.getLatLng();
                    dropMarker = mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Drop Here"));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(destinationLatLng, 16);
                    mMap.animateCamera(cameraUpdate);
                    mTxtDrop.setText(place.getAddress());
                    mImgResetDrop.setVisibility(View.VISIBLE);
                    mDropLay.setVisibility(View.VISIBLE);
                    mImgResetPick.setVisibility(View.VISIBLE);

                }
            }
        }

    }
    @Override
    public void onMapClick(LatLng latLngg) {
        if(mCancel.getVisibility()!=View.VISIBLE) {

            if (pickupSel) {
                if (pickupMarker != null) {
                    pickupLatLng = latLngg;
                    pickupMarker.setPosition(pickupLatLng);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pickupLatLng, 16);
                    mMap.animateCamera(cameraUpdate);
                    getStaticAddress(mTxtPickup, pickupLatLng, "pickup");
                } else {
                    pickupLatLng = latLngg;
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup Here").icon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN)));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pickupLatLng, 16);
                    mMap.animateCamera(cameraUpdate);
                    getStaticAddress(mTxtPickup, pickupLatLng, "pickup");
                    mImgResetPick.setVisibility(View.VISIBLE);
                }

            } else {
                if (dropMarker != null) {
                    destinationLatLng = latLngg;
                    dropMarker.setPosition(destinationLatLng);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(destinationLatLng, 16);
                    mMap.animateCamera(cameraUpdate);
                    getStaticAddress(mTxtDrop, destinationLatLng, "drop");
                } else {
                    destinationLatLng = latLngg;
                    dropMarker = mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Drop Here"));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(destinationLatLng, 16);
                    mMap.animateCamera(cameraUpdate);
                    getStaticAddress(mTxtDrop, destinationLatLng, "drop");
                    mImgResetDrop.setVisibility(View.VISIBLE);
                }
            }
        }

    }
    public  void getStaticAddress(TextView txt, LatLng latLng,String type) {
       try {
           List<Address> addresses = new Geocoder(getActivity(), Locale.getDefault()).getFromLocation(latLng.latitude, latLng.longitude, 1);
            StringBuilder strReturnedAddress = new StringBuilder(BuildConfig.FLAVOR);
            if (addresses.size()>0) {


            Address returnedAddress = (Address) addresses.get(0);

                strReturnedAddress.append(returnedAddress.getAddressLine(0));

            }

            if(strReturnedAddress.toString().equalsIgnoreCase("")) {
/*

                    LocationInfoAsyncTask runner = new LocationInfoAsyncTask();
                    runner.execute(myLocation.latitude + "," + myLocation.longitude);
*/
                txt.setText("Getting adress");
                if(type.equalsIgnoreCase("pickup"))
                {

                    PickUpLocationInfoAsyncTask runner = new PickUpLocationInfoAsyncTask();
                    runner.execute("https://maps.google.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude +  "&sensor=true&key="+getResources().getString(R.string.google_maps_key),type);

                }else {
                    LocationInfoAsyncTask runner = new LocationInfoAsyncTask();
                    runner.execute("https://maps.google.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true&key="+getResources().getString(R.string.google_maps_key), type);
                }

        } else {
                if(type.equalsIgnoreCase("pickup"))
                {
                    pickup=strReturnedAddress.toString();
                }else
                {
                    destination=strReturnedAddress.toString();

                }
                txt.setText(strReturnedAddress.toString());
            }


        } catch (IOException e) {
            e.printStackTrace();
           txt.setText("Getting adress");
           if(type.equalsIgnoreCase("pickup"))
           {

               PickUpLocationInfoAsyncTask runner = new PickUpLocationInfoAsyncTask();
               runner.execute("https://maps.google.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude +  "&sensor=true&key="+getResources().getString(R.string.google_maps_key),type);

           }else {
               LocationInfoAsyncTask runner = new LocationInfoAsyncTask();
               runner.execute("https://maps.google.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true&key="+getResources().getString(R.string.google_maps_key), type);
           }
        }
    }



    private void sendFcmPushToDriver(String driverId)
    {
       DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.orderByKey().equalTo(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Token token=postSnapShot.getValue(Token.class);
                            //raw payload
                            Data data=new Data("ServeMe","New Request available");
                            Sender content=new Sender(token.getToken(),data);
                            mService.sendMessage(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                            if(response.isSuccessful()) {
                                                if(requestBol) {
                                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                    HashMap map = new HashMap();
                                                    map.put("customerRideId", customerId);
                                                    map.put("pickup", pickup);
                                                    if (destination != null && destination.length() > 0) {
                                                        map.put("destination", destination);
                                                    } else {
                                                        map.put("destination", mTxtDrop.getText().toString());
                                                    }
                                                    map.put("status", "Pending");
                                                    map.put("service", requestService);
                                                    if (destinationLatLng != null) {
                                                        map.put("destinationLat", destinationLatLng.latitude);
                                                        map.put("destinationLng", destinationLatLng.longitude);
                                                    }
                                                    driverRef.updateChildren(map);
                                                    getAssignedDriver(driverFoundID, customerId);
                                                    getDriverLocation();
                                                }else
                                                {
                                                        driverFound = false;
                                                        driverFoundID ="";
                                                        geoQuery.removeAllListeners();
                                                    mProgressLay.setVisibility(View.GONE);
                                                    mRequest.setVisibility(View.VISIBLE);
                                                    mCancel.setVisibility(View.GONE);
                                                    serviceTypeAdapter.selected=false;
                                                }
                                             }
                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            endRide();
                                        }
                                    });
                      }


                    }

                   @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void OnSelecetd(int pos, String type) {


        if(pos==-1)
        {
            serviceTypePos=pos;
            serviceType="";
            mRequest.setVisibility(View.GONE);
        }else {
            serviceTypePos=pos+1;
            serviceType=""+serviceTypePos;
            mRequest.setVisibility(View.VISIBLE);
            if (serviceType.equalsIgnoreCase("1") || serviceType.equalsIgnoreCase("3") || serviceType.equalsIgnoreCase("2"))
            {                mDropView.setVisibility(View.VISIBLE);

            } else {
                mDropView.setVisibility(View.GONE);
            }
        }
    }

    public  class LocationInfoAsyncTask extends AsyncTask<String, String, String> {

        private String resp;
        private JSONObject location;
        private Address addr1;
        private String type="";
        @Override
        protected String doInBackground(String... params) {
            type="";
            type = params[1];
            try {
                JSONObject obj = getLocationInfo(params[0]);
                if(obj!=null) {
                    resp = obj.toString();
                }else
                {
                    resp="";
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonObj = new JSONObject(result);
                location = jsonObj.getJSONArray("results").getJSONObject(0);
                String mCuurentAddress = location.getString("formatted_address");
                addr1 = new Address(Locale.ENGLISH);
                JSONArray addrComp = (JSONArray) location.getJSONArray("address_components");

                for (int i = 1; i < addrComp.length(); i++) {
                    //Toast.makeText(getApplicationContext(),addrComp.toString(),Toast.LENGTH_LONG).show();
                    // Log.d("Geocoder",addrComp.toString());

                    String locality = ((JSONArray) ((JSONObject) addrComp.get(i)).get("types")).getString(0);
                    if (locality.compareTo("locality") == 0) {
                        String locality1 = ((JSONObject) addrComp.get(i)).getString("long_name");
                        addr1.setLocality(locality1);
                    }
                }

                    mTxtDrop.setText(mCuurentAddress);
                    destination=mCuurentAddress;


            } catch (JSONException e1) {
                e1.printStackTrace();

            }

        }


    }

    /**
     * A method to download json data from url
     */
    private JSONObject getLocationInfo(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            //Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        JSONObject jsonObject = null;
        try
        {
            jsonObject = new JSONObject(data.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public  class PickUpLocationInfoAsyncTask extends AsyncTask<String, String, String> {

        private String resp;
        private JSONObject location;
        private Address addr1;
        private String type="";
        @Override
        protected String doInBackground(String... params) {
            type="";
            type = params[1];
            try {
                JSONObject obj = getLocationInfo(params[0]);
                if(obj!=null) {
                    resp = obj.toString();
                }else
                {
                    resp="";
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonObj = new JSONObject(result);
                location = jsonObj.getJSONArray("results").getJSONObject(0);
                String mCuurentAddress = location.getString("formatted_address");
                addr1 = new Address(Locale.ENGLISH);
                JSONArray addrComp = (JSONArray) location.getJSONArray("address_components");

                for (int i = 1; i < addrComp.length(); i++) {
                    //Toast.makeText(getApplicationContext(),addrComp.toString(),Toast.LENGTH_LONG).show();
                    // Log.d("Geocoder",addrComp.toString());

                    String locality = ((JSONArray) ((JSONObject) addrComp.get(i)).get("types")).getString(0);
                    if (locality.compareTo("locality") == 0) {
                        String locality1 = ((JSONObject) addrComp.get(i)).getString("long_name");
                        addr1.setLocality(locality1);
                    }
                }

                    mTxtPickup.setText(mCuurentAddress);
                    pickup=mCuurentAddress;


            } catch (JSONException e1) {
                e1.printStackTrace();

            }

        }


    }

}
