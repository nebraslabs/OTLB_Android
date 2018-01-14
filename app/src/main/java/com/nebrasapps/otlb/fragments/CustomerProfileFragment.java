package com.nebrasapps.otlb.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.nebrasapps.otlb.dialogs.DialogList;
import com.nebrasapps.otlb.storage.SharedData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;


/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */



public class CustomerProfileFragment extends Fragment {

    private EditText mNameField, mPhoneField;
    private Button mConfirm;
    private ImageView mProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private String userID;
    private String mName;
    private String mPhone;
    private String mProfileImageUrl;
    private Uri resultUri;
    private EditText mEmailField;
    private LinearLayout progressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.rider_profile, container, false);
        //views initialization
        progressBar = (LinearLayout) view.findViewById(R.id.lay_progress);
        mNameField = (EditText) view.findViewById(R.id.name);
        mPhoneField = (EditText) view.findViewById(R.id.phone);
        mEmailField = (EditText) view.findViewById(R.id.email);
        mProfileImage = (ImageView) view.findViewById(R.id.profileImage);
        mConfirm = (Button) view.findViewById(R.id.confirm);


        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);
        mEmailField.setHint(mAuth.getCurrentUser().getEmail());
        //getting  userinfo based on loggedinuser
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
                saveUserInformation();
            }
        });
        return view;

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

    private void getUserInfo() {
        //making loadingbar visible
        progressBar.setVisibility(View.VISIBLE);
        //registering listener for user info to get latest profile updates
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    //updating data in ui
                    if (map.get("name") != null) {
                        mName = map.get("name").toString();
                        mNameField.setText(mName);
                        SharedData.savePref("name", mName);
                    }
                    if (map.get("phone") != null) {
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
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
                    //making loadingbar visibility gone
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //making loadingbar visibility gone
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void saveUserInformation() {
        progressBar.setVisibility(View.VISIBLE);
        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString();
        //updating user data
        final Map userInfo = new HashMap();
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);
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
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    userInfo.put("profileImageUrl", downloadUrl.toString());
                    //updating user data and making loadingbar view gone
                    mCustomerDatabase.updateChildren(userInfo);
                    progressBar.setVisibility(View.GONE);
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Updated successfully", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
            });
        } else {
            // progressbar gone
            progressBar.setVisibility(View.GONE);
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "Updated successfully", Toast.LENGTH_LONG).show();
            }

        }

    }
    protected RiderHomeActivity getBaseActivity() throws NullPointerException
    {
        if(getActivity() instanceof RiderHomeActivity)
            return (RiderHomeActivity) getActivity();
        else
            return null;
    }

}
