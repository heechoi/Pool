package kr.or.dgit.bigdata.pool.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.or.dgit.bigdata.pool.BuildConfig;
import kr.or.dgit.bigdata.pool.ClassBoardInsertActivity;
import kr.or.dgit.bigdata.pool.ClassboardUpdateActivity;
import kr.or.dgit.bigdata.pool.MainActivity;
import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.SearchIdActivity;
import kr.or.dgit.bigdata.pool.dto.ClassBoard;
import kr.or.dgit.bigdata.pool.dto.ClassboardReply;
import kr.or.dgit.bigdata.pool.onKeyBackPressedListener;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

import static android.content.Context.MODE_PRIVATE;

public class ClassBoardRead extends Fragment implements View.OnClickListener,onKeyBackPressedListener,View.OnFocusChangeListener{
    private InputMethodManager imm;
    TextView tvTitle;
    TextView tvWriter;
    TextView tvRegdate;
    TextView tvContent;
    ImageView imgview;
    Bitmap bmImg;
    back tack;
    Bitmap rotate;
    File filePath;
    String url = "http://rkd0519.cafe24.com";
    ProgressDialog mProgressDialog;
    int bno;
    List<ClassboardReply> list;
    LinearLayout listview;
    ImageView menu_img;
    LinearLayout menu_linear;
    TextView update_btn;
    TextView delete_btn;
    int rno;
    int cno;
    EditText reply_text;
    Button reply_add_btn;
    View root;
    SharedPreferences sp;
    SharedPreferences sp2;
    private AlertDialog.Builder mDialog;
    private AlertDialog dialog;

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.classboard_read, container, false);
        imgview = root.findViewById(R.id.imgview);
        tvTitle = root.findViewById(R.id.title);
        tvWriter = root.findViewById(R.id.writer);
        tvRegdate = root.findViewById(R.id.tvRegdate);
        tvContent = root.findViewById(R.id.content);
        listview = root.findViewById(R.id.listview);
        menu_img = root.findViewById(R.id.menu_img);
        menu_linear = root.findViewById(R.id.menu_linear);
        update_btn = root.findViewById(R.id.classboard_update);
        delete_btn = root.findViewById(R.id.classboard_delete);
        reply_text = root.findViewById(R.id.reply_text);
        reply_add_btn = root.findViewById(R.id.reply_add_btn);
        reply_text.setOnFocusChangeListener(this);
        reply_add_btn.setOnClickListener(this);
        update_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);
        menu_img.setOnClickListener(this);
        menu_linear.setVisibility(View.GONE);

        imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        Bundle bundle = getArguments();
        tvTitle.setText((String)bundle.get("title"));
        String contexttext =  bundle.get("content").toString().replace("<br>",System.getProperty("line.separator"));
        tvContent.setText(contexttext);
        tvWriter.setText((String)bundle.get("id"));
        String writer = (String)bundle.get("id");
        list = new ArrayList<>();
        Long l =bundle.getLong("regdate");
        rno = bundle.getInt("rno");
        Log.d("bum",l+"");
        Date date =  new Date(l);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        String text = sdf.format(date) + " ㆍ " + bundle.get("readcnt")+" 읽음";
        String imgpath = (String)bundle.get("imgpath");
        tvRegdate.setText(text);
        Log.d("bum","이미지 경로"+imgpath);
        if (imgpath !=null && !imgpath.equalsIgnoreCase("") && !imgpath.equalsIgnoreCase("null")){
            mProgressDialog = ProgressDialog.show(getContext(), "Wait", "이미지를 불러오는 중입니다.");
            Log.d("bum","이미지 경로널 아님");
            tack = new back();
            tack.execute(url+imgpath);
        }
        menu_img.setVisibility(View.GONE);
        sp = getActivity().getSharedPreferences("member",MODE_PRIVATE);
        sp2 = getActivity().getSharedPreferences("Admin",MODE_PRIVATE);
        String id2 = sp2.getString("name","");
        String id = sp.getString("name","");
        String matser =  sp2.getString("title","");
        Log.d("bum","====================="+writer);
        if (id.equalsIgnoreCase(writer) || id2.equalsIgnoreCase(writer) || matser.equalsIgnoreCase("사장")){
            menu_img.setVisibility(View.VISIBLE);
        }
        bno = bundle.getInt("bno");
        cno = bundle.getInt("cno");
        Log.d("bum","bno ====="+bno);
        String replyhttps = url+"/pool/restclassboard/readreply";
        new HttpRequestTack(getContext(),mHandler,new String[]{bno+""},new String[]{"bno"},"POST","댓글을 읽어오고 있습니다...").execute(replyhttps);

        return root;

}

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.menu_img:
                if(menu_linear.getVisibility() ==View.GONE){
                    menu_linear.setVisibility(View.VISIBLE);
                }else{
                    menu_linear.setVisibility(View.GONE);
                }
                break;
            case R.id.classboard_delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.AlertDialog);
                alert.setTitle("삭제");
                alert.setMessage("게시물을 삭제하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String deleteHttp = url+"/pool/restclassboard/delete";
                        Log.d("bum",bno+"");
                        new HttpRequestTack(getContext(),mHandler,new String[]{bno+""},new String[]{"bno"},"POST","게시글을 삭제하는중입니다...",2).execute(deleteHttp);
                        dialog.cancel();
                        dialog.dismiss();
                        ClassBoardFragment fgm = new ClassBoardFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("cno",cno+"");
                        FragmentTransaction tr =getActivity().getSupportFragmentManager().beginTransaction();
                        fgm.setArguments(bundle);
                        tr.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.exit);
                        tr.replace(R.id.frame, fgm);
                        tr.commit();

                    }
                }).setNegativeButton("취소",null);
                alert.create();
                alert.show();
                break;
            case R.id.reply_add_btn:

                String id2 = sp2.getString("name","");
                String id = sp.getString("name","");
                if ((id.equalsIgnoreCase("") || id==null) && (id2.equalsIgnoreCase("") || id2==null)){
                    Toast.makeText(getContext(),"로그인이 필요합니다.",Toast.LENGTH_SHORT).show();
                    break;
                }
                if (reply_text.getText().toString().equalsIgnoreCase("") || reply_text.getText().toString()==null){
                    Toast.makeText(getContext(),"댓글을 입력해주세요",Toast.LENGTH_SHORT).show();
                    break;
                }
                String replytext = reply_text.getText().toString();
                String insertHttp = url+"/pool/restclassboard/insertreply";
                if(id.equalsIgnoreCase("")){
                    new HttpRequestTack(getContext(),mHandler,new String[]{bno+"",replytext,id2},new String[]{"bno","replytext","id"},"POST","댓글을 작성중입니다.",3).execute(insertHttp);
                }else{
                    new HttpRequestTack(getContext(),mHandler,new String[]{bno+"",replytext,id},new String[]{"bno","replytext","id"},"POST","댓글을 작성중입니다.",3).execute(insertHttp);
                }

                break;
            case R.id.classboard_update:
                String[] arr = {bno+""};
                String[] arrname = {"bno"};
                String httpread = "http://rkd0519.cafe24.com/pool/restclassboard/read";
                new HttpRequestTack(getContext(), mHandler, arr, arrname, "POST", "정보를 가져오는 중입니다.", 4).execute(httpread);
                break;

        }
    }

    @Override
    public void onBack() {
        ClassBoardFragment fgm = new ClassBoardFragment();
        Bundle bundle = new Bundle();
        bundle.putString("cno",cno+"");
        FragmentTransaction tr =getActivity().getSupportFragmentManager().beginTransaction();
        fgm.setArguments(bundle);
        tr.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.exit);
        tr.replace(R.id.frame, fgm);
        tr.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity)context).setOnKeyBackPressedListener(this);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        sp = getActivity().getSharedPreferences("member",MODE_PRIVATE);
        sp2 = getActivity().getSharedPreferences("Admin",MODE_PRIVATE);
        String id2 = sp2.getString("name","");
        String id = sp.getString("name","");
        if ((id.equalsIgnoreCase("") || id==null) && (id2.equalsIgnoreCase("") || id2==null)){
            Toast.makeText(getContext(),"로그인이 필요합니다.",Toast.LENGTH_SHORT).show();
        }
    }



    private class back extends AsyncTask<String, Integer, Bitmap> {
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
                Log.d("bum","파일패스 "+filePath.getAbsolutePath());
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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1400);
            imgview.setLayoutParams(lp);
            imgview.setImageBitmap(rotate);
            mProgressDialog.dismiss();
        }
    }

    private Bitmap rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        Log.d("bum","===========시작");
        Log.d("bum","===========1");
        try {
            if (filePath != null) {
                Log.d("bum","===========2");
                exifInterface = new ExifInterface(filePath.getAbsolutePath());
                Log.d("bum","===========3");
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

    class MyListAdapter extends BaseAdapter {
        private static final String TAG = "MyListAdapter";
        private Context mContext;  //MyListAdapter 가 Activity가 아니므로
        private int mItemRowLayout; //항목레이아웃
        private LayoutInflater mInflater; //항목레이아웃을 전개할 전개자
        private List<ClassboardReply> arItem;

        public MyListAdapter(Context context, int itemRowLayout, List<ClassboardReply> list) {
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
            TextView content = convertview.findViewById(R.id.content);
            content.setText(arItem.get(position).getReplytext());

            TextView writer = convertview.findViewById(R.id.id);
            writer.setText(arItem.get(position).getId());

            Date d = arItem.get(position).getRegdate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = sdf.format(d);
            TextView date = convertview.findViewById(R.id.regdate);
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
                    Log.d("bum", "googogogoggoo"+result);
                    try {
                        JSONArray ja = new JSONArray(result);
                        getList(ja);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    String result2 = (String) msg.obj;
                    Log.d("bum", "googogogoggoo2"+result2);
                    break;
                case 3:
                    String result3 = (String) msg.obj;
                    Log.d("bum", "googogogoggoo3"+result3);
                    try {
                        reply_text.setText("");
                        //imm.hideSoftInputFromInputMethod(reply_text.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                        imm.toggleSoftInput(0,0);
                        JSONArray ja = new JSONArray(result3);
                        getList(ja);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    String result4 = (String)msg.obj;

                    Intent intent = new Intent(getContext(),ClassboardUpdateActivity.class);
                    intent.putExtra("bno",bno);
                    startActivity(intent);
                    break;
                case 5:
                    String result5 = (String) msg.obj;
                    Log.d("bum", "googogogoggoo3"+result5);
                    try {

                        reply_text.setText("");
                        imm.toggleSoftInput(0,0);
                        JSONArray ja = new JSONArray(result5);
                        getList(ja);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.cancel();
                    dialog.dismiss();
                    break;
                case 6:
                    String result6 = (String) msg.obj;
                    Log.d("bum", "googogogoggoo3"+result6);
                    try {
                        reply_text.setText("");
                        imm.toggleSoftInput(0,0);
                        JSONArray ja = new JSONArray(result6);
                        getList(ja);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.cancel();
                    dialog.dismiss();
                    break;
            }
        }
    };
    private void getList(JSONArray ja){
        try{
            listview.removeAllViews();
            if (ja.length() > 0) {
                for (int j = 0; j < ja.length(); j++) {
                    JSONObject order = ja.getJSONObject(j);
                    ClassboardReply reply = new ClassboardReply();
                    reply.setRno(order.getInt("rno"));
                    reply.setId(order.getString("id"));
                    reply.setReplytext(order.getString("replytext"));
                    Date date = new Date(order.getLong("regdate"));
                    reply.setRegdate(date);
                    list.add(reply);
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.class_reply,null);

                    ImageView imageView  = view.findViewById(R.id.reply_update);
                    //imgview.setText
                    final int finalJ = j;
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDialog = new AlertDialog.Builder(getContext());
                            View v = getLayoutInflater().inflate(R.layout.reply_update,null);

                            final EditText reply = v.findViewById(R.id.etreply);
                            final Button deleteBtn = v.findViewById(R.id.deleteBtn);
                            final Button okBtn = v.findViewById(R.id.okBtn);
                            final Button updateBtn = v.findViewById(R.id.updateBtn);
                            reply.setText(list.get(finalJ).getReplytext());
                            reply.requestFocus();
                            mDialog.setView(v);
                            dialog = mDialog.create();
                            dialog.show();
                            deleteBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.AlertDialog);
                                    alert.setTitle("삭제");
                                    alert.setMessage("댓글을 삭제하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String deleteHttp = url+"/pool/restclassboard/deletereply";
                                            Log.d("bum",bno+"");
                                            new HttpRequestTack(getContext(),mHandler,new String[]{list.get(finalJ).getRno()+"",bno+""},new String[]{"rno","bno"},"POST","댓글을 삭제하는중입니다...",5).execute(deleteHttp);
                                            dialog.cancel();
                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton("취소",null);
                                    alert.create();
                                    alert.show();
                                }
                            });
                            okBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            });
                            updateBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String replytext = reply.getText().toString();
                                    int rno = list.get(finalJ).getRno();
                                    String[] arr = {replytext,rno+"",bno+""};
                                    String[] arrName = {"replytext","rno","bno"};
                                    String updateHttp = url+"/pool/restclassboard/updatereply";
                                    new HttpRequestTack(getContext(),mHandler,arr,arrName,"POST","댓글을 수정하는중입니다...",6).execute(updateHttp);
                                }
                            });
                        }
                    });
                    TextView content = view.findViewById(R.id.content);
                    content.setText(order.getString("replytext"));
                    TextView writer = view.findViewById(R.id.id);
                    writer.setText(order.getString("id"));
                    sp = getActivity().getSharedPreferences("member",MODE_PRIVATE);
                    sp2 = getActivity().getSharedPreferences("Admin",MODE_PRIVATE);
                    String id2 = sp2.getString("name","");
                    String id = sp.getString("name","");
                    String title = sp2.getString("title","");
                    if ((!id.equalsIgnoreCase(writer.getText().toString()) || id==null) && (!id2.equalsIgnoreCase(writer.getText().toString()) || id2==null)){
                        imageView.setVisibility(View.GONE);
                    }
                    if(title.equalsIgnoreCase("사장")){
                        imageView.setVisibility(View.VISIBLE);
                    }
                    TextView regdate = view.findViewById(R.id.regdate);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = sdf.format(date);
                    regdate.setText(strDate);

                    listview.addView(view);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
