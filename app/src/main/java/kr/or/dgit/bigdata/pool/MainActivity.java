package kr.or.dgit.bigdata.pool;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import kr.or.dgit.bigdata.pool.dto.Teacher;
import kr.or.dgit.bigdata.pool.fragment.ClassBoardFragment;
import kr.or.dgit.bigdata.pool.fragment.ClassBoardRead;
import kr.or.dgit.bigdata.pool.fragment.ClassInfoFragment;
import kr.or.dgit.bigdata.pool.fragment.ClasstimeFragment;
import kr.or.dgit.bigdata.pool.fragment.ClinicFragment;
import kr.or.dgit.bigdata.pool.fragment.MapsActivity;
import kr.or.dgit.bigdata.pool.fragment.MemberInfoFragment;
import kr.or.dgit.bigdata.pool.fragment.NoticeListFragment;
import kr.or.dgit.bigdata.pool.fragment.QnaInsertFragment;
import kr.or.dgit.bigdata.pool.fragment.NoticeFragment;
import kr.or.dgit.bigdata.pool.fragment.QnaListFragment;
import kr.or.dgit.bigdata.pool.fragment.ReclassFragment;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private LinearLayout login;
    private TextView info;
    private TextView login_title;
    private TextView logOut;
    private TextView qna_list;
    private SharedPreferences mlogin;
    private SharedPreferences admin;
    private SharedPreferences state;
    private ImageView barcode;
    private  Toolbar toolbar;
    private TextView toolbar_title;
    private onKeyBackPressedListener mOnKeyBackPressedListener;
    private TextView user;
    private Menu navigationViewMenu;
    public void setOnKeyBackPressedListener(onKeyBackPressedListener onKeyBackPressedListener) {
        mOnKeyBackPressedListener = onKeyBackPressedListener;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("pool");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title =findViewById(R.id.toolbar_title);
        toolbar_title.setText("대구아이티수영장");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationViewMenu =navigationView.getMenu();

        login = navigationView.getHeaderView(0).findViewById(R.id.login);
        login.setOnClickListener(this);
        user = navigationView.getHeaderView(0).findViewById(R.id.user);
        //회원정보수정
        info = navigationView.getHeaderView(0).findViewById(R.id.info);
        login_title = navigationView.getHeaderView(0).findViewById(R.id.login_title);
        qna_list = navigationView.getHeaderView(0).findViewById(R.id.qna_list);
        qna_list.setOnClickListener(this);
        info.setOnClickListener(this);

        TestFragment cf = TestFragment.newInstance();

        getSupportFragmentManager().beginTransaction().add(R.id.frame, cf).commit();

        mlogin = getSharedPreferences("member", MODE_PRIVATE);
        admin = getSharedPreferences("Admin", MODE_PRIVATE);
        state  = getSharedPreferences("state",0);
        getLoginInfo();

        Intent intent = getIntent();
        if(intent.getStringExtra("classboard") !=null){
            ClassBoardFragment tf = new ClassBoardFragment();
            Bundle bundle = new Bundle();
            bundle.putString("cno",intent.getStringExtra("classboard"));
            tf.setArguments(bundle);
            viewFragment(tf);
        }else if(intent.getStringExtra("bno") !=null){
            String httpread = "http://rkd0519.cafe24.com/pool/restclassboard/read";
            new HttpRequestTack(this, mHandler, new String[] {intent.getStringExtra("bno")}, new String[]{"bno"}, "POST", "글을 불러옵니다.",1).execute(httpread);
        }

        //퍼미션

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS,android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.READ_CONTACTS,android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);


    }

    @Override
    public void onBackPressed() {
        if(mOnKeyBackPressedListener !=null){
            mOnKeyBackPressedListener.onBack();
            mOnKeyBackPressedListener = null;
        }else{
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                toolbar_title.setText("대구아이티수영장");
                FragmentManager fm = getSupportFragmentManager(); // or 'getSupportFragmentManager();'
                int count = fm.getBackStackEntryCount();
                for(int i = 0; i < count; ++i) {
                    fm.popBackStack();
                }

                if(count==0){
                    super.onBackPressed();
                }
            }
       }
    }

    @Override
    public void finish() {
        int store = state.getInt("state",0);
        if(store==0){
            SharedPreferences.Editor logOutm = mlogin.edit();
            logOutm.clear();
            logOutm.commit();
            //강사정보 삭제
            SharedPreferences.Editor logOutT = admin.edit();
            logOutT.clear();
            logOutT.commit();

        }
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.classboard) {
            toolbar_title.setText("반별게시판");
            ClassBoardFragment cf = ClassBoardFragment.newInstance();
            viewFragment(cf);
        } else if (id == R.id.user_attendance) {
            toolbar_title.setText("출석보기");
            MemberInfoFragment mf = MemberInfoFragment.newInstance();
            viewFragment(mf);
        } else if (id == R.id.classtime) {
            toolbar_title.setText("수강시간");
            ClasstimeFragment cf = new ClasstimeFragment();
            viewFragment(cf);
        } else if (id == R.id.bus) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.clinic) {
            toolbar_title.setText("클리닉");
            ClinicFragment cf = new ClinicFragment();
            viewFragment(cf);
        } else if (id == R.id.qnaboard) {
            toolbar_title.setText("문의하기");
            QnaInsertFragment qna = QnaInsertFragment.newInstance();
            viewFragment(qna);
        } else if (id == R.id.noticeboard) {
            NoticeListFragment nl = NoticeListFragment.newInstance();
            viewFragment(nl);
        } else if (id == R.id.classinfo) {
            toolbar_title.setText("반별정보");
            ClassInfoFragment cf = ClassInfoFragment.newInstance();
            viewFragment(cf);
        } else if (id == R.id.reclass) {
            toolbar_title.setText("재등록률");
            ReclassFragment cf = ReclassFragment.newInstance();
            viewFragment(cf);
        } else if (id == R.id.notice_alram) {
            NoticeFragment nf = NoticeFragment.newInstance();
            viewFragment(nf);
        }else if(id==R.id.logOut){
            //회원정보 삭제
            SharedPreferences.Editor logOutm = mlogin.edit();
            logOutm.clear();
            logOutm.commit();
            //강사정보 삭제
            SharedPreferences.Editor logOutT = admin.edit();
            logOutT.clear();
            logOutT.commit();
            //main activity 재요청0

            //로그인 유지 상태
            SharedPreferences.Editor edit = state.edit();
            edit.clear();
            edit.commit();

            finish();
            startActivity(getIntent());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login) {
            int mno = mlogin.getInt("mno",0);
            int tno = admin.getInt("tno",0);

            if(mno!=0||tno!=0){
                return;
            }
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            overridePendingTransition(R.anim.login, R.anim.login_out);

        }
        if(v.getId()==R.id.qna_list){
            toolbar_title.setText("문의내역");
            QnaListFragment qf = QnaListFragment.newInstance();
            viewFragment(qf);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        if (v.getId() == R.id.info) {
            int mno = mlogin.getInt("mno", 0);
            int tno = admin.getInt("tno", 0);
            String title = admin.getString("title","");
            if(mno!=0 &&tno==0){
                Intent MemberInfo = new Intent(this, MemberInfoActivity.class);
                startActivity(MemberInfo);
                overridePendingTransition(R.anim.login, R.anim.login_out);
            }

            if(tno!=0&&mno==0){
                Intent TeacherInfo = new Intent(this, TeacherInfoActivity.class);
                startActivity(TeacherInfo);
                overridePendingTransition(R.anim.login, R.anim.login_out);
            }

        }
    }

    public void viewFragment(Fragment fgm) {
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.exit);
        tr.addToBackStack(null);
        tr.replace(R.id.frame, fgm);
        tr.commit();
    }

    private void getLoginInfo() {
        int tno = admin.getInt("tno", 0);
        String title = admin.getString("title", "");
        int mno = mlogin.getInt("mno", 0);
        String mName = mlogin.getString("name","");
        String tName = admin.getString("name","");
        //회원,강사일때
        if (mno != 0 || (tno != 0 && !title.equals("사장"))) {
            if(mno!=0){
                user.setText(mName+" 회원님");
                MenuItem attendance = navigationViewMenu.findItem(R.id.user_attendance);
                attendance.setVisible(true);
                MenuItem teacheritem = navigationViewMenu.findItem(R.id.teacherItem);
                teacheritem.setVisible(false);
            }

            if(tno !=0){
                user.setText(tName+" 강사님");
                MenuItem teacheritem = navigationViewMenu.findItem(R.id.teacherItem);
                teacheritem.setVisible(true);
                MenuItem attendance = navigationViewMenu.findItem(R.id.user_attendance);
                attendance.setVisible(false);
            }

            info.setVisibility(View.VISIBLE);
            login_title.setVisibility(View.GONE);
            qna_list.setVisibility(View.VISIBLE);
            MenuItem logoutItem = navigationViewMenu.findItem(R.id.logOut);
            logoutItem.setVisible(true);
        }
        //모든 정보가 없을때 로그인 할 수 있는 화면으로
        if (mno == 0 && tno == 0) {
            user.setText("");
            info.setVisibility(View.GONE);
            login_title.setVisibility(View.VISIBLE);
            qna_list.setVisibility(View.GONE);
            MenuItem teacheritem = navigationViewMenu.findItem(R.id.teacherItem);
            teacheritem.setVisible(false);
            MenuItem attendance = navigationViewMenu.findItem(R.id.user_attendance);
            attendance.setVisible(false);
            MenuItem logoutItem = navigationViewMenu.findItem(R.id.logOut);
            logoutItem.setVisible(false);
        }
        //사장님일때
        if (mno == 0 && (title.equals("사장") && tno != 0)) {
            user.setText(tName+" 관리자님");

            Toast.makeText(this, "관리자님 환영합니다" + tno, Toast.LENGTH_SHORT).show();
            info.setVisibility(View.GONE);
            login_title.setVisibility(View.GONE);
            qna_list.setVisibility(View.GONE);
            MenuItem teacheritem = navigationViewMenu.findItem(R.id.teacherItem);
            teacheritem.setVisible(true);
            MenuItem logoutItem = navigationViewMenu.findItem(R.id.logOut);
            logoutItem.setVisible(true);
            MenuItem attendance = navigationViewMenu.findItem(R.id.user_attendance);
            attendance.setVisible(false);
        }

    }
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String result = (String) msg.obj;
                    Bundle bundle = new Bundle();
                    try {
                        JSONObject jObj = new JSONObject(result);
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
                    FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                    tr.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.exit);
                    ClassBoardRead fgm = new ClassBoardRead();
                    fgm.setArguments(bundle);
                    tr.replace(R.id.frame, fgm);
                    tr.commit();

            }
        }
    };
}