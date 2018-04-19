package kr.or.dgit.bigdata.pool.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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
    ListView listView;
    public static ClassBoardFragment newInstance() {
        ClassBoardFragment cf = new ClassBoardFragment();
        return cf;
    }

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
                    ArrayList<ClassBoard> mList = new ArrayList<>();
                    try{
                     //   JSONObject jObj = new JSONObject(result);
                        //JSONObject cri = jObj.getJSONObject("cri");
                       // int page = cri.getInt("page");
                        JSONArray ja2 = new JSONArray(result);
                        for(int i=0; i < ja2.length(); i++){
                            JSONObject order = ja2.getJSONObject(i);
                            ClassBoard board = new ClassBoard();
                            board.setBno(order.getInt("bno"));
                            board.setId(order.getString("id"));
                            Date date = new Date(order.getLong("regdate"));
                            board.setRegdate(date);
                            board.setTitle(order.getString("title"));
                            mList.add(board);
                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    MyListAdapter adapter = new MyListAdapter(getContext(),R.layout.class_item,mList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        }
                    });
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.classboard, container, false);
        listView = root.findViewById(R.id.listview);
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

            TextView bno = convertview.findViewById(R.id.bno);
            bno.setText(arItem.get(position).getBno()+"");

            TextView writer = convertview.findViewById(R.id.writer);
            writer.setText(arItem.get(position).getId());

            TextView title  = convertview.findViewById(R.id.title);
            title.setText(arItem.get(position).getTitle());

            TextView date  = convertview.findViewById(R.id.date);
            Date date2 = arItem.get(position).getRegdate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = sdf.format(date2);
            date.setText(strDate);

            return convertview;
        }
    }
}