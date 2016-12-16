package com.sujit.zelotest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_STORAGE = 112;
    private final String android_version_names[] = {
            "Donut",
            "Eclair",
            "Froyo",
            "Gingerbread",
            "Honeycomb",
            "Ice Cream Sandwich",
            "Jelly Bean",
            "KitKat",
            "Lollipop",
            "Marshmallow"
    };

    private final String android_image_urls[] = {
            "http://www.designbolts.com/wp-content/uploads/2013/11/Android-1.6-Donut.jpg",
            "http://2.bp.blogspot.com/-XmZG7XWH0yU/UJ9udUYqV3I/AAAAAAAAACI/9bW8_kU9stI/s1600/Android-Eclair.png",
            "http://www.androidheadlines.com/wp-content/uploads/2014/11/Android-2.2-Froyo.png",
            "http://img12.deviantart.net/95a0/i/2011/313/5/8/android_gingerbread_wallpaper_hd_by_tpbarratt-d4fn6cs.jpg",
            "http://compixels.com/wp-content/uploads/2011/04/Honeycomb-Wallpaper.png",
            "https://www.howitworksdaily.com/wp-content/uploads/2011/10/ice-cream-sandwich.jpg",
            "http://cdn.cultofandroid.com/wp-content/uploads/2012/06/jb_wallpaper.jpg",
            "http://hdwallpaperbackgrounds.net/wp-content/uploads/2016/08/Android-Kitkat-Wallpaper-7.jpg",
            "https://mir-s3-cdn-cf.behance.net/project_modules/disp/99790624295211.563324b82ca9c.png",
            "http://cdn01.androidauthority.net/wp-content/uploads/2015/09/android-6.0-marshmallow.jpg"

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkpermissionforstorage();
        initViews();
    }

    private void checkpermissionforstorage() {
        //ask for the permission in android M
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
           

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Permission to access the SD-CARD is required for this app to Download PDF.")
                        .setTitle("Permission required");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                       
                        makeRequest();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                makeRequest();
            }
        }

    }

    private void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_STORAGE);
    }

    private void initViews(){
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<AndroidVersion> androidVersions = prepareData();
        ImageAdapter adapter = new ImageAdapter(MainActivity.this,androidVersions);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

    }
    private ArrayList<AndroidVersion> prepareData(){

        ArrayList<AndroidVersion> android_version = new ArrayList<>();
        for(int i=0;i<android_version_names.length;i++){
            AndroidVersion androidVersion = new AndroidVersion();
            androidVersion.setAndroid_version_name(android_version_names[i]);
            androidVersion.setAndroid_image_url(android_image_urls[i]);
            android_version.add(androidVersion);
        }
        return android_version;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    finish();

                } else {



                }
                return;
            }
        }
    }
}
