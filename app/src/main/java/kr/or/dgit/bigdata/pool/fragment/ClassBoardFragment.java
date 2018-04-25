package kr.or.dgit.bigdata.pool.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.or.dgit.bigdata.pool.ClassBoardInsertActivity;
import kr.or.dgit.bigdata.pool.LoginActivity;
import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.ClassBoard;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class ClassBoardFragment extends Fragment {
    private String http = "http://192.168.0.60:8080/pool/restclassboard/";
    private String time = "";
    private String level = "";
    ListView listview;


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
                        ArrayList<ClassBoard> mList = new ArrayList<>();

                        JSONArray ja = new JSONArray(result);
                        BaseAdapter adapter;
                        if (ja.length() > 0) {
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
                            adapter = new MyListAdapter(getContext(), R.layout.class_item, mList);
                        } else {
                            ClassBoard board = new ClassBoard();
                            board.setBno(-1);
                            board.setId("없음");
                            Date date = new Date();
                            board.setRegdate(date);
                            board.setTitle("등록된 게시글이 없습니다. ");
                            mList.add(board);
                            adapter = new MyNoListAdapter(getContext(), R.layout.class_item2, mList);
                        }
                        adapter.notifyDataSetChanged();
                        listview.setAdapter(adapter);

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

        View root = inflater.inflate(R.layout.classboard, container, false);
        listview = root.findViewById(R.id.listview);
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
                                time = arrays[i];
                                new AlertDialog.Builder(getActivity())
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setTitle("시간을 선택해주세요")
                                        .setItems(R.array.classboard_level, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String[] arrays = getResources().getStringArray(R.array.classboard_level);
                                                String[] arrname = new String[]{"time", "level"};
                                                Log.d("bum", "==================" + arrays[i]);
                                                level = arrays[i];
                                                String[] arr = {time, level};
                                                String httpclasstime = http + "classlist";
                                                new HttpRequestTack(getContext(), mHandler, arr, arrname, "POST", "noProgressbar").execute(httpclasstime);
                                            }
                                        })
                                        .setNegativeButton("취소", null).create().show();
                            }
                        })
                        .setNegativeButton("취소", null).create().show();

            }
        });
        Button cls_board_insert_btn = (Button) root.findViewById(R.id.cls_board_insert_btn);
        cls_board_insert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent insertIntent = new Intent(getContext(), ClassBoardInsertActivity.class);
                startActivity(insertIntent);

                getActivity().overridePendingTransition(R.anim.login, R.anim.login_out);
            }
        });
        return root;
    }

    class MyNoListAdapter extends BaseAdapter {
        private static final String TAG = "MyListAdapter";
        private Context mContext;  //MyListAdapter 가 Activity가 아니므로
        private int mItemRowLayout; //항목레이아웃
        private LayoutInflater mInflater; //항목레이아웃을 전개할 전개자
        private List<ClassBoard> arItem;

        public MyNoListAdapter(Context context, int itemRowLayout, List<ClassBoard> list) {
            mContext = context;
            mItemRowLayout = itemRowLayout;
            arItem = list;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount()");
            return arItem.size();
        }

        @Override
        public Object getItem(int i) {
            Log.d(TAG, "getItem()");
            return arItem.get(i);
        }

        @Override
        public long getItemId(int i) {
            Log.d(TAG, "getItemId()");
            return i;
        }

        @Override
        public View getView(final int position, View convertview, ViewGroup viewGroup) {
            Log.d(TAG, "getView()");
            if (convertview == null) {
                convertview = mInflater.inflate(mItemRowLayout, viewGroup, false);
            }
            TextView title = convertview.findViewById(R.id.title);
            title.setText(arItem.get(position).getTitle());

            return convertview;
        }
    }

    class MyListAdapter extends BaseAdapter {
        private static final String TAG = "MyListAdapter";
        private Context mContext;  //MyListAdapter 가 Activity가 아니므로
        private int mItemRowLayout; //항목레이아웃
        private LayoutInflater mInflater; //항목레이아웃을 전개할 전개자
        private List<ClassBoard> arItem;

        public MyListAdapter(Context context, int itemRowLayout, List<ClassBoard> list) {
            mContext = context;
            mItemRowLayout = itemRowLayout;
            arItem = list;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount()");
            return arItem.size();
        }

        @Override
        public Object getItem(int position) {
            Log.d(TAG, "getItem()");
            return arItem.get(position);
        }

        @Override
        public long getItemId(int position) {
            Log.d(TAG, "getItemId()");
            return position;
        }

        @Override
        public View getView(final int position, View convertview, ViewGroup viewGroup) {
            Log.d(TAG, "getView()");
            if (convertview == null) {
                convertview = mInflater.inflate(mItemRowLayout, viewGroup, false);
            }

            TextView bno = convertview.findViewById(R.id.bno);
            bno.setText(arItem.get(position).getBno() + "");

            TextView writer = convertview.findViewById(R.id.writer);
            writer.setText(arItem.get(position).getId());

            TextView title = convertview.findViewById(R.id.title);
            title.setText(arItem.get(position).getTitle());

            TextView date = convertview.findViewById(R.id.date);
            Date date2 = arItem.get(position).getRegdate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = sdf.format(date2);
            date.setText(strDate);

            return convertview;
        }
    }
}