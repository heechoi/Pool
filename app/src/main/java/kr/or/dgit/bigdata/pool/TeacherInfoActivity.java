package kr.or.dgit.bigdata.pool;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.or.dgit.bigdata.pool.dto.ClassBoard;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class TeacherInfoActivity extends AppCompatActivity {
    private ImageView tImage;
    private TextView no;
    private TextView basic;
    private TextView name;
    private File filePath;
    private Bitmap bmImg;
    private Bitmap rotate;
    int tno;
    private back tack;
    private String imgUrl = "http://192.168.0.239:8080";
    private String http ="http://192.168.0.239:8080/pool/restInfoUpdate/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);
        SharedPreferences t = getSharedPreferences("Admin",MODE_PRIVATE);
        tno = t.getInt("tno",0);

        String isleave = http+"findTeacher";

        String[] arrQueryname ={"tno"};
        String[] arrQuery={String.valueOf(tno)};

        tImage = findViewById(R.id.tImage);
        no = findViewById(R.id.tno);
        basic = findViewById(R.id.basic_info);
        name = findViewById(R.id.name);

        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","강사정보 가져오는 중..",1);
        httpRequestTack.execute(isleave);

    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1: {
                    String result = (String) msg.obj;
                    try{
                        JSONObject object = new JSONObject(result);

                        no.setText(object.getInt("tno")+"");
                        name.setText(object.getString("name"));
                        String imgpath = object.getString("img_path");
                        if(!imgpath.equals("null")&&!imgpath.equals("")){
                            tack = new back();
                            tack.execute(imgUrl+imgpath);
                        }
                    }catch (Exception e){

                    }
                }
                break;
            }
        }
    };

    public void clickCheckEmail(View view) {
    }

    public void clickChangePw(View view) {
    }


    private class back extends AsyncTask<String, Integer, Bitmap> {

        ProgressDialog mProgressDialog = null;


        @Override
        protected void onPreExecute() {

            mProgressDialog = ProgressDialog.show(TeacherInfoActivity.this, "Wait", "이미지를 불러오는 중입니다.");
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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(300,400);
            tImage.setLayoutParams(lp);
            tImage.setImageBitmap(rotate);
            mProgressDialog.dismiss();

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
