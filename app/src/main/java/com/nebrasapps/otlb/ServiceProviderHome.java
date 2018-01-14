package com.nebrasapps.otlb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nebrasapps.otlb.components.CustomViewPager;
import com.nebrasapps.otlb.fragments.AboutFragment;
import com.nebrasapps.otlb.fragments.CustomerProfileFragment;
import com.nebrasapps.otlb.fragments.DriverProfileFragment;
import com.nebrasapps.otlb.fragments.HistoryFragment;
import com.nebrasapps.otlb.fragments.RiderHomeFragment;
import com.nebrasapps.otlb.fragments.ServiceProviderHomeFragm;
import com.nebrasapps.otlb.service.RegistrationIntentService;
import com.nebrasapps.otlb.storage.SharedData;
import com.nebrasapps.otlb.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */


public class ServiceProviderHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView tvTitle;
    private ImageButton imgFilter;
    private ConstraintLayout appBarLayout;
    private Menu menu;
    private DrawerLayout drawer;
    private ServiceProviderHomeFragm homeFragment;
    private ImageView imgLogo;
    private RadioGroup radioGroup;
    private TextView logoutMenu;
    private Switch mWorkingSwitch;
    private CustomViewPager viewPager;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private ImageView mImgProfile;
    private TextView mUserName;

    private BroadcastReceiver bReceiver;
    private LocalBroadcastManager bManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_home_act);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        registerBroadCast();
        Intent i=new Intent(this, RegistrationIntentService.class);
        startService(i);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        mWorkingSwitch = (Switch) findViewById(R.id.workingSwitch);
        imgLogo = (ImageView) findViewById(R.id.img_logo);
        appBarLayout = (ConstraintLayout) findViewById(R.id.appBarLayout);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        radioGroup = (RadioGroup) findViewById(R.id.menu_radio_group);
        viewPager = (CustomViewPager) findViewById(R.id.fragment_container);
        mImgProfile = (ImageView) findViewById(R.id.img_profile);
        mUserName = (TextView) findViewById(R.id.tv_user_name);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        NavigationView navigationViewBottom = (NavigationView) findViewById(R.id.navigation_drawer_bottom);
        navigationViewBottom.setItemIconTintList(null);
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(this);
        navigationViewBottom.setNavigationItemSelectedListener(this);
        logoutMenu  = (TextView) findViewById(R.id.nav_logout);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);
        viewPager.setPagingEnabled(false);
        viewPager.setCurrentItem(0);
      //  replaceFragment(homeFragment);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.nav_home) {
                    viewPager.setCurrentItem(0);
                    tvTitle.setVisibility(View.GONE);
                    imgLogo.setVisibility(View.VISIBLE);
                    mWorkingSwitch.setVisibility(View.VISIBLE);

                    // Handle the camera action
                } else if (id == R.id.nav_history) {
                    viewPager.setCurrentItem(1);
                    HistoryFragment fragm= (HistoryFragment) mFragmentList.get(1);
                    fragm.getUserHistoryIds();
                    tvTitle.setText(getResources().getString(R.string.history));
                    tvTitle.setVisibility(View.VISIBLE);
                    imgLogo.setVisibility(View.GONE);
                    mWorkingSwitch.setVisibility(View.GONE);
                } else if (id == R.id.nav_profile) {
                    viewPager.setCurrentItem(2);
                    tvTitle.setText(getResources().getString(R.string.profile));
                    tvTitle.setVisibility(View.VISIBLE);
                    imgLogo.setVisibility(View.GONE);
                    mWorkingSwitch.setVisibility(View.GONE);
                } else if (id == R.id.nav_about) {
                    viewPager.setCurrentItem(3);
                    tvTitle.setText(getResources().getString(R.string.about));
                    tvTitle.setVisibility(View.VISIBLE);
                    imgLogo.setVisibility(View.GONE);
                    mWorkingSwitch.setVisibility(View.GONE);
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
            }
        });
        logoutMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ServiceProviderHomeFragm(), "ONE");
        adapter.addFrag(new HistoryFragment(), "TWO");
        adapter.addFrag(new DriverProfileFragment(), "THREE");
        adapter.addFrag(new AboutFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(item.getItemId() == R.id.action_home) {
            if(drawer.isDrawerOpen(Gravity.END)) {
                drawer.closeDrawer(Gravity.END);
            }
            else {
                drawer.openDrawer(Gravity.END);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            replaceFragment(homeFragment);
            tvTitle.setVisibility(View.GONE);
            imgLogo.setVisibility(View.VISIBLE);
            // Handle the camera action
        } else if (id == R.id.nav_history) {
            replaceFragment(new HistoryFragment());
            tvTitle.setText(getResources().getString(R.string.history));
            tvTitle.setVisibility(View.VISIBLE);
            imgLogo.setVisibility(View.GONE);
        } else if (id == R.id.nav_profile) {
            replaceFragment(new DriverProfileFragment());
            tvTitle.setText(getResources().getString(R.string.profile));
            tvTitle.setVisibility(View.VISIBLE);
            imgLogo.setVisibility(View.GONE);
        } else if (id == R.id.nav_about) {
            replaceFragment(new AboutFragment());
            tvTitle.setText(getResources().getString(R.string.about));
            tvTitle.setVisibility(View.VISIBLE);
            imgLogo.setVisibility(View.GONE);
        }
        else if (id == R.id.nav_logout) {
            logout();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }
    public void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // builder.setTitle("Confirm");
        String msg=getResources().getString(R.string.logout);
        builder.setMessage(msg);

        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if(MainApp.isNetworkAvailable(ServiceProviderHome.this)) {
                    if (homeFragment != null) {
                        homeFragment.disconnectDriver();
                    }
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.removeLocation(userId);
                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    driverRef.removeValue();
                    FirebaseAuth.getInstance().signOut();
                    SharedData.reset();
                    Intent intent = new Intent(ServiceProviderHome.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else
                {
                    Toast.makeText(ServiceProviderHome.this, "No network", Toast.LENGTH_SHORT).show();

                }
            }

        });

        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
    private void replaceFragment (Fragment fragment){
        FragmentTransaction fragmentTransaction =   getSupportFragmentManager().beginTransaction();

        String backStateName = fragment.getClass().getName();



        Fragment fragment1 = getSupportFragmentManager().findFragmentByTag(backStateName);

        if(fragment1!=null && fragment.getClass().getName().equals(ServiceProviderHomeFragm.class.getName())) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }


        if(fragment1!=null&&!fragment1.getTag().equals(ServiceProviderHomeFragm.class.getName())) {
            fragmentTransaction.replace(R.id.fragment_container, fragment1, backStateName).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(backStateName);
            fragmentTransaction.commit();


        }else  {
            Fragment fr1 = getSupportFragmentManager().findFragmentByTag(ServiceProviderHomeFragm.class.getName());
            if(fr1!=null&&fr1.getTag().equals(backStateName))
                return;

            fragmentTransaction.replace(R.id.fragment_container, fragment, backStateName).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            if(!fragment.getClass().getName().equals(ServiceProviderHomeFragm.class.getName())) {

                fragmentTransaction.addToBackStack(backStateName);

            }
            fragmentTransaction.commit();


        }

    }
    public void setUserData()
    {
        String img=SharedData.getPref("img","");
        String name=SharedData.getPref("name","");

        if(img!=null && img.length()>0)
        {
            Glide.with(getApplicationContext()).load(img).into(mImgProfile);

        }else
        {
            Glide.with(getApplicationContext()).load(R.drawable.default_image).into(mImgProfile);

        }
        mUserName.setText(name);
    }

    private void registerBroadCast() {
        bReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.NEW_REQUEST)) {
                    if(viewPager.getCurrentItem()!=0)
                    {
                        viewPager.setCurrentItem(0);
                        tvTitle.setVisibility(View.GONE);
                        imgLogo.setVisibility(View.VISIBLE);
                        mWorkingSwitch.setVisibility(View.VISIBLE);

                    }
                }
            }


        };
        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.NEW_REQUEST);
        bManager.registerReceiver(bReceiver, intentFilter);

    }
}
