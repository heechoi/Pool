package kr.or.dgit.bigdata.pool.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.ClassBoard;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class ClassBoardListFragment extends Fragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener, Serializable {
    ListView mListView;
    ProgressBar progressBar;
    ArrayList<ClassBoard> mList;
    BaseAdapter adapter;
    private boolean lastItemVisibleFlag = false;
    private boolean mLockListView = false;
    private String http = "http://192.168.0.60:8080/pool/restclassboard/classboardlist";
    int page = 1;
    int cno = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("bum", "===================createView");
        View root = inflater.inflate(R.layout.classboard_list, container, false);
        Bundle bundle = getArguments();
        mList = (ArrayList<ClassBoard>) bundle.getSerializable("list");

        if (mList.get(0).getBno() == -1) {
            adapter = new MyNoListAdapter(getContext(), R.layout.class_item2, mList);
        } else {
            adapter = new MyListAdapter(getContext(), R.layout.class_item, mList);
            cno = mList.get(0).getCno();
        }


        mListView = root.findViewById(R.id.listview);
        mListView.setAdapter(adapter);
        progressBar = root.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);
        return root;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {

        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            progressBar.setVisibility(View.VISIBLE);
            page++;
            String[] arr = {cno + "", page + ""};
            String[] arrname = {"cno", "page"};

            new HttpRequestTack(getContext(), mHandler, arr, arrname, "POST", "noProgressbar...").execute(http);
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


        String bno = mList.get(position).getBno() + "";
        String[] arrname = {"bno"};
        String[] arr = {bno};
        String httpread = "http://192.168.0.60:8080/pool/restclassboard/read";
        new HttpRequestTack(getContext(), mHandler, arr, arrname, "POST", "글을 읽어오고 있습니다...", 2).execute(httpread);
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

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String result = (String) msg.obj;
                    Log.d("bum", result);
                    try {
                        JSONArray ja = new JSONArray(result);
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
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    break;
                case 2:
                    String result2 = (String) msg.obj;
                    Bundle bundle = new Bundle();
                    try {
                        JSONObject jObj = new JSONObject(result2);

                        bundle.putInt("bno",jObj.getInt("bno"));
                        bundle.putInt("regdate",jObj.getInt("regdate"));
                        bundle.putLong("readcnt",jObj.getLong("readcnt"));
                        bundle.putString("imgpath",jObj.getString("imgpath"));
                        bundle.putString("title",jObj.getString("title"));
                        bundle.putString("id",jObj.getString("id"));
                        bundle.putString("content",jObj.getString("content"));
                        bundle.putInt("cno",jObj.getInt("cno"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();
                    tr.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.exit);
                    tr.addToBackStack(null);
                    ClassBoardRead fgm = new ClassBoardRead();
                    fgm.setArguments(bundle);
                    tr.replace(R.id.frame, fgm);
                    tr.commit();
                    break;
            }

        }
    };
}
