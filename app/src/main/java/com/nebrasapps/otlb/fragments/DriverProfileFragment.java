package com.nebrasapps.otlb.fragments;

/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */



import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.nebrasapps.otlb.R;
import com.nebrasapps.otlb.RiderHomeActivity;
import com.nebrasapps.otlb.ServiceProviderHome;
import com.nebrasapps.otlb.ServiceSelectionAdapter;
import com.nebrasapps.otlb.dialogs.DialogList;
import com.nebrasapps.otlb.pojo.Services;
import com.nebrasapps.otlb.storage.SharedData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

public class DriverProfileFragment extends Fragment {

    private EditText mNameField, mPhoneField, mEmailField;

    private Button mConfirm;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;

    private String userID;
    private String mName;
    private String mPhone;
    private String mCar;
    private String mService;
    private String mProfileImageUrl;

    private Uri resultUri;
    private String serviceType = "";
    private RecyclerView rcServices;
    private List<Services> serviceTypesList = new ArrayList<Services>();
    private ServiceSelectionAdapter serviceTypeAdapter;
    private LinearLayout progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.servicepvdr_profile, container, false);

        progressBar = (LinearLayout) view.findViewById(R.id.lay_progress);
        mNameField = (EditText) view.findViewById(R.id.name);
        mPhoneField = (EditText) view.findViewById(R.id.phone);
        mEmailField = (EditText) view.findViewById(R.id.email);
        mProfileImage = (ImageView) view.findViewById(R.id.profileImage);
        mConfirm = (Button) view.findViewById(R.id.confirm);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);
        mEmailField.setHint(mAuth.getCurrentUser().getEmail());
        LinearLayoutManager countryLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        rcServices = (RecyclerView) view.findViewById(R.id.rc_services);
        rcServices.setLayoutManager(countryLayoutManager);
        setAdapter();
        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                saveUserInformation();
            }
        });

        return view;
    }

    private void getUserInfo() {
        //making loadingbar visible
        progressBar.setVisibility(View.VISIBLE);
        //registering listener for serviceprovider or driver info to get latest profile updates
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        mName = map.get("name").toString();
                        mNameField.setText(mName);
                        SharedData.savePref("name", mName);
                    }
                    if (map.get("phone") != null) {
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }
                    //checking the selected user services
                    if (map.get("service") != null) {
                        mService = map.get("service").toString();
                        if (mService.contains(",")) {
                            String[] split = mService.split(",");
                            for (int j = 0; j < split.length; j++) {
                                serviceTypesList.get(Integer.valueOf(split[j]) - 1).setSelected(true);
                            }
                        } else if (mService.length() > 0) {
                            serviceTypesList.get(Integer.valueOf(mService) - 1).setSelected(true);
                        }
                        if (serviceTypeAdapter != null) {
                            serviceTypeAdapter.notifyDataSetChanged();
                        }


                    }
                    if (map.get("profileImageUrl") != null) {
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        if (getActivity() != null) {
                            Glide.with(getActivity().getApplicationContext()).load(mProfileImageUrl).into(mProfileImage);
                        }
                        SharedData.savePref("img", mProfileImageUrl);

                    }
                    if(getBaseActivity()!=null) {
                        getBaseActivity().setUserData();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void selectImage() {
        List<String> list = new ArrayList<>();
        list.add("Camera");
        list.add("Gallery");
        final DialogList dialogList = new DialogList(getActivity());
        dialogList.setList(list);
        dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int which, long l) {
                // Using RxImagePicker to capture or choose images from gallery
                if (which == 0) {//take picture from camera
                    RxImagePicker.with(getActivity()).requestImage(Sources.CAMERA).subscribe(new Consumer<Uri>() {
                        @Override
                        public void accept(@NonNull Uri uri) throws Exception {
                            //Get image by uri using one of image loading libraries.using Glide in this app
                            resultUri = uri;
                            Glide.with(getActivity()).load(resultUri).into(mProfileImage);
                        }
                    });
                    dialogList.dismiss();


                } else if (which == 1) {//open gallery
                    RxImagePicker.with(getActivity()).requestImage(Sources.GALLERY).subscribe(new Consumer<Uri>() {
                        @Override
                        public void accept(@NonNull Uri uri) throws Exception {
                            //Get image by uri using one of image loading libraries.using Glide in this app
                            resultUri = uri;
                            Glide.with(getActivity()).load(resultUri).into(mProfileImage);
                        }
                    });
                    dialogList.dismiss();
                }
            }
        });

        dialogList.setPositiveButton(
                getResources().getString(R.string.cancel),
                null);
        dialogList.setListItemColor(Color.BLACK);
        dialogList.show();


    }

    private void saveUserInformation() {
        progressBar.setVisibility(View.VISIBLE);
        //updating user data
        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString();
        if (serviceType == null && serviceType.length() <= 0) {
            AlertDialog.Builder subDialog = new AlertDialog.Builder(getActivity())
                    .setMessage(getResources().getString(R.string.service_type))
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dlg2, int which) {
                            dlg2.cancel();
                        }
                    });
            subDialog.show();
            return;
        }
        mService = "";
        List<Services> serviceList = serviceTypeAdapter.serviceTypesList;
        for (int j = 0; j < serviceList.size(); j++) {
            if (serviceList.get(j).isSelected()) {
                if (mService.length() <= 0) {
                    mService = "" + (j + 1);
                } else {
                    mService += "," + (j + 1);
                }
            }
        }

        final Map userInfo = new HashMap();
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);
        userInfo.put("service", mService);

        // checking user selected new profile image
        if (resultUri != null) {
            //code for uploading image to FirebaseStorage
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    userInfo.put("profileImageUrl", downloadUrl.toString());
                    //updating user data and making loadingbar view gone
                    mDriverDatabase.updateChildren(userInfo);
                    progressBar.setVisibility(View.GONE);
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Updated successfully", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
            });
        } else {
            //updating user data and making loadingbar view gone
            mDriverDatabase.updateChildren(userInfo);
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "Updated successfully", Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
        }

    }

    private void setAdapter() {
        //hardcoded list of services
        serviceTypesList = new ArrayList<>();
        //PICKUP & DROP SERVICES
        //Taxi,Trucks,Delivery
        //PICKUP SERVICES
        //Car Repair,Car Battery,Car Cleaning,Car Unlock,House Keeper,Oil Change,Maintenance,Wheel
        String[] servicetypes_array = getResources().getStringArray(R.array.service_types);
        serviceTypesList.add(new Services(servicetypes_array[0], R.drawable.taxi, R.drawable.taxi));
        serviceTypesList.add(new Services(servicetypes_array[1], R.drawable.delivery, R.drawable.delivery));
        serviceTypesList.add(new Services(servicetypes_array[2], R.drawable.two_trucks, R.drawable.two_trucks));
        serviceTypesList.add(new Services(servicetypes_array[3], R.drawable.power, R.drawable.power));
        serviceTypesList.add(new Services(servicetypes_array[4], R.drawable.plumbing, R.drawable.plumbing));
        serviceTypesList.add(new Services(servicetypes_array[5], R.drawable.housekeeper, R.drawable.housekeeper));
        serviceTypesList.add(new Services(servicetypes_array[6], R.drawable.petrol, R.drawable.petrol));

        serviceTypesList.add(new Services(servicetypes_array[7], R.drawable.wheel, R.drawable.wheel));
        serviceTypesList.add(new Services(servicetypes_array[8], R.drawable.oil_change, R.drawable.oil_change));
        serviceTypesList.add(new Services(servicetypes_array[9], R.drawable.car_cleaning, R.drawable.car_cleaning));
        serviceTypesList.add(new Services(servicetypes_array[10], R.drawable.car_battery, R.drawable.car_battery));
        serviceTypesList.add(new Services(servicetypes_array[11], R.drawable.car_unlock, R.drawable.car_unlock));
        serviceTypesList.add(new Services(servicetypes_array[12], R.drawable.car_repair, R.drawable.car_repair));
        serviceTypesList.add(new Services(servicetypes_array[13], R.drawable.water_trucks, R.drawable.water_trucks));

        serviceTypeAdapter = new ServiceSelectionAdapter(getActivity(), serviceTypesList);
        rcServices.setAdapter(serviceTypeAdapter);
        rcServices.setHasFixedSize(true);

    }

    public void onResume() {
        super.onResume();
    }
    protected ServiceProviderHome getBaseActivity() throws NullPointerException
    {
        if(getActivity() instanceof ServiceProviderHome)
            return (ServiceProviderHome) getActivity();
        else
            return null;
    }
}
