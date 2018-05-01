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

public class ReclassFragment extends Fragment{

    private String http = "http://rkd0519.cafe24.com/pool";
    int tno;
    String level;
    DonutView view;
    Double percent;
    int i=0;

    public static ReclassFragment newInstance(){
        ReclassFragment cf = new ReclassFragment();
        return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.reclass, container, false);
        view= root.findViewById(R.id.c);

        SharedPreferences sp = this.getActivity().getSharedPreferences("Admin",0);
        Date d = new Date();

        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.DATE) >= 20) {
            d.setMonth(d.getMonth() + 1);
        }
        d.setDate(1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01");

        level = sdf.format(d);
        tno = (int)sp.getInt("tno",0);
        new HttpRequestTack(getContext(),mHandler,"GET","정보를 가져오고 있습니다...").execute(http+"/restregister/reclass?tno="+tno+"&level="+level);
        return root;
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    String result = (String)msg.obj;
                    view.setValue(Double.parseDouble(result.substring(0,4)));
                }

                break;
            }
        }
    };

}
