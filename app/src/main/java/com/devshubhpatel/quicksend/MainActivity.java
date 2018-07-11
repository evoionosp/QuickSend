package com.devshubhpatel.quicksend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import butterknife.ButterKnife;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    public static Toolbar mToolbar;
    public static Realm realm;
    String TAG = "MainActivity";
    public static InterstitialAd interstitialAd;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        initViewPagerAndTabs();
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);


        realm = Realm.getDefaultInstance();
        interstitialAd = new InterstitialAd(this);
        MobileAds.initialize(this, getString(R.string.banner_ad_unit_id2));
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        //interstitialAd.loadAd(new AdRequest.Builder().build()); --remind to turn on
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }

    private void initViewPagerAndTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        final FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabAdd.hide();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,EditReminderActivity.class));
            }
        });
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new SendFragment(), getString(R.string.send));;
        pagerAdapter.addFragment(new ReminderFragment(), getString(R.string.reminders));
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    fabAdd.show();
                    imm.hideSoftInputFromWindow(fabAdd.getWindowToken(), 0);
                }else {
                    fabAdd.hide();

                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_whatsapp) {

            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
            } else {
                new AlertDialog.Builder(this).setMessage(
                        "Whatsapp not installed on this device.")
                        .setCancelable(false)
                        .setPositiveButton("Install",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp")));
                                        } catch (Exception e) {
                                            Log.e(TAG, "OpenPlaystore:" + e.getMessage());
                                            Snackbar.make(mToolbar, "Google Playstore Error", Snackbar.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        //dialog.cancel();
                                    }
                                }).create().show();
            }
            return true;
        }
        if (id == R.id.action_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "QuickSend for Whatsapp");
                String sAux = "\nOften send messages to anonymous ? Try this app\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.devshubhpatel.quicksend \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Share with"));
            } catch(Exception e) {
                Log.e("SHARE APP",e.toString());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    finish();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    finish();
                }
            });
            interstitialAd.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        initViewPagerAndTabs();
        super.onResume();
    }
}
