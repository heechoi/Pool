package kr.or.dgit.bigdata.pool.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.dto.ClassBoard;
import kr.or.dgit.bigdata.pool.dto.Clinic;

public class ClinicList extends Fragment {
    ListView mListView;
    ArrayList<Clinic> lists;
    ImageView imgview;
    Bitmap bmImg;
    back tack;
    Bitmap rotate;
    File filePath;
    MyListAdapter adapter;
    private String imgUrl = "http://192.168.0.60:8080";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.clinic_list,container,false);
        mListView = root.findViewById(R.id.listView);
        Bundle bundle = getArguments();
        lists = (ArrayList<Clinic>) bundle.getSerializable("list");
        adapter = new MyListAdapter(getContext(),R.layout.clinic_item,lists);
        mListView.setAdapter(adapter);
        for(int i=0; i< lists.size(); i++){
            Log.d("bum",lists.get(i).toString());
            if (!lists.get(i).getClinic_path().toString().equalsIgnoreCase("null") && !lists.get(i).getClinic_path().toString().equalsIgnoreCase("") && lists.get(i).getClinic_path().toString() != null) {

                tack = new back(i);
                tack.execute(imgUrl + lists.get(i).getClinic_path());
            }
        }
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

            imgview = convertview.findViewById(R.id.clinic_img);
            if (!arItem.get(position).getClinic_path().toString().equalsIgnoreCase("null") && !arItem.get(position).getClinic_path().toString().equalsIgnoreCase("") && arItem.get(position).getClinic_path().toString() != null) {{
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600);
                imgview.setLayoutParams(lp);
                imgview.setImageBitmap(arItem.get(position).getRotate());
            }}else{
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                imgview.setLayoutParams(lp);
                imgview.setImageBitmap(null);
            }
            return convertview;
        }
    }
    private class back extends AsyncTask<String, Integer, Bitmap> {


        private int position;

        public back(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            Log.d("bum1", "back onPreExecute 로그인");

        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Log.d("bum1", "back doInBackground urls : " + urls[0]);
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

            } catch (Exception e) {
                e.printStackTrace();
            }
            rotate = rotateImage(bmImg);
            return rotate;
        }

        protected void onPostExecute(Bitmap img) {

            Log.d("bum1", "back onPostExecute 로그아웃");
            Clinic board = lists.get(position);
            board.setRotate(rotate);
            adapter.notifyDataSetChanged();
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
}