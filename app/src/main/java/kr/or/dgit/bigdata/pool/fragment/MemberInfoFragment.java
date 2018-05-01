package kr.or.dgit.bigdata.pool.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.Member;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class MemberInfoFragment extends Fragment implements View.OnClickListener{

    private String http = "http://rkd0519.cafe24.com/pool";
    Button btn;
    String[] arrays;
    int[] cno;
    int mno;
    ListView listView;
    List<String> lists;
    List<String> list;
    String tell ;
    public static MemberInfoFragment newInstance(){
        MemberInfoFragment cf = new MemberInfoFragment();
        return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.memberinfo, container, false);

        btn = root.findViewById(R.id.call);
        btn.setOnClickListener(this);
        mno = 0;

        SharedPreferences sp = this.getActivity().getSharedPreferences("member",Context.MODE_PRIVATE);

        if(sp.getInt("mno",0) != 0){
            mno = (int)sp.getInt("mno",0);
            btn.setVisibility(View.GONE);

        }else if(getArguments().getSerializable("mno") !=null){
            mno= (int) getArguments().getSerializable("mno");
            btn.setVisibility(View.VISIBLE);
        }


        new HttpRequestTack(getContext(),mHandler,"GET","정보를 가져오고 있습니다...").execute(http+"/restregister/member?mno="+mno);

        Calendar cal = Calendar.getInstance();
        Date d = new Date();
        d.setMonth(5-1);
        cal.setTime(d);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        lists = new ArrayList<>();
        list = new ArrayList<>();
        for(int i=1; i <= lastDay; i++) {
            d.setDate(i);
            if(d.getDay() != 0 && d.getDay() !=6) {
                if(i<10){
                    lists.add("2018040"+i);
                }else{
                    lists.add("201804"+i);
                }
            }
        }

        String arr = "";
        for(int i=0;i<lists.size();i++){
            arr += lists.get(i)+"/";
        }
        new HttpRequestTack(getContext(),mHandler,"GET","정보를 가져오고 있습니다...",2).execute(http+"/restregister/attendance?mno="+mno+"&lists="+arr);

        listView = root.findViewById(R.id.check_list);

        return root;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.call){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+tell));
            startActivity(intent);
        }
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    String result = (String)msg.obj;

                    try {
                        JSONObject order = new JSONObject(result);
                        Object name = order.get("name");
                        Object tel = order.get("tell");
                        tell = (String)tel;
                        TextView nameText = getView().findViewById(R.id.name);
                        nameText.setText((String)name);

                    } catch (JSONException e) {
                        Log.i("Json_parser", e.getMessage());
                    }
                }
                break;
                case 2:{
                    String result = (String) msg.obj;
                    result= result.replace("[","");
                    result=result.replace("]","");
                    result= result.replace("\"","");

                    String[] str = result.split(",");
                    for(int i = 0;i<str.length;i++){
                        list.add(str[i]);
                    }


                    MyListAdapter adapter = new MyListAdapter(getContext(),R.layout.attend_item,list,lists);
                    listView.setAdapter(adapter);

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
        private List<String> arItem;
        private List<String> items;

        public MyListAdapter(Context context, int itemRowLayout, List<String> list, List<String> lists) {
            mContext = context;
            mItemRowLayout = itemRowLayout;
            arItem = list;
            items=lists;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            Log.d(TAG,"getCount()");
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            Log.d(TAG,"getItem()");
            return items.get(position);
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

            TextView date = convertview.findViewById(R.id.date_text);
            date.setText(items.get(position));

           TextView attend = convertview.findViewById(R.id.attend_text);
           attend.setText(arItem.get(position));

            return convertview;
        }
    }
}
