package kr.or.dgit.bigdata.pool.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager manager;
    Marker m;
    Marker mBus;
    private String http = "http://rkd0519.cafe24.com/pool";
    int mno;
    Double lat;
    Double longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_bus);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Criteria cri = new Criteria();
        cri.setAccuracy(Criteria.ACCURACY_FINE);
        cri.setPowerRequirement(Criteria.POWER_LOW);
        cri.setAltitudeRequired(true);
        cri.setBearingRequired(false);
        cri.setSpeedRequired(false);
        cri.setCostAllowed(true);

        String provider = manager.getBestProvider(cri, true);
        manager.requestLocationUpdates(provider, 1000, 10, this);

        Location location = manager.getLastKnownLocation(provider);
        lat = location.getLatitude();
        longi = location.getLongitude();
        LatLng me = new LatLng(lat, longi);



        SharedPreferences sp = this.getSharedPreferences("member",0);

        mno = (int)sp.getInt("mno",0);
        mBus =  mMap.addMarker(new MarkerOptions().position(me).title("내위치"));
        if(mno==1){

            new HttpRequestTack(this,mHandler,"GET","위치찾는중").execute(http+"/bus/bus?map="+lat+"/"+longi);

        }else{
            CameraPosition cp = new CameraPosition.Builder().target(me).zoom(16).build();
            m = mMap.addMarker(new MarkerOptions().position(me).title("내위치"));
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
            new HttpRequestTack(this,mHandler,"GET","위치찾는중",2).execute(http+"/bus/");
        }



    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    mBus.remove();
                    String result = (String)msg.obj;
                    String[] s = result.split("/");
                    LatLng bus = new LatLng(Double.parseDouble(s[0]), Double.parseDouble(s[1]));

                    mBus = mMap.addMarker(new MarkerOptions().position(bus).title("버스위치").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));


                    CameraPosition cp = new CameraPosition.Builder().target(bus).zoom(16).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));

                }

                break;
                case 2:{
                    mBus.remove();
                    String result = (String)msg.obj;
                    String[] s = result.split("/");
                    LatLng bus = new LatLng(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
                    mBus = mMap.addMarker(new MarkerOptions().position(bus).title("버스위치").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));

                }

                break;

            }
        }
    };

    @Override
    public void onLocationChanged(Location location) {

        Criteria cri = new Criteria();
        cri.setAccuracy(Criteria.ACCURACY_FINE);

        cri.setPowerRequirement(Criteria.POWER_LOW);

        cri.setAltitudeRequired(true);

        cri.setBearingRequired(false);

        cri.setSpeedRequired(false);

        cri.setCostAllowed(true);
        String provider = manager.getBestProvider(cri, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = manager.getLastKnownLocation(provider);
        LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
        lat = location.getLatitude();
        longi = location.getLongitude();
        if(mno==1){
            new HttpRequestTack(this,mHandler,"GET","위치찾는중",1).execute(http+"/bus/bus?map="+lat+"/"+longi);
        }else{
            m.remove();
            m = mMap.addMarker(new MarkerOptions().position(me).title("내위치"));
            new HttpRequestTack(this,mHandler,"GET","위치찾는중",2).execute(http+"/bus/");
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
