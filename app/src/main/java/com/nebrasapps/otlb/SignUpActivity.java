package com.nebrasapps.otlb;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nebrasapps.otlb.pojo.Services;
import com.nebrasapps.otlb.storage.SharedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */


public class SignUpActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private TextView mLogin, mRegistration;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private Spinner muserTypes;
    private String role="";
    private LinearLayout progressBar;
    private TextView mTitle;
    private RadioButton customer;
    private RadioButton driver;

    private EditText mNameField;
    private EditText mPhoneField;
    private String mService="";
    private RadioGroup userOptions;
    private TextView mserviceTypes;
    private RecyclerView rcServices;
    private List<Services> serviceTypesList=new ArrayList<Services>();
    private ServiceSelectionAdapter serviceTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        new SharedData(this);
        mAuth = FirebaseAuth.getInstance();
        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.mobile);
        customer=(RadioButton)findViewById(R.id.customer);
        driver=(RadioButton)findViewById(R.id.driver);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        muserTypes = (Spinner) findViewById(R.id.userType);
        mserviceTypes = (TextView) findViewById(R.id.serviceType);
        mRegistration = (TextView) findViewById(R.id.registration);
        progressBar = (LinearLayout) findViewById(R.id.lay_progress);
        userOptions=(RadioGroup)findViewById(R.id.userOptions);
        LinearLayoutManager countryLayoutManager =new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
        rcServices = (RecyclerView) findViewById(R.id.rc_services);
        rcServices.setLayoutManager(countryLayoutManager);
        setAdapter();
        progressBar.setVisibility(View.GONE);
        userOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.customer)
                {
                    mserviceTypes.setVisibility(View.GONE);
                    rcServices.setVisibility(View.GONE);
                }else  if(i==R.id.driver)
                {
                    mserviceTypes.setVisibility(View.VISIBLE);
                    rcServices.setVisibility(View.VISIBLE);
                }
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mNameField.getText().toString();
                final String mobile = mPhoneField.getText().toString();
                if(email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Please enter email & password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mService="";
               List<Services> serviceList=serviceTypeAdapter.serviceTypesList;
                if(driver.isChecked()) {
                    if (serviceList.size() <= 0) {
                        AlertDialog dialog=null;
                        AlertDialog.Builder subDialog = new AlertDialog.Builder(SignUpActivity.this)
                                .setMessage("Please select atleast one service")
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
                for (int j=0;j<serviceList.size();j++)
                {
                    if(serviceList.get(j).isSelected()) {
                        if (mService.length() <= 0) {
                            mService = ""+(j+1);
                        } else {
                            mService += "," + (j+1);
                        }
                    }
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }else{
                            if(customer.isChecked()) {

                                String user_id = mAuth.getCurrentUser().getUid();

                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);
                                current_user_db.setValue(true);
                                Map userInfo = new HashMap();
                                userInfo.put("name", name);
                                userInfo.put("phone", mobile);
                                current_user_db.updateChildren(userInfo);
                                SharedData.savePref("role", "customer");
                                SharedData.savePref("name", name);
                                role = "customer";
                                Intent intent = new Intent(SignUpActivity.this, RiderHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else
                            {
                                String user_id = mAuth.getCurrentUser().getUid();

                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id);
                                current_user_db.setValue(true);
                                Map userInfo = new HashMap();
                                userInfo.put("name", name);
                                userInfo.put("phone", mobile);
                                userInfo.put("service", mService);
                                SharedData.savePref("name", name);
                                current_user_db.updateChildren(userInfo);
                                SharedData.savePref("role","driver");
                                role="driver";
                                Intent intent = new Intent(SignUpActivity.this, ServiceProviderHome.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });


    }
    private void setAdapter() {
        serviceTypesList=new ArrayList<>();
        //PICKUP & DROP SERVICES
        //Taxi,Truck,Delivery
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

        serviceTypeAdapter=new ServiceSelectionAdapter(this,serviceTypesList);
        rcServices.setAdapter(serviceTypeAdapter);
        rcServices.setHasFixedSize(true);

    }


}
