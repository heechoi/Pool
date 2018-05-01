package kr.or.dgit.bigdata.pool.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.ClassBoard;
import kr.or.dgit.bigdata.pool.dto.Clinic;
import kr.or.dgit.bigdata.pool.dto.Member;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;


public class ClinicFragment extends Fragment implements  TabLayout.OnTabSelectedListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> arrFragment;
    private String http = "http://rkd0519.cafe24.com/pool/restclinic/clinic";
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.clinic,container,false);
        tabLayout = (TabLayout)root.findViewById(R.id.tabs);
        viewPager = (ViewPager)root.findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
        new HttpRequestTack(getContext(),mHandler,"GET","클리닉 불러오는중입니다.").execute(http);
        return root;
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragment = new ArrayList<Fragment>();
        private String title[] = new String[]{"자유형","배영","평영","접영","스타트&턴"};
        public MyPagerAdapter(FragmentManager fm,ArrayList<Fragment> arrFragment) {
            super(fm);
            this.fragment = arrFragment;

        }
        @Override
        public Fragment getItem(int position) {
            return this.fragment.get(position);
        }

        @Override
        public int getCount() {
            return this.fragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String result = (String) msg.obj;
                    Log.d("bum", "=============3 " + result);
                    try{
                        JSONObject jObj = new JSONObject(result);
                        arrFragment = new ArrayList<>();
                        for(int i=0; i< 5; i++){
                            String name = "list_"+i;
                            JSONArray ja = jObj.getJSONArray(name);
                            ArrayList<Clinic> list = new ArrayList<>();
                            for(int j=0; j < ja.length(); j++){
                                Clinic clinic = new Clinic();
                                JSONObject order = ja.getJSONObject(j);
                                clinic.setClinic_content(order.getString("clinic_content"));
                                clinic.setClinic_title(order.getString("clinic_title"));
                                clinic.setClinic_path(order.getString("clinic_path"));

                                list.add(clinic);
                            }
                            ClinicList cf = new ClinicList();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("list",list);
                            cf.setArguments(bundle);
                            arrFragment.add(cf);
                        }

                        viewPager.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager(),arrFragment));

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
}
