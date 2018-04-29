package kr.or.dgit.bigdata.pool;

import android.*;
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
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.or.dgit.bigdata.pool.dto.ClassBoard;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;
import static android.speech.tts.TextToSpeech.ERROR;

public class TeacherInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView tImage;
    private TextView no;
    private TextView basic;
    private TextView name;
    private File filePath;
    private Bitmap bmImg;
    private Bitmap rotate;
    private TextView tell;
    private ImageView showPw;
    private ImageView showTell;
    private ScrollView scrollView;
    int tno;
    private EditText tell1;
    private EditText tell2;
    private EditText tell3;
    private TextView title;
    private EditText nowPw;
    private EditText newPw;
    private EditText newPw2;
    String id;
    private Button changeInfo;
    private EditText editInfo;
    private Button storeInfo;
    private Button back;
    private LinearLayout btnLayout;
    private back tack;
    private EditText check;
    private TextView auto;
    private Button refresh;
    private Button voice;
    private TextToSpeech speech;
    String galleryPath;
    CustomDialog customDialog;

    private String imgUrl = "http://192.168.123.113:8080";
    private String http ="http://192.168.123.113:8080/pool/restInfoUpdate/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);
        SharedPreferences t = getSharedPreferences("Admin",MODE_PRIVATE);
        tno = t.getInt("tno",0);

        String findHttp = http+"findTeacher";

        String[] arrQueryname ={"tno"};
        String[] arrQuery={String.valueOf(tno)};

        tImage = findViewById(R.id.tImage);
        no = findViewById(R.id.tno);
        scrollView = findViewById(R.id.scroll_view);
        basic = findViewById(R.id.basic_info);
        basic.setMovementMethod(new ScrollingMovementMethod());
        basic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;

            }

        });
        speech = new TextToSpeech(this,new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status !=ERROR){
                    speech.setLanguage(Locale.KOREA);
                }
            }
        });
        check =  findViewById(R.id.check);
        auto = findViewById(R.id.auto);
        refresh = findViewById(R.id.refresh);
        voice = findViewById(R.id.voice);

        editInfo = findViewById(R.id.editInfo);
        editInfo.setMovementMethod(new ScrollingMovementMethod());
        editInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        storeInfo = findViewById(R.id.storeInfo);
        back = findViewById(R.id.back);
        btnLayout = findViewById(R.id.btn_layout);
        name = findViewById(R.id.name);

        tell = findViewById(R.id.tell);
        showTell = findViewById(R.id.show_tell);
        showTell.setOnClickListener(this);
        tell1 = findViewById(R.id.tell1);
        tell2 = findViewById(R.id.tell2);
        tell3 = findViewById(R.id.tell3);

        title  = findViewById(R.id.title);

        showPw = findViewById(R.id.show_pw);
        showPw.setOnClickListener(this);

        nowPw=findViewById(R.id.nowPw);
        newPw = findViewById(R.id.newPw);
        newPw2 = findViewById(R.id.newPw2);

        changeInfo = findViewById(R.id.changeInfo);
        changeInfo.setOnClickListener(this);

        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","강사정보 가져오는 중..",1);
        httpRequestTack.execute(findHttp);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(speech !=null){
            speech.stop();
            speech.shutdown();
            speech=null;
        }
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

                        String ttell = object.getString("tell");
                        String t1 = ttell.substring(0,ttell.indexOf("-")+1);
                        String t2 = ttell.substring(ttell.indexOf("-")+1,ttell.indexOf("-")+2)+"***-";
                        String t3 = ttell.substring(ttell.lastIndexOf("-")+1,ttell.lastIndexOf("-")+2)+"***";
                        title.setText(object.getString("title"));

                        tell.setText(t1+t2+t3);

                        if(!imgpath.equals("null")&&!imgpath.equals("")){
                            tack = new back();
                            tack.execute(imgUrl+imgpath);
                        }

                        if(!object.getString("info").equals("null")){
                            basic.setText(object.getString("info"));
                        }else{
                            basic.setText("");
                        }
                        id = object.getString("id");

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                case 2:{
                    String result = (String) msg.obj;
                    if(result.equals("update")){
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(TeacherInfoActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("연락처 변경");
                        alert.setMessage("연락처가 변경 되었습니다");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                finish();
                                startActivity(getIntent());
                            }
                        });
                        alert.show();
                    }
                }
                case 3:{
                    String result = (String) msg.obj;
                    if(result.equals("no")){
                        Toast toast = Toast.makeText(getApplicationContext(),"기존 비밀번호가 일치하지 않습니다\n비밀번호를 확인해 주세요",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        nowPw.setText("");
                        nowPw.requestFocus();
                        newPw.setText("");
                        newPw2.setText("");
                        toast.show();
                    }else if(result.equals("update")){
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(TeacherInfoActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("비밀번호 변경");
                        alert.setMessage("비밀번호가 변경 되었습니다");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                finish();
                                startActivity(getIntent());
                            }
                        });
                        alert.show();
                    }

                }
                case 4:{
                    String result = (String) msg.obj;
                    if(result.equals("success")){
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(TeacherInfoActivity.this, R.style.SearchAlertDialog);
                        alert.setTitle("정보 변경");
                        alert.setMessage("졍보가 변경 되었습니다");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialogInterface.dismiss();
                                finish();
                                startActivity(getIntent());
                            }
                        });
                        alert.show();
                    }
                }
                case 5:{
                    String result = (String) msg.obj;
                    String imgpath = "";
                    if(filePath !=null){
                        imgpath =  "/pool/resources/upload/classboard/"+filePath.getName();
                    }else if(galleryPath !=null){
                        imgpath =  "/pool/resources/upload/classboard/"+galleryPath;
                    }
                    Log.d("bum","=============="+imgpath);
                    String[] arr = {tno+"",imgpath};
                    String[] arrname = {"tno","imgpath"};
                    String updateimgpath = http+"updateImgpath";
                    HttpRequestTack httpRequestTack = new HttpRequestTack(TeacherInfoActivity.this,mHandler,arr,arrname,"POST","비밀번호 변경중..",6);
                    httpRequestTack.execute(updateimgpath);
                }
                case 6:{
                    String result = (String) msg.obj;
                    Log.d("bum",result);
                }
                break;
            }
        }
    };

    public void clickChangePw(View view) {
        if(nowPw.getText().toString().isEmpty()||newPw.getText().toString().isEmpty()||newPw2.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(this,"비밀번호를 모두 입력해주세요",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }

        Pattern patternPw = Pattern.compile("(^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*])[A-Za-z0-9~!@#$%^&*]{8,20}$)");
        Matcher matcherPw = patternPw.matcher(newPw2.getText().toString());

        if(!matcherPw.find()){
            AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.AlertDialog);
            alert.setTitle("비밀번호 사용 불가");
            alert.setMessage("비밀번호 정규식이 일치하지 않습니다\n비밀번호는8~20자,최소한 한번 이상의 영문,숫자,특수문자(~!@#$%^&*)만 허용됩니다").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    newPw.setText("");
                    newPw2.setText("");
                    newPw.requestFocus();
                    dialog.cancel();
                    dialog.dismiss();
                }
            });
            alert.create();
            alert.show();
            return;
        }

        if(!newPw.getText().toString().equals(newPw2.getText().toString())){
            Toast toast = Toast.makeText(this,"비밀번호가 일치하지 않습니다\n비밀번호를 확인해주세요",Toast.LENGTH_SHORT);
            newPw.requestFocus();
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        if(check.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(this,"자동입력방지를 입력해주세요",Toast.LENGTH_SHORT);
            check.requestFocus();
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        if(!auto.getText().toString().equals(check.getText().toString())){
            Toast toast = Toast.makeText(this,"자동입력방지가 일치하지 않습니다",Toast.LENGTH_SHORT);
            check.requestFocus();
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }

        String updatePw = http+"tupdatePw";
        String[] arrQuery = {id,nowPw.getText().toString(),newPw.getText().toString()};
        String[] arrQueryname = {"id","pw","newPw"};
        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","비밀번호 변경중..",3);
        httpRequestTack.execute(updatePw);


    }

    public void clickCheckTell(View view) {
        if(tell1.getText().toString().isEmpty()||tell2.getText().toString().isEmpty()||tell3.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(this,"연락처를 모두 입력해 주세요",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        Pattern patternTell = Pattern.compile("(^[0-9]{3,4}$)");
        Matcher matcherTell1 = patternTell.matcher(tell1.getText().toString());
        Matcher matcherTell2 = patternTell.matcher(tell2.getText().toString());
        Matcher matcherTell3 = patternTell.matcher(tell3.getText().toString());

        if(!matcherTell1.find()||!matcherTell2.find()||!matcherTell3.find()){
            Toast toast = Toast.makeText(this,"연락처가 형식에 맞지 않습니다\n연락처를 확인해주세요",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            tell1.requestFocus();
            return;
        }

        String tell = tell1.getText().toString()+"-"+tell2.getText().toString()+"-"+tell3.getText().toString();
        String tellHttp = http+"tupdateTell";
        String[] arrQuery ={no.getText().toString(),tell};
        String[] arrQueryname ={"tno","tell"};
        HttpRequestTack httpRequestTack = new HttpRequestTack(this,mHandler,arrQuery,arrQueryname,"POST","연락처 변경 중..",2);
        httpRequestTack.execute(tellHttp);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.show_tell){
            LinearLayout layout = findViewById(R.id.change_tell_layout);
            EditText editText = layout.findViewById(R.id.tell1);
            if(layout.getVisibility()==View.VISIBLE){
                showTell.setImageResource(R.drawable.arrow_down);
                layout.setVisibility(View.GONE);
                editText.setFocusable(false);
            }else if(layout.getVisibility()==View.GONE){
                showTell.setImageResource(R.drawable.arrow_up);
                layout.setVisibility(View.VISIBLE);
                editText.requestFocus();
            }
        }

        if(view.getId()==R.id.show_pw){
            LinearLayout layout = findViewById(R.id.change_layout);
            EditText editText = layout.findViewById(R.id.nowPw);
            if(layout.getVisibility() ==View.VISIBLE){
                showPw.setImageResource(R.drawable.arrow_down);
                layout.setVisibility(View.GONE);
                editText.setFocusable(false);
            }else if(layout.getVisibility()==View.GONE){
                showPw.setImageResource(R.drawable.arrow_up);
                layout.setVisibility(View.VISIBLE);
                editText.requestFocus();

            }
        }

        if(view.getId()==R.id.changeInfo){
            basic.setVisibility(View.GONE);
            changeInfo.setVisibility(View.GONE);
            editInfo.setVisibility(View.VISIBLE);
           btnLayout.setVisibility(View.VISIBLE);
           editInfo.setText(basic.getText().toString());
        }

    }

    public void clickBack(View view) {
        basic.setVisibility(View.VISIBLE);
        btnLayout.setVisibility(View.GONE);
        editInfo.setVisibility(View.GONE);
        changeInfo.setVisibility(View.VISIBLE);
    }

    public void clickStore(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.AlertDialog);
        alert.setTitle("정보 변경");
        alert.setMessage("내정보를 수정하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updateInfo = http+"updateInfo";


                String[] arrQuery ={no.getText().toString(),editInfo.getText().toString()};
                String[] arrQueryname ={"tno","info"};

                HttpRequestTack httpRequestTack = new HttpRequestTack(TeacherInfoActivity.this,mHandler,arrQuery,arrQueryname,"POST","정보 변경 중..",4);
                httpRequestTack.execute(updateInfo);


                basic.setVisibility(View.VISIBLE);
                btnLayout.setVisibility(View.GONE);
                editInfo.setVisibility(View.GONE);
                changeInfo.setVisibility(View.VISIBLE);

                dialog.cancel();
                dialog.dismiss();
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                dialogInterface.cancel();
            }
        });
        alert.create();
        alert.show();

    }

    public void clickPhoto(View view) {
        //프로필 사진 변경
        customDialog = new CustomDialog(TeacherInfoActivity.this);
        customDialog.show();
    }

    @Override
    public void finish() {
        super.finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    public void clickVoice(View view) {
        speech.setPitch(1.0f);
        speech.setSpeechRate(0.5f);
        speech.speak(auto.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.AlertDialog);
        alert.setTitle("개인정보 수정");
        alert.setMessage("개인정보 수정을 종료하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
                finish();
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                dialogInterface.dismiss();
            }
        });
        alert.create();
        alert.show();

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
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
                    ActivityCompat.requestPermissions(TeacherInfoActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }
            } else if (view.getId() == R.id.gallery) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "갤러리", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, 20);
                } else {
                    ActivityCompat.requestPermissions(TeacherInfoActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }
            }
        }
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
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(300,400);

                Bitmap bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
                Bitmap rotated = rotateImage(bitmap);

                tImage.setImageBitmap(rotated);
                tImage.setLayoutParams(lp);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tImage.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
                }
                new Thread(){
                    public void run(){

                        DoFileUpload(http+"upload",filePath.getAbsolutePath());
                        Bundle bun = new Bundle();
                        bun.putString("upload","goodgood");
                        Message msg = Message.obtain(mHandler,5);
                        //  msg.set
                        mHandler.sendMessage(msg);
                    }
                }.start();
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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(300,400);

            Bitmap bitmap = BitmapFactory.decodeFile(galleryPath);
            Matrix m = new Matrix();

            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            tImage.setImageBitmap(rotated);
            tImage.setLayoutParams(lp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tImage.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
            }
            new Thread(){
                public void run(){

                    DoFileUpload(http+"upload",filePath.getAbsolutePath());
                    Bundle bun = new Bundle();
                    bun.putString("upload","goodgood");
                    Message msg = Message.obtain(mHandler,5);
                    //  msg.set
                    mHandler.sendMessage(msg);
                }
            }.start();
        }
    }
    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = TeacherInfoActivity.this.getContentResolver().query(contentUri, proj, null, null, null);
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
}
