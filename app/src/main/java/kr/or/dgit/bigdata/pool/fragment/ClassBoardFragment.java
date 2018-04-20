package kr.or.dgit.bigdata.pool.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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

import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.ClassBoard;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class ClassBoardFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private String http = "http://192.168.0.60:8080/pool/restclassboard/";
    private String time = "";
    private String level = "";
    private int cno = 0;
    ViewPager viewpager;
    LinearLayout pageNum;
    private final int img_1 = R.drawable.circle;
    private final int img_2 = R.drawable.circle_2;
    private ImageView[] imgArr;

    private int pagechange = 0;
    private int page = 0;
    private int pageSize = 0;


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
                    final String[] string;
                    try {

                        JSONArray ja = new JSONArray(result);
                        string = new String[ja.length()];
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject order = ja.getJSONObject(i);
                            string[i] = (order.getString("level"));
                        }
                        new AlertDialog.Builder(getActivity())
                                .setIcon(R.mipmap.ic_launcher)
                                .setTitle("레벨을 선택해주세요")
                                .setItems(string, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String[] arrname = new String[]{"time", "level"};
                                        String[] arr = new String[]{time, string[i]};
                                        String httpclasstime = http + "classlist2";
                                        new HttpRequestTack(getContext(), mHandler, arr, arrname, "POST", "message...", 2).execute(httpclasstime);
                                    }
                                }).setNegativeButton("취소", null).create().show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case 2: {
                    String result = (String) msg.obj;
                    Log.d("bum", "=================2 " + result);

                    try {
                        JSONArray ja = new JSONArray("[" + result + "]");
                        JSONObject order = ja.getJSONObject(0);
                        cno = order.getInt("cno");
                        String[] arrname = new String[]{"cno"};
                        String[] arr = new String[]{cno + ""};
                        String httpclassboard = http + "classboard";
                        Log.d("bum", httpclassboard + "");
                        new HttpRequestTack(getContext(), mHandler, arr, arrname, "POST", "message...", 3).execute(httpclassboard);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case 3: {
                    String result = (String) msg.obj;
                    Log.d("bum", "=============3 " + result);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(20, 0, 0, 0);

                    pageNum.removeAllViews();
                    try {
                        JSONObject jObj = new JSONObject(result);
                        pageSize = jObj.getInt("pageSize");
                        page = jObj.getInt("page");
                        Log.d("bum", pageSize + "");
                        ArrayList<Fragment> arFragment = new ArrayList<>();


                        imgArr = new ImageView[pageSize];
                        String arName = "list_";
                        for (int i = 0; i < pageSize; i++) {
                            imgArr[i] = new ImageView(getContext());
                            ArrayList<ClassBoard> mList = new ArrayList<>();

                            arName += i;
                            Log.d("bum", arName);
                            JSONArray ja = jObj.getJSONArray(arName);
                            for (int j = 0; j < ja.length(); j++) {
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

                            arFragment.add(cf);
                            arName = "list_";
                            Bitmap bm;
                            if (i == 0) {
                                bm = BitmapFactory.decodeResource(getResources(), img_2);
                            } else {
                                bm = BitmapFactory.decodeResource(getResources(), img_1);
                            }
                            Matrix m = new Matrix();
                            m.postTranslate(0, 0);

                            imgArr[i].setLayoutParams(lp);
                            imgArr[i].setImageBitmap(bm);
                            imgArr[i].setImageMatrix(m);

                            pageNum.addView(imgArr[i]);
                        }
                        //viewpager.getAdapter().
                        MyPagerAdapter mPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager(), arFragment);
                        mPagerAdapter.notifyDataSetChanged();
                        viewpager.addOnPageChangeListener(ClassBoardFragment.this);
                        viewpager.setAdapter(mPagerAdapter);

                    } catch (Exception e) {
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
                                String[] arrname = new String[]{"time"};
                                Log.d("bum", "==================" + arrays[i]);
                                String[] arr = {arrays[i]};
                                time = arrays[i];
                                String httpclasstime = http + "classlist";
                                new HttpRequestTack(getContext(), mHandler, arr, arrname, "POST", "message...").execute(httpclasstime);
                            }
                        })
                        .setNegativeButton("취소", null).create().show();
            }
        });
        return root;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (pagechange == 1 && position == 4) {
            String[] arrname = new String[]{"cno", "page"};
            int pageno = page + pageSize;
            String[] arr = new String[]{cno + "", pageno + ""};
            String httpclassboard = http + "classboard";
            Log.d("bum", httpclassboard + "");
            new HttpRequestTack(getContext(), mHandler, arr, arrname, "POST", "message...", 3).execute(httpclassboard);
        }
        if (position == 4) {
            pagechange = 1;
        }
    }

    @Override
    public void onPageSelected(int position) {

        imgArr[position].setImageResource(R.drawable.circle_2);

        for (int i = 0; i < pageSize; i++) {
            if (position != i) {
                imgArr[i].setImageResource(R.drawable.circle);
            }
            if (position != 4) {
                pagechange = 0;
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragment;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragment) {
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

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }


}