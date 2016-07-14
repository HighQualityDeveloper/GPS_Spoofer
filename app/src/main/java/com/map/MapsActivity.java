package com.guardianmap;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
ConnectionDetector connectionDetector;
    Integer[] markericon={R.drawable.marker1,R.drawable.marker2,R.drawable.marker3,R.drawable.marker4,R.drawable.marker5,
            R.drawable.marker6,R.drawable.marker7,R.drawable.marker8,R.drawable.marker9,R.drawable.marker10};
    List<Marker> markersList = new ArrayList<Marker>();
    LatLngBounds.Builder builder;
    CameraUpdate cu;

    // 160606
    String lastResponse="";
    CountDownTimerKroid cTimerK;

    @Override
    protected void onPause() {
        super.onPause();
        if(cTimerK != null) {
            cTimerK.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cTimerK != null) {
            cTimerK.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cTimerK != null) {
            cTimerK.cancel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        connectionDetector=new ConnectionDetector(MapsActivity.this);
        if(connectionDetector.isConnectingToInternet())
        {
            GetData(true);
        }
        else
        {
            Toast.makeText(MapsActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

    }
    public void GetData(final boolean isShowProgress){
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(MapsActivity.this, "http://URL", new AsyncHttpResponseHandler() {
            ProgressDialog progressDialog=new ProgressDialog(MapsActivity.this);
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response=new String(responseBody);
Log.i("Response",""+response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("success").equals("1") && jsonObject.has("device")) {
                        Log.i("Length",""+jsonObject.getJSONArray("device").length());
                        if (!lastResponse.equals(response)) {
                            lastResponse = response;
                            markersList.clear();
                            mMap.clear();
                            JSONArray jsonArray = jsonObject.getJSONArray("device");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                Double lat = Double.parseDouble(jsonObject1.getString("latitude"));
                                Double lng = Double.parseDouble(jsonObject1.getString("longitude"));
                                LatLng sydney = new LatLng(lat, lng);

                                if(i>=10)
                                {
                                    int idx = new Random().nextInt(markericon.length);
                                   Marker m = mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(markericon[idx])).title(jsonObject1.getString("deviceid")));
                                    markersList.add(m);
                                }
                                else {
                                   Marker  m = mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(markericon[i])).title(jsonObject1.getString("deviceid")));
                                    markersList.add(m);
                                }


                          /*  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                            mMap.animateCamera(CameraUpdateFactory.zoomBy(13f));*/

                            }
                            Log.i("Length1",""+markersList.size());
                            builder = new LatLngBounds.Builder();
                            for (Marker m : markersList) {
                                builder.include(m.getPosition());
                            }
                            /**initialize the padding for map boundary*/
                            int padding = 50;
                            /**create the bounds from latlngBuilder to set into map camera*/
                            LatLngBounds bounds = builder.build();
                            /**create the camera with bounds and padding to set into map*/
                            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                @Override
                                public void onMapLoaded() {
                                    /**set animated zoom camera into map*/
                                    mMap.animateCamera(cu);

                                }
                            });



                        }
                    } else {
                        Log.i("gk test", "no change");
                        //Toast.makeText(MapsActivity.this, "no change", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
              //  if (cTimerK!=null) {
                    startTimerK3(8000);//5sec
              //  }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onStart() {
                super.onStart();
                progressDialog.setMessage("Please Wait...");
                if (isShowProgress) {
                    progressDialog.show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void startTimerK3(long tmp) {
        if (cTimerK != null) {
            cTimerK.cancel();
        }
        Log.i("gk test", "start timer");
        cTimerK = null;
        cTimerK = new CountDownTimerKroid(tmp, 1000, true) {
            @Override
            public void onTick(long tick) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                Log.i("gk test","timer finish");
                cTimerK = null;
                if(connectionDetector.isConnectingToInternet()) {
                    GetData(false);
                }
                else
                {
                    startTimerK3(8000);//5sec
                }
            }
        };
        cTimerK.create();
    }//startAdTimerK3


}
