package kr.or.dgit.bigdata.pool;

import android.Manifest;
import android.annotation.SuppressLint;
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.or.dgit.bigdata.pool.dto.ClassBoard;
import kr.or.dgit.bigdata.pool.fragment.ClassBoardInsert;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class ClassBoardInsertActivity extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener{
    CustomDialog customDialog;
    File filePath;
    int reqWidth;
    int reqHeight;
    String galleryPath;
    String http = "http://192.168.123.113:8080/pool/restclassboard/";
    ImageView img_btn;
    ImageView imgSelect;
    EditText etcontent;
    EditText etTitle;
    TextView tvclass;
    String time;
    String level;
    ImageView check_Class;
    ImageView check_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_board_insert);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.classboardtitle);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        img_btn = findViewById(R.id.img_btn);
        img_btn.setOnClickListener(this);
        imgSelect = findViewById(R.id.selectImg);
        reqWidth = getResources().getDimensionPixelSize(R.dimen.request_image_width);
        reqHeight = getResources().getDimensionPixelSize(R.dimen.request_image_height);
        etcontent = findViewById(R.id.etcontent);
        tvclass = findViewById(R.id.tvclassselect);
        tvclass.setOnClickListener(this);
        check_Class = findViewById(R.id.check_Class);
        etTitle = findViewById(R.id.ettitle);
        etTitle.setOnFocusChangeListener(this);
        check_title = findViewById(R.id.check_title);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() ==R.id.img_btn){
            customDialog = new CustomDialog(ClassBoardInsertActivity.this);
            customDialog.show();
        }else if(view.getId() ==R.id.tvclassselect){
            Log.d("bum","tvclass click");
            new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("시간을 선택해주세요")
                    .setItems(R.array.classboard_selected, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String[] arrays = getResources().getStringArray(R.array.classboard_selected);
                            time = arrays[i];
                            new AlertDialog.Builder(ClassBoardInsertActivity.this)
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setTitle("시간을 선택해주세요")
                                    .setItems(R.array.classboard_level,new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String[] arrays2 = getResources().getStringArray(R.array.classboard_level);
                                            level = arrays2[i];
                                            tvclass.setText(time+"/"+level);
                                            check_Class.setImageResource(R.drawable.circle_2);
                                        }
                                    })
                                    .setNegativeButton("취소",null).create().show();
                        }
                    })
                    .setNegativeButton("취소", null).create().show();
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(view.getId()==R.id.ettitle){
            Log.d("bum","포커스");
            if(!etTitle.getText().toString().equalsIgnoreCase("")){
                check_title.setImageResource(R.drawable.circle_2);
            }
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
                    ActivityCompat.requestPermissions(ClassBoardInsertActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }
            } else if (view.getId() == R.id.gallery) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "갤러리", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, 20);
                } else {
                    ActivityCompat.requestPermissions(ClassBoardInsertActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }

            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.classboardinsert, menu);
        return super.onCreateOptionsMenu(menu);
    }
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
           // Bitmap rotated = rotateImage(bitmap);
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
        Cursor cursor = ClassBoardInsertActivity.this.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
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
            Toast t = Toast.makeText(this,"HOME AS UP Click",Toast.LENGTH_SHORT);
            t.show();
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
                        Message msg = Message.obtain(mhandler,1);
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
                        Message msg = Message.obtain(mhandler,1);
                        //  msg.set
                        mhandler.sendMessage(msg);
                    }
                }.start();
                return true;
            }
            insert();
            return true;
        }
        return super.onContextItemSelected(item);
    }
    @SuppressLint("HandlerLeak")
    Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(ClassBoardInsertActivity.this,"업로드 성공",Toast.LENGTH_SHORT).show();
                    insert();
                    break;
                case 2:
                    String result = (String) msg.obj;
                    Log.d("bum", "============= " + result);
                    break;
            }
        }
    };
    private void insert(){
        SharedPreferences sp = getSharedPreferences("member",MODE_PRIVATE);
        String id = sp.getString("name","");
        String contentReplace = etcontent.getText().toString();
        String content = contentReplace.replace(System.getProperty("line.separator"),"<br>");
        String title = etTitle.getText().toString();
        String imgPathcheck = null;
        if(filePath !=null){
            imgPathcheck =  "/pool/resources/upload/classboard/"+filePath.getName();
        }else if(galleryPath !=null){
            imgPathcheck =  "/pool/resources/upload/classboard/"+galleryPath;
        }
        String[] arrname = new String[]{"time","level","id","content","title","imgPathcheck"};
        String[] arr = {time,level,id,content,title,imgPathcheck};

        String httpclasstime = http + "insert";
        new HttpRequestTack(this, mhandler, arr, arrname, "POST", "글을 작성합니다.",2).execute(httpclasstime);
    }
}
