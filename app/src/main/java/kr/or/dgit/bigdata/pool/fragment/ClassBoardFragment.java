package kr.or.dgit.bigdata.pool.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.text.LocaleDisplayNames;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.or.dgit.bigdata.pool.ClassBoardInsertActivity;
import kr.or.dgit.bigdata.pool.LoginActivity;
import kr.or.dgit.bigdata.pool.MainActivity;
import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.ClassBoard;
import kr.or.dgit.bigdata.pool.dto.ClassboardReply;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

import static android.content.Context.MODE_PRIVATE;

public class ClassBoardFragment extends Fragment implements AdapterView.OnItemClickListener , AbsListView.OnScrollListener{
    private String http = "http://192.168.0.60:8080/pool/restclassboard/";
    private String time = "";
    private String level = "";
    Bitmap bmImg;
    back tack;
    Bitmap rotate;
    ListView listview;
    File filePath;
    String imgurl = "http://192.168.0.60:8080";
    BaseAdapter mListAdapter;
    ArrayList<ClassBoard> mList  = new ArrayList<>();;
    SharedPreferences sp;
    SharedPreferences sp2;
    private boolean mLockListView = false;
    private boolean lastItemVisibleFlag = false;
    int page =1;
    int c = 0;
    ProgressBar mProgressBar;
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
                                board.setContent(order.getString("content"));
                                board.setImgpath(order.getString("imgpath"));
                                board.setTitle(order.getString("title"));
                                mList.add(board);
                            }
                            mListAdapter = new MyListAdapter(getContext(), R.layout.class_item, mList);
                        } else {
                            ClassBoard board = new ClassBoard();
                            board.setBno(-1);
                            board.setId("없음");
                            Date date = new Date();
                            board.setRegdate(date);
                            board.setTitle("등록된 게시글이 없습니다. ");
                            mList.add(board);
                            mListAdapter = new MyNoListAdapter(getContext(), R.layout.class_item2, mList);
                        }
                        mListAdapter.notifyDataSetChanged();
                        listview.setAdapter(mListAdapter);
                        Log.d("bum", "끝끝");
                        for(int i=0; i< mList.size(); i++){
                            Log.d("bum",mList.get(i).toString());
                            if (!mList.get(i).getImgpath().toString().equalsIgnoreCase("null") && !mList.get(i).getImgpath().toString().equalsIgnoreCase("") && mList.get(i).getImgpath().toString() != null) {
                                Log.d("bum", mList.get(i).getBno() + "널아님");
                                tack = new back(i);
                                tack.execute(imgurl + mList.get(i).getImgpath());
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case 2:
                    String result2 = (String) msg.obj;
                    Bundle bundle = new Bundle();
                    try {
                        JSONObject jObj = new JSONObject(result2);

                        bundle.putInt("bno",jObj.getInt("bno"));
                        bundle.putInt("readcnt",jObj.getInt("readcnt"));
                        bundle.putLong("regdate",jObj.getLong("regdate"));
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
                    ClassBoardRead fgm = new ClassBoardRead();
                    fgm.setArguments(bundle);
                    tr.replace(R.id.frame, fgm);
                    tr.commit();
                    break;
                case 3:
                    String result3 = (String) msg.obj;
                    Log.d("bum", "=============5 " + result3);
                    try {
                        JSONArray ja = new JSONArray(result3);

                        if (ja.length() > 0) {
                            for (int j = 0; j < ja.length(); j++) {
                                JSONObject order = ja.getJSONObject(j);
                                ClassBoard board = new ClassBoard();
                                board.setCno(order.getInt("cno"));
                                board.setBno(order.getInt("bno"));
                                board.setId(order.getString("id"));
                                Date date = new Date(order.getLong("regdate"));
                                board.setRegdate(date);
                                board.setContent(order.getString("content"));
                                board.setImgpath(order.getString("imgpath"));
                                board.setTitle(order.getString("title"));
                                mList.add(board);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    mListAdapter.notifyDataSetChanged();
                    mProgressBar.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d("bum","메뉴 체인지");
        inflater.inflate(R.menu.classbardupdate, menu);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.classboard, container, false);
        listview = root.findViewById(R.id.listview);
        listview.setOnItemClickListener(this);
        listview.setOnScrollListener(this);
        sp = getActivity().getSharedPreferences("member",MODE_PRIVATE);
        sp2 = getActivity().getSharedPreferences("admin",MODE_PRIVATE);
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
                String id2 = sp2.getString("name","");
                String id = sp.getString("name","");
                if ((id.equalsIgnoreCase("") || id==null) && (id2.equalsIgnoreCase("") || id2==null)){
                    Toast.makeText(getContext(),"로그인이 필요합니다.",Toast.LENGTH_SHORT).show();
                }else{
                    Intent insertIntent = new Intent(getContext(), ClassBoardInsertActivity.class);
                    startActivity(insertIntent);
                    getActivity().overridePendingTransition(R.anim.login, R.anim.login_out);
                }
            }
        });
        Bundle bundle = getArguments();
        if(bundle !=null){
            Log.d("bumbum",bundle.getString("cno"));
            c = Integer.parseInt(bundle.getString("cno"));
            String[] arr = {c+""};
            String[] arrname = {"cno"};
            String searchhttp = http+"classboardlist";
            new HttpRequestTack(getContext(), mHandler, arr, arrname, "POST", "noProgressbar").execute(searchhttp);
        }
        mProgressBar = root.findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        return root;
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
            TextView writer = convertview.findViewById(R.id.writer);
            writer.setText(arItem.get(position).getId());

            TextView content = convertview.findViewById(R.id.content);
            String contenttext = arItem.get(position).getContent().replace("<br>",System.getProperty("line.separator"));
            content.setText(contenttext);

            TextView title = convertview.findViewById(R.id.title);
            title.setText(arItem.get(position).getTitle());

            TextView date = convertview.findViewById(R.id.tvRegdate);
            Date date2 = arItem.get(position).getRegdate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = sdf.format(date2);
            date.setText(strDate);

            ImageView imgView = convertview.findViewById(R.id.imgview);

            if (!arItem.get(position).getImgpath().toString().equalsIgnoreCase("null") && !arItem.get(position).getImgpath().toString().equalsIgnoreCase("") && arItem.get(position).getImgpath().toString() != null) {{
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1400);
                imgView.setLayoutParams(lp);
                imgView.setImageBitmap(arItem.get(position).getRotateImage());
            }}else{
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                imgView.setLayoutParams(lp);
                imgView.setImageBitmap(null);
            }

            return convertview;
        }
    }

    private class back extends AsyncTask<String, Integer, Bitmap> {

        ProgressDialog mProgressDialog = null;
        private int position;

        public back(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            Log.d("bum1", "로그인");
            //mProgressDialog = ProgressDialog.show(getContext(), "Wait", "이미지를 불러오는 중입니다.");
        }

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pool";
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                filePath = new File("/storage/emulated/0/pool/test.jpg");
                filePath.createNewFile();
                Log.d("bum", "파일패스 " + filePath.getAbsolutePath());
                OutputStream out = new FileOutputStream(filePath);
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                //out.close();
                bmImg = BitmapFactory.decodeFile(filePath.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
            }
            rotate = rotateImage(bmImg);
            return rotate;
        }

        protected void onPostExecute(Bitmap img) {
            //mProgressDialog.dismiss();
            Log.d("bum1", "로그아웃");
            ClassBoard board = mList.get(position);
            board.setRotateImage(rotate);
            mListAdapter.notifyDataSetChanged();
        }
    }

    private Bitmap rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        Log.d("bum", "===========시작");
        Log.d("bum", "===========1");
        try {
            if (filePath != null) {
                Log.d("bum", "===========2");
                exifInterface = new ExifInterface(filePath.getAbsolutePath());
                Log.d("bum", "===========3");
            } else {
                //  exifInterface = new ExifInterface(galleryPath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix m = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                m.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                m.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                m.setRotate(270);
                break;
            default:
                m.setRotate(0);
        }
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        return rotated;
    }
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            Log.d("bum","끝 스크롤");
            mProgressBar.setVisibility(View.VISIBLE);
            String[] arr = {mList.get(0).getCno()+"",page+""};
            String[] arrname = {"cno","page"};
            String searchhttp = http+"classboardlist";
            page++;
            new HttpRequestTack(getContext(), mHandler, arr, arrname, "POST", "noProgressbar",3).execute(searchhttp);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }
}