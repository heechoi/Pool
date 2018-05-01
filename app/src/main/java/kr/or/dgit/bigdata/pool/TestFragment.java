package kr.or.dgit.bigdata.pool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import kr.or.dgit.bigdata.pool.dto.NoticeBoard;
import kr.or.dgit.bigdata.pool.fragment.NoticeBoardRead;
import kr.or.dgit.bigdata.pool.fragment.NoticeListFragment;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class TestFragment extends Fragment implements View.OnClickListener{
    private String http = "http://192.168.0.60:8080/pool";
    ListView mListView;
    List<NoticeBoard> nList;
    Button btn_tel;
    ImageView imgView;
    TextView nameTv;
    SharedPreferences member;
    LinearLayout card;
    int mno;
    public static TestFragment newInstance(){
        TestFragment cf = new TestFragment();
        Bundle args = new Bundle();

        cf.setArguments(args);
        return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_counter, container, false);
        mListView = root.findViewById(R.id.main_notice_list);
        nList = new ArrayList<>();
        imgView = root.findViewById(R.id.barcode);
        nameTv = root.findViewById(R.id.name);
        card = root.findViewById(R.id.card);
        card.setOnClickListener(this);
        new HttpRequestTack(getContext(),mHandler,"GET","정보를 가져오고 있습니다...").execute(http+"/notice/list?page=1");
        member = getContext().getSharedPreferences("member",0);
        mno = member.getInt("mno",0);
        if(mno !=0){

            nameTv.setText(member.getString("name","")+" 님");
        }

        Bundle args = getArguments();
        int start=0;
        if(args !=null){
            start = args.getInt("start");
        }
        btn_tel = root.findViewById(R.id.phone_qna);
        btn_tel.setOnClickListener(this);
        return root;
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    String result = (String)msg.obj;

                    try {
                        JSONArray ja = new JSONArray(result);

                        if (ja.length() > 0) {
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
                        }

                    } catch (JSONException e) {
                        Log.i("Json_parser", e.getMessage());
                    }

                    MyListAdapter adapter = new MyListAdapter(getContext(),R.layout.main_notice_item,nList);
                    mListView.setAdapter(adapter);
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.phone_qna){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:01041229404"));
            startActivity(intent);
        }else if(view.getId()==R.id.card){
            if(mno == 0){
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(getActivity(), BarCodeActivity.class);
                startActivity(intent);
            }

        }

    }


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
