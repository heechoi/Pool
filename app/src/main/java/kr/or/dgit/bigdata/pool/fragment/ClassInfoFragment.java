package kr.or.dgit.bigdata.pool.fragment;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.Member;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class ClassInfoFragment extends Fragment implements View.OnClickListener{

    private String http = "http://rkd0519.cafe24.com/pool";
    Button classBtn;
    String[] arrays;
    int[] cno;
    int tno;
    ListView listView;
    ArrayList<Member> mList;
    String d;
    public static ClassInfoFragment newInstance(){
        ClassInfoFragment cf = new ClassInfoFragment();
        return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.classinfo, container, false);

        classBtn = root.findViewById(R.id.btn_class);
        classBtn.setOnClickListener(this);

        SharedPreferences sp = this.getActivity().getSharedPreferences("Admin",0);

        tno = (int)sp.getInt("tno",0);




        listView = root.findViewById(R.id.member_list);
        return root;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_class){
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month= month+1;
                    if(month<10){
                        d = year+"0"+month+"01";
                    }else{
                        d = year+""+month+"01";
                    }

                    new HttpRequestTack(getContext(),mHandler,"GET","정보를 가져오고 있습니다...").execute(http+"/restclassinfo/classlist?tno="+tno+"&s_day="+d);

                }
            },year,month,day);

            datePickerDialog.show();
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
                        arrays = new String[ja.length()];
                        cno = new int[ja.length()];
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject order = ja.getJSONObject(i);
                            Object no = order.get("cno");
                            Object time = order.get("time");
                            Object level =order.get("level");
                            cno[i] = (int) no;
                            arrays[i]=(time+" "+level);
                        }
                    } catch (JSONException e) {
                        Log.i("Json_parser", e.getMessage());
                    }
                    new android.support.v7.app.AlertDialog.Builder(getActivity())
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("반을 선택하세요")
                            .setItems(arrays, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i){
                                    TextView title=getActivity().findViewById(R.id.class_name);
                                    title.setText(arrays[i]);
                                    new HttpRequestTack(getContext(),mHandler,"GET","정보를 가져오고 있습니다...",2).execute(http+"/restregister/memberList?cno="+cno[i]);
                                }
                            })
                            .setNegativeButton("취소", null).create().show();
                }
                break;
                case 2:{
                    String result = (String)msg.obj;
                     mList = new ArrayList<>();

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
                            mList.add(member);
                        }
                    } catch (JSONException e) {
                        Log.i("Json_parser", e.getMessage());
                    }


                    MyListAdapter adapter = new MyListAdapter(getContext(),R.layout.member_item,mList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();
                            tr.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.exit);
                            tr.addToBackStack(null);
                            MemberInfoFragment mf = new MemberInfoFragment();
                            Bundle bundle = new Bundle();
                           /* new HttpRequestTack(getContext(),mHandler,"GET","정보를 가져오고 있습니다...",3).execute(http+"/restregister/member?mno="+mList.get(i).getMno());*/
                            bundle.putSerializable("mno", mList.get(i).getMno());
                            mf.setArguments(bundle);
                            tr.replace(R.id.frame,mf);
                            tr.commit();

                        }
                    });



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
