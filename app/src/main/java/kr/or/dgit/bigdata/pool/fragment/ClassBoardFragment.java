package kr.or.dgit.bigdata.pool.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.or.dgit.bigdata.pool.LoginActivity;
import kr.or.dgit.bigdata.pool.MainActivity;
import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.ClassBoard;
import kr.or.dgit.bigdata.pool.dto.Member;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class ClassBoardFragment extends Fragment {
    private String http = "http://192.168.0.60:8080/pool/restclassboard/";
    private String time = "";
    private String level = "";
    private int cno = 0;
    ViewPager viewpager;
    LinearLayout pageNum;
    public static ClassBoardFragment newInstance() {
        ClassBoardFragment cf = new ClassBoardFragment();
        return cf;
    }
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{

                        String result = (String)msg.obj;
                        final String[] string;
                        try{

                            JSONArray ja = new JSONArray(result);
                            string = new String[ja.length()];
                            for (int i=0; i< ja.length(); i++){
                                JSONObject order = ja.getJSONObject(i);
                                string[i] = (order.getString("level"));
                            }
                            new AlertDialog.Builder(getActivity())
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setTitle("레벨을 선택해주세요")
                                    .setItems(string, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String[] arrname = new String[] {"time","level"};
                                            String[] arr = new String[]{time,string[i]};
                                            String httpclasstime = http+"classlist2";
                                            new HttpRequestTack(getContext(),mHandler,arr,arrname,"POST","message...",2).execute(httpclasstime);
                                        }
                                    }).setNegativeButton("취소", null).create().show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
                    break;
                case 2:{
                    String result = (String)msg.obj;
                    Log.d("bum","=================2 "+result);

                    try {
                        JSONArray ja = new JSONArray("["+result+"]");
                        JSONObject order = ja.getJSONObject(0);
                        cno = order.getInt("cno");
                        String[] arrname = new String[] {"cno"};
                        String[] arr = new String[]{cno+""};
                        String httpclassboard = http+"classboard";
                        Log.d("bum",httpclassboard+"");
                        new HttpRequestTack(getContext(),mHandler,arr,arrname,"POST","message...",3).execute(httpclassboard);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                    break;
                case 3:{
                    String result = (String)msg.obj;
                    Log.d("bum","=============3 "+result);

                    try{
                        JSONObject jObj = new JSONObject(result);
                        int pageSize = jObj.getInt("pageSize");
                        Log.d("bum",pageSize+"");
                        ArrayList<Fragment> arFragment = new ArrayList<>();
                        String arName = "list_";
                        for(int i=0; i< pageSize; i++){
                            ArrayList<ClassBoard> mList = new ArrayList<>();
                            arName += i;
                            Log.d("bum",arName);
                            JSONArray ja = jObj.getJSONArray(arName);
                            for(int j=0; j < ja.length(); j++){
                                JSONObject order = ja.getJSONObject(j);
                                ClassBoard board = new ClassBoard();
                                board.setBno(order.getInt("bno"));
                                board.setId(order.getString("id"));
                                Date date = new Date(order.getLong("regdate"));
                                board.setRegdate(date);
                                board.setTitle(order.getString("title"));
                                mList.add(board);
                            }
                            ClassBoardListFragment cf = new ClassBoardListFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("list", mList);
                            cf.setArguments(bundle);
                           // cf.setListAdapter(getContext(),mList);
                            arFragment.add(cf);
                            arName = "list_";
                        }
                        viewpager.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager(),arFragment));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(20,0,0,0);

                        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
                        Matrix m = new Matrix();
                        m.postTranslate(0, 0);
                        ImageView v2 = new ImageView(getContext());
                        v2.setLayoutParams(lp);
                        v2.setScaleType(ImageView.ScaleType.MATRIX);  // 매트릭스로 이동시키기 때문에 scaleType은 반드시 Matrix로 지정해야한다.
                        v2.setImageBitmap(bm);      // 이미지를 등록한다.
                        v2.setImageMatrix(m);
                        ImageView v3 = new ImageView(getContext());
                        v3.setLayoutParams(lp);
                        v3.setScaleType(ImageView.ScaleType.MATRIX);  // 매트릭스로 이동시키기 때문에 scaleType은 반드시 Matrix로 지정해야한다.
                        v3.setImageBitmap(bm);      // 이미지를 등록한다.
                        m = new Matrix();
                        m.postTranslate(0, 0);
                        v3.setImageMatrix(m);
                        ImageView v4 = new ImageView(getContext());
                        v4.setLayoutParams(lp);
                        v4.setScaleType(ImageView.ScaleType.MATRIX);  // 매트릭스로 이동시키기 때문에 scaleType은 반드시 Matrix로 지정해야한다.
                        v4.setImageBitmap(bm);      // 이미지를 등록한다.
                        m = new Matrix();
                        m.postTranslate(0, 0);
                        v4.setImageMatrix(m);
                        pageNum.addView(v2);
                        pageNum.addView(v3);
                        pageNum.addView(v4);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.classboard, container, false);
        viewpager = root.findViewById(R.id.viewPager);
        pageNum = root.findViewById(R.id.pageNum);
        Button cls_board_btn = (Button) root.findViewById(R.id.cls_board_btn);
        cls_board_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("반을 선택하세요")
                        .setItems(R.array.classboard_selected, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String[] arrays = getResources().getStringArray(R.array.classboard_selected);
                                String[] arrname = new String[] {"time"};
                                Log.d("bum","=================="+arrays[i]);
                                String[] arr  ={arrays[i]};
                                time = arrays[i];
                                String httpclasstime = http+"classlist";
                                new HttpRequestTack(getContext(),mHandler,arr,arrname,"POST","message...").execute(httpclasstime);
                            }
                        })
                        .setNegativeButton("취소", null).create().show();
            }
        });
        return root;
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragment;

        public MyPagerAdapter(FragmentManager fm,List<Fragment> fragment) {
            super(fm);
            this.fragment = fragment;

        }
        @Override
        public Fragment getItem(int position) {
            return this.fragment.get(position);
        }

        @Override
        public int getCount() {
            return this.fragment.size();
        }

    }
}