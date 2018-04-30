package kr.or.dgit.bigdata.pool.fragment;


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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.Member;
import kr.or.dgit.bigdata.pool.dto.NoticeBoard;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class NoticeListFragment extends Fragment implements View.OnClickListener{

    private String http = "http://192.168.0.12:8080/pool";
    String[] arrays;
    List<NoticeBoard> nList;
    ListView listView;


    public static NoticeListFragment newInstance(){
        NoticeListFragment cf = new NoticeListFragment();
        return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.notice_list, container, false);
        listView = root.findViewById(R.id.notice_list);
        nList= new ArrayList<>();
        new HttpRequestTack(getContext(),mHandler,"GET","정보를 가져오고 있습니다...").execute(http+"/notice/list");

        return root;
    }


    @Override
    public void onClick(View view) {


    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    String result = (String)msg.obj;

                    try {
                        JSONArray ja = new JSONArray(result);


                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject order = ja.getJSONObject(i);
                            NoticeBoard nBoard = new NoticeBoard();
                            nBoard.setNno((Integer) order.get("nno"));
                            nBoard.setTitle((String) order.get("title"));
                            nBoard.setContent((String) order.get("content"));
                            Date d = new Date((long)order.get("regdate"));

                            nBoard.setRegdate(d);

                            nBoard.setReadcnt((Integer) order.get("readcnt"));
                            nList.add(nBoard);
                        }
                    } catch (JSONException e) {
                        Log.i("Json_parser", e.getMessage());
                    }

                    MyListAdapter adapter = new MyListAdapter(getContext(),R.layout.notice_item,nList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();
                            tr.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.exit);
                            tr.addToBackStack(null);
                            NoticeBoardRead nb = new NoticeBoardRead();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("nno", nList.get(i).getNno());
                            nb.setArguments(bundle);
                            tr.replace(R.id.frame,nb);
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
        private List<NoticeBoard> arItem;

        public MyListAdapter(Context context, int itemRowLayout, List<NoticeBoard> list) {
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

            TextView name = convertview.findViewById(R.id.tv_name);
            name.setText("관리자");

            TextView nno = convertview.findViewById(R.id.tv_nno);
            nno.setText(String.valueOf(arItem.get(position).getNno()));

            TextView title  = convertview.findViewById(R.id.tv_title);
            title.setText(arItem.get(position).getTitle());

            TextView date  = convertview.findViewById(R.id.tv_date);
            SimpleDateFormat sf = new SimpleDateFormat("yyyy.MM.dd");
           String d= sf.format(arItem.get(position).getRegdate());
            date.setText(d);



            return convertview;
        }
    }

}
