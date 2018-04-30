package kr.or.dgit.bigdata.pool.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.Clinic;

public class ClinicList extends Fragment {
    ListView mListView;
    ArrayList<Clinic> lists;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.clinic_list,container,false);
        mListView = root.findViewById(R.id.listView);
        Bundle bundle = getArguments();
        lists = (ArrayList<Clinic>) bundle.getSerializable("list");
        MyListAdapter adapter = new MyListAdapter(getContext(),R.layout.clinic_item,lists);
        mListView.setAdapter(adapter);
        return root;
    }

    class MyListAdapter extends BaseAdapter {
        private static final String TAG = "MyListAdapter";
        private Context mContext;  //MyListAdapter 가 Activity가 아니므로
        private int mItemRowLayout; //항목레이아웃
        private LayoutInflater mInflater; //항목레이아웃을 전개할 전개자
        private List<Clinic> arItem;

        public MyListAdapter(Context context, int itemRowLayout, List<Clinic> list) {
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

            TextView title = convertview.findViewById(R.id.title);
            title.setText(arItem.get(position).getClinic_title());

            TextView content = convertview.findViewById(R.id.content);
            content.setText(arItem.get(position).getClinic_content());

            return convertview;
        }
    }
}
