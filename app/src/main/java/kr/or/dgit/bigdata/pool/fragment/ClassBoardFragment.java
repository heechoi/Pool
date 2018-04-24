package kr.or.dgit.bigdata.pool.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.or.dgit.bigdata.pool.ClassBoardInsertActivity;
import kr.or.dgit.bigdata.pool.LoginActivity;
import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.ClassBoard;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class ClassBoardFragment extends Fragment implements  TabLayout.OnTabSelectedListener{
    private String http = "http://192.168.0.60:8080/pool/restclassboard/";
    private String time = "";
    ViewPager viewpager;
    TabLayout tabs;

    public static ClassBoardFragment newInstance() {
        ClassBoardFragment cf = new ClassBoardFragment();
        return cf;
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    String result = (String) msg.obj;
                    Log.d("bum", "=============3 " + result);
                    try {
                        JSONObject jObj = new JSONObject(result);

                        List<Fragment> arFragment = new ArrayList<>();
                        String arName;
                        for (int i = 0; i < 5; i++) {
                            arName ="lists_"+i;
                            ArrayList<ClassBoard> mList = new ArrayList<>();

                            Log.d("bum", arName);
                            JSONArray ja = jObj.getJSONArray(arName);
                            if(ja.length() >0){
                                for (int j = 0; j < ja.length(); j++) {
                                    JSONObject order = ja.getJSONObject(j);
                                    ClassBoard board = new ClassBoard();
                                    board.setCno(order.getInt("cno"));
                                    board.setBno(order.getInt("bno"));
                                    board.setId(order.getString("id"));
                                    Date date = new Date(order.getLong("regdate"));
                                    board.setRegdate(date);
                                    board.setTitle(order.getString("title"));
                                    mList.add(board);

                                }
                            }else{
                                ClassBoard board = new ClassBoard();
                                board.setBno(-1);
                                board.setId("없음");
                                Date date = new Date();
                                board.setRegdate(date);
                                board.setTitle("등록된 게시글이 없습니다. ");
                                mList.add(board);
                            }
                            ClassBoardListFragment cf = new ClassBoardListFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("list", mList);
                            cf.setArguments(bundle);

                            arFragment.add(cf);
                        }
                        MyPagerAdapter adapter = new MyPagerAdapter(getFragmentManager(),arFragment);
                        viewpager.setAdapter(adapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.classboard, container, false);
        viewpager = root.findViewById(R.id.viewPager);
        tabs = root.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewpager);
        tabs.addOnTabSelectedListener(this);
        Button cls_board_btn = (Button) root.findViewById(R.id.cls_board_btn);
        cls_board_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("시간을 선택해주세요")
                        .setItems(R.array.classboard_selected, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String[] arrays = getResources().getStringArray(R.array.classboard_selected);
                                String[] arrname = new String[]{"time"};
                                Log.d("bum", "==================" + arrays[i]);
                                String[] arr = {arrays[i]};
                                time = arrays[i];
                                String httpclasstime = http + "classlist";
                                new HttpRequestTack(getContext(), mHandler, arr, arrname, "POST", "noProgressbar").execute(httpclasstime);
                            }
                        })
                        .setNegativeButton("취소", null).create().show();
            }
        });
        Button cls_board_insert_btn = (Button)root.findViewById(R.id.cls_board_insert_btn);
        cls_board_insert_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent insertIntent = new Intent(getContext(),ClassBoardInsertActivity.class);
                startActivity(insertIntent);

                getActivity().overridePendingTransition(R.anim.login,R.anim.login_out);
            }
        });
        return root;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewpager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragment;
        private String title[] = new String[]{"초급","중급","고급","상급","연수"};

        public MyPagerAdapter(FragmentManager fm,List<Fragment> list) {
            super(fm);
            this.fragment = list;
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
}