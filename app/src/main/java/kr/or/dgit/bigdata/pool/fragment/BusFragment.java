package kr.or.dgit.bigdata.pool.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class BusFragment extends Fragment{

    private String http = "http://192.168.0.12:8080/pool";


    public static BusFragment newInstance(){
        BusFragment cf = new BusFragment();
        return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.map_bus, container, false);

        return root;
    }


}
