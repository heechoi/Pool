package kr.or.dgit.bigdata.pool.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.Date;

import kr.or.dgit.bigdata.pool.MainActivity;
import kr.or.dgit.bigdata.pool.R;
import kr.or.dgit.bigdata.pool.onKeyBackPressedListener;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

/**
 * Created by DGIT3-4 on 2018-04-30.
 */

public class QnaReadFragement extends Fragment implements onKeyBackPressedListener {
    int bno;
    ProgressDialog mProgressDialog;
    private String http ="http://rkd0519.cafe24.com/pool/restQna/";
    private String imgHttp ="http://rkd0519.cafe24.com";

    File filePath;
    View root;
    TextView title;
    TextView regdate;
    TextView qnaContent;
    TextView replydate;
    TextView replyContent;
    LinearLayout replyLayout;
    ImageView imgView;

    Bitmap bmImg;
    back back;
    Bitmap rotate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.qna_read,container,false);
        title = root.findViewById(R.id.title);
        imgView = root.findViewById(R.id.imgview);
        regdate = root.findViewById(R.id.regdate);
        qnaContent = root.findViewById(R.id.qnaContent);
        replydate = root.findViewById(R.id.replyDate);
        replyContent = root.findViewById(R.id.replyContent);
        replyLayout = root.findViewById(R.id.reply);

        Bundle bundle = getArguments();
        bno = bundle.getInt("bno");

        new HttpRequestTack(getContext(), mHandler, "GET", "정보를 가져오고 있습니다...").execute(http + "read?bno=" + bno);
        return root;
    }

    @Override
    public void onBack() {
        QnaListFragment fgm = new QnaListFragment();
        Bundle bundle = new Bundle();
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

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String result = (String) msg.obj;
                    Log.d("list",result);
                    try {

                        JSONObject order = new JSONObject(result);

                        title.setText((String) order.get("title"));
                        String contexttext =  (String) order.get("content").toString().replace("<br>",System.getProperty("line.separator"));
                        qnaContent.setText(contexttext);

                        Date d = new Date((long) order.get("regdate"));
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
                        String text = sdf.format(d);
                        regdate.setText(text);

                       if(order.getBoolean("replycheck")==false){
                             replyLayout.setVisibility(View.GONE);
                        }else{
                            replyLayout.setVisibility(View.VISIBLE);
                            Date date = new Date((long) order.get("answerdate"));
                            SimpleDateFormat sd = new SimpleDateFormat("yyyy년 MM월 dd일");
                            replydate.setText( sd.format(date));

                            String str =  (String) order.get("answer").toString().replace("<br />",System.getProperty("line.separator"));
                            replyContent.setText(str);
                        }


                        String imgpath =order.getString("imgpath");

                        if (imgpath !=null && !imgpath.equalsIgnoreCase("") && !imgpath.equalsIgnoreCase("null")){
                            mProgressDialog = ProgressDialog.show(getContext(), "Wait", "이미지를 불러오는 중입니다.");
                            Log.d("bum","이미지 경로널 아님");
                            back = new back();
                            back.execute(imgHttp+imgpath);
                        }


                    } catch (JSONException e) {
                        Log.i("Json_parser", e.getMessage());
                    }
                    break;
            }
        }
    };

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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1000);
            imgView.setLayoutParams(lp);
            imgView.setImageBitmap(rotate);
            mProgressDialog.dismiss();
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

}
