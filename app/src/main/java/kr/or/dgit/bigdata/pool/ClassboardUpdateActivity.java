package kr.or.dgit.bigdata.pool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.or.dgit.bigdata.pool.fragment.ClassBoardRead;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class ClassboardUpdateActivity extends AppCompatActivity implements View.OnClickListener{
    CustomDialog customDialog;
    int reqWidth;
    int reqHeight;
    String galleryPath;
    File filePath;
    String http = "http://rkd0519.cafe24.com/pool/restclassboard/";
    private String imgUrl = "http://rkd0519.cafe24.com";
    ImageView img_btn;
    ImageView imgSelect;
    EditText etcontent;
    EditText etTitle;
    TextView tvclass;
    String original_imgPath;
    String time;
    String level;
    private Bitmap bmImg;
    private Bitmap rotate;
    int bno;
    SharedPreferences sp;
    SharedPreferences sp2;
    back tack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classboard_update);
        Intent intent = getIntent();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.classboard_update_title);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        bno = intent.getIntExtra("bno",0);
        Log.d("bum5",bno+"");
        img_btn = findViewById(R.id.img_btn);
        img_btn.setOnClickListener(this);
        imgSelect = findViewById(R.id.selectImg);
        reqWidth = getResources().getDimensionPixelSize(R.dimen.request_image_width);
        reqHeight = getResources().getDimensionPixelSize(R.dimen.request_image_height);
        etcontent = findViewById(R.id.etcontent);
        tvclass = findViewById(R.id.tvclassselect);
        tvclass.setOnClickListener(this);
        etTitle = findViewById(R.id.ettitle);
        String read = http+"readupdate";
        new HttpRequestTack(this,mHandler,new String[]{bno+""},new String[]{"bno"},"POST","정보를 가져오는 중입니다.").execute(read);
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
                        JSONObject jObj = new JSONObject(result);
                        JSONObject classboard = jObj.getJSONObject("vo");
                        etTitle.setText(classboard.getString("title"));
                        String contentText =  classboard.getString("content").replace("<br>",System.getProperty("line.separator"));
                        etcontent.setText(contentText);
                        JSONObject cls = jObj.getJSONObject("class");
                        time = cls.getString("time");
                        level = cls.getString("level");
                        String clstype = time + "/" +level;
                        tvclass.setText(clstype);
                        Log.d("bum","================img"+classboard.getString("imgpath"));
                        if(!classboard.getString("imgpath").equalsIgnoreCase("") && !classboard.getString("imgpath").equalsIgnoreCase("null")){
                            original_imgPath = classboard.getString("imgpath");
                            tack = new back();
                            tack.execute(imgUrl+original_imgPath);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    updateClassboard();
                    break;
                case 3:
                    break;

            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.classbardupdate, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.tvclassselect){
            etTitle.clearFocus();
            new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("시간을 선택해주세요")
                    .setItems(R.array.classboard_selected, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String[] arrays = getResources().getStringArray(R.array.classboard_selected);
                            time = arrays[i];
                            new AlertDialog.Builder(ClassboardUpdateActivity.this)
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setTitle("시간을 선택해주세요")
                                    .setItems(R.array.classboard_level,new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String[] arrays2 = getResources().getStringArray(R.array.classboard_level);
                                            level = arrays2[i];
                                            tvclass.setText(time+"/"+level);
                                        }
                                    })
                                    .setNegativeButton("취소",null).create().show();
                        }
                    })
                    .setNegativeButton("취소", null).create().show();
        }else if(view.getId() ==R.id.img_btn){
            customDialog = new CustomDialog(ClassboardUpdateActivity.this);
            customDialog.show();
        }
    }

    public class CustomDialog extends android.app.AlertDialog implements View.OnClickListener {
        Button camaraBtn;
        Button galleryBtn;

        public CustomDialog(@NonNull Context context) {
            super(context);
        }

        public CustomDialog(Context context, int themeResId) {
            super(context, themeResId);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.custom_dialog);
            camaraBtn = findViewById(R.id.camara);
            galleryBtn = findViewById(R.id.gallery);
            camaraBtn.setOnClickListener(this);
            galleryBtn.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.camara) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pool";
                        File dir = new File(dirPath);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }

                        filePath = File.createTempFile("IMG", ".jpg", dir);
                        if (!filePath.exists()) {
                            filePath.createNewFile();
                        }
                        Uri photoURI = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", filePath);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(intent, 10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(ClassboardUpdateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }
            } else if (view.getId() == R.id.gallery) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "갤러리", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, 20);
                } else {
                    ActivityCompat.requestPermissions(ClassboardUpdateActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("작성중인 내용을 저장하지 않고 나가시겠습니까?");
        builder.setPositiveButton("확인",dialogListener);
        builder.setNegativeButton("취소",dialogListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int i) {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.login,R.anim.login_out);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(ClassboardUpdateActivity.this,"업로드 성공",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    updateClassboard();

                    break;
                case 3:
                    Log.d("bum","update 성공");
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("bno",bno+"");
                    overridePendingTransition(R.anim.login,R.anim.login_out);
                    startActivity(intent);

                    break;
            }
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == this.RESULT_OK) {

            if (filePath != null) {
                customDialog.dismiss();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                galleryPath = null;
                try {
                    InputStream in = new FileInputStream(filePath);
                    BitmapFactory.decodeStream(in, null, options);
                    in.close();
                    in = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, reqHeight);

                Bitmap bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
                Bitmap rotated = rotateImage(bitmap);

                imgSelect.setImageBitmap(rotated);
                imgSelect.setLayoutParams(lp);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    imgSelect.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
                }
            }
        } else if (requestCode == 20 && resultCode == this.RESULT_OK) {
            customDialog.dismiss();
            filePath = null;
            galleryPath = getRealPathFromURI(data.getData());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, reqHeight);

            Bitmap bitmap = BitmapFactory.decodeFile(galleryPath);
            Matrix m = new Matrix();

            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            imgSelect.setImageBitmap(rotated);
            imgSelect.setLayoutParams(lp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imgSelect.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    private Bitmap rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        try {
            if (filePath != null) {
                exifInterface = new ExifInterface(filePath.getAbsolutePath());
            } else {
                exifInterface = new ExifInterface(galleryPath);
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
    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = ClassboardUpdateActivity.this.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    private  void updateClassboard(){
        SharedPreferences sp = getSharedPreferences("member",MODE_PRIVATE);
        String id = sp.getString("name","");
        String contentReplace = etcontent.getText().toString();
        String content = contentReplace.replace(System.getProperty("line.separator"),"<br>");
        String title = etTitle.getText().toString();
        String imgPathcheck = null;
        if(filePath !=null){
            imgPathcheck =  "/pool/resources/upload/teacher/"+filePath.getName();
        }else if(galleryPath !=null){
            imgPathcheck =  "/pool/resources/upload/teacher/"+galleryPath;
        }
        String[] arrname = new String[]{"time","level","id","content","title","imgPathcheck","oriImgPath","bno"};
        String[] arr = {time,level,id,content,title,imgPathcheck,original_imgPath,bno+""};

        String httpclasstime = http + "update";
        new HttpRequestTack(this, mhandler, arr, arrname, "POST", "글을 작성합니다.",3).execute(httpclasstime);
    }

    private class back extends AsyncTask<String, Integer, Bitmap> {

        ProgressDialog mProgressDialog = null;


        @Override
        protected void onPreExecute() {

            mProgressDialog = ProgressDialog.show(ClassboardUpdateActivity.this, "Wait", "이미지를 불러오는 중입니다.");
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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, reqHeight);
            imgSelect.setLayoutParams(lp);
            imgSelect.setImageBitmap(rotate);
            mProgressDialog.dismiss();

        }
    }
    public void DoFileUpload(String apiUrl, String absolutePath) {

        HttpFileUpload(apiUrl, "", absolutePath);

    }
    public void HttpFileUpload(String urlString, String params, String fileName) {


        String lineEnd = "\r\n";

        String twoHyphens = "--";

        String boundary = "*****";

        try {

            File sourceFile = new File(fileName);

            DataOutputStream dos;

            if (!sourceFile.isFile()) {

                Log.e("uploadFile", "Source File not exist :" + fileName);

            } else {

                FileInputStream mFileInputStream = new FileInputStream(sourceFile);

                URL connectUrl = new URL(urlString);

                // open connection

                HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();

                conn.setDoInput(true);

                conn.setDoOutput(true);

                conn.setUseCaches(false);

                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");

                conn.setRequestProperty("ENCTYPE", "multipart/form-data");

                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                conn.setRequestProperty("uploaded_file", fileName);

                // write data

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);
                int bytesAvailable = mFileInputStream.available();
                int maxBufferSize = 1024 * 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = mFileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                }


                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                mFileInputStream.close();
                dos.flush(); // finish upload...
                if (conn.getResponseCode() == 200) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                }

                mFileInputStream.close();
                dos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("작성중인 내용을 저장하지 않고 나가시겠습니까?");
            builder.setPositiveButton("확인",dialogListener);
            builder.setNegativeButton("취소",dialogListener);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }else if(item.getItemId() ==R.id.action_search){
            String content = etcontent.getText().toString();

            if(content.equalsIgnoreCase("")){
                Toast.makeText(this,"내용을 입력해주세요.",Toast.LENGTH_SHORT).show();
                return false;
            }else if(etTitle.getText().toString().equalsIgnoreCase("")){
                Toast.makeText(this,"제목을 입력해주세요.",Toast.LENGTH_SHORT).show();
                return false;
            }else if(tvclass.getText().toString().equalsIgnoreCase("반을 선택해주세요.")){
                Toast.makeText(this,"반을 선택해주세요.",Toast.LENGTH_SHORT).show();
                return false;
            }
            if(galleryPath !=null){
                new Thread(){
                    public void run(){
                        DoFileUpload(http+"upload",galleryPath);
                        Bundle bun = new Bundle();
                        bun.putString("upload","goodgood");
                        Message msg = Message.obtain(mhandler,2);
                        //  msg.set
                        mhandler.sendMessage(msg);
                    }
                }.start();
                return true;
            }else if(filePath !=null){
                Log.d("bum",filePath.getName());
                new Thread(){
                    public void run(){

                        DoFileUpload(http+"upload",filePath.getAbsolutePath());
                        Bundle bun = new Bundle();
                        bun.putString("upload","goodgood");
                        Message msg = Message.obtain(mhandler,2);
                        //  msg.set
                        mhandler.sendMessage(msg);
                    }
                }.start();
                return true;
            }
            updateClassboard();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
