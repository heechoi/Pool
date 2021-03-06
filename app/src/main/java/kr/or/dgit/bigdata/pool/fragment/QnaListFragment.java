package kr.or.dgit.bigdata.pool.fragment;


import android.content.Context;
import android.content.SharedPreferences;
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
import kr.or.dgit.bigdata.pool.dto.NoticeBoard;
import kr.or.dgit.bigdata.pool.dto.QnaBoard;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class QnaListFragment extends Fragment implements View.OnClickListener{

    private String http = "http://rkd0519.cafe24.com/pool";
    String[] arrays;
    List<QnaBoard> nList;
    ListView listView;
    String id;
    SharedPreferences member;
    public static QnaListFragment newInstance(){
        QnaListFragment cf = new QnaListFragment();
        return cf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.qna_list, container, false);
        listView = root.findViewById(R.id.qna_list);
        nList= new ArrayList<>();
        member = getContext().getSharedPreferences("member",0);
        id = member.getString("id","");
        new HttpRequestTack(getContext(),mHandler,"GET","정보를 가져오고 있습니다...").execute(http+"/restQna/list?id="+id);

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
                            QnaBoard qBoard = new QnaBoard();
                            qBoard.setBno((Integer) order.get("bno"));
                            qBoard.setWriter((String) order.get("writer"));
                            qBoard.setTitle((String) order.get("title"));
                            qBoard.setReplycheck((boolean)order.get("replycheck"));
                            Date d = new Date((long)order.get("regdate"));

                            qBoard.setRegdate(d);

                            nList.add(qBoard);
                        }
                    } catch (JSONException e) {
                        Log.i("Json_parser", e.getMessage());
                    }

                    MyListAdapter adapter = new MyListAdapter(getContext(),R.layout.qna_item,nList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();
                            tr.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.exit);
                            tr.addToBackStack(null);
                            QnaReadFragement qna = new QnaReadFragement();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("bno", nList.get(i).getBno());
                            qna.setArguments(bundle);
                            tr.replace(R.id.frame,qna);
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
        private List<QnaBoard> arItem;

        public MyListAdapter(Context context, int itemRowLayout, List<QnaBoard> list) {
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
            nno.setText(String.valueOf(arItem.get(position).getBno()));

            TextView title  = convertview.findViewById(R.id.tv_title);
            title.setText(arItem.get(position).getTitle());

            TextView check  = convertview.findViewById(R.id.tv_check);
            if(arItem.get(position).isReplycheck()){
                check.setText("O");
            }else{
                check.setText("X");
            }


            TextView date  = convertview.findViewById(R.id.tv_date);
            SimpleDateFormat sf = new SimpleDateFormat("yyyy.MM.dd");
           String d= sf.format(arItem.get(position).getRegdate());
            date.setText(d);



            return convertview;
        }
    }

}
