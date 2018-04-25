package kr.or.dgit.bigdata.pool.fragment;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.or.dgit.bigdata.pool.MainActivity;
import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.Member;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class ReclassFragment extends Fragment implements View.OnClickListener{

    private String http = "http://192.168.0.12:8080/pool";
    Spinner s;
    public static ReclassFragment newInstance(){
        ReclassFragment cf = new ReclassFragment();
        return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.reclass, container, false);
        s = root.findViewById(R.id.spinner);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               s.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return root;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_class){

        }
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    String result = (String)msg.obj;

                    try {
                        JSONArray ja = new JSONArray(result);


                    } catch (JSONException e) {
                        Log.i("Json_parser", e.getMessage());
                    }

                }
                break;
                case 2:{
                    String result = (String)msg.obj;


                    try {
                        JSONArray ja = new JSONArray(result);

                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject order = ja.getJSONObject(i);
                            Member member= new Member();
                            member.setMno((int)order.get("mno"));
                            member.setGender((String)order.get("gender"));
                            member.setName((String)order.get("name"));
                            member.setAge((int)order.get("age"));
                            member.setTell((String)order.get("tell"));

                        }
                    } catch (JSONException e) {
                        Log.i("Json_parser", e.getMessage());
                    }






                }
                break;
            }
        }
    };


    class MyListAdapter extends BaseAdapter {
        private static final String TAG = "MyListAdapter";
        private Context mContext;  //MyListAdapter 가 Activity가 아니므로
        private int mItemRowLayout; //항목레이아웃
        private LayoutInflater mInflater; //항목레이아웃을 전개할 전개자
        private List<Member> arItem;

        public MyListAdapter(Context context, int itemRowLayout, List<Member> list) {
            mContext = context;
            mItemRowLayout = itemRowLayout;
            arItem = list;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            Log.d(TAG,"getCount()");
            return arItem.size();
        }

        @Override
        public Object getItem(int position) {
            Log.d(TAG,"getItem()");
            return arItem.get(position);
        }

        @Override
        public long getItemId(int position) {
            Log.d(TAG,"getItemId()");
            return position;
        }

        @Override
        public View getView(final int position, View convertview, ViewGroup viewGroup) {
            Log.d(TAG,"getView()");
            if(convertview == null){
                convertview = mInflater.inflate(mItemRowLayout,viewGroup,false);
            }

            TextView name = convertview.findViewById(R.id.name_text);
            name.setText(arItem.get(position).getName());

            TextView gender = convertview.findViewById(R.id.gender_text);
            gender.setText(arItem.get(position).getGender());

            TextView age  = convertview.findViewById(R.id.age_text);
            String ageSub = String.valueOf(arItem.get(position).getAge());

            age.setText(ageSub.substring(2,4)+"-"+ageSub.substring(4,6)+"-"+ageSub.substring(6,8));

            TextView tel  = convertview.findViewById(R.id.tell_text);
            tel.setText(arItem.get(position).getTell());



            return convertview;
        }
    }
}
