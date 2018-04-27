package kr.or.dgit.bigdata.pool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import java.util.Map;

import kr.or.dgit.bigdata.pool.dto.Teacher;
import kr.or.dgit.bigdata.pool.fragment.ClassBoardFragment;
import kr.or.dgit.bigdata.pool.fragment.ClassBoardRead;
import kr.or.dgit.bigdata.pool.fragment.ClassInfoFragment;
import kr.or.dgit.bigdata.pool.fragment.MapsActivity;
import kr.or.dgit.bigdata.pool.fragment.MemberInfoFragment;
import kr.or.dgit.bigdata.pool.fragment.QnaInsertFragment;
import kr.or.dgit.bigdata.pool.fragment.NoticeFragment;
import kr.or.dgit.bigdata.pool.fragment.ReclassFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private LinearLayout login;
    private TextView info;
    private TextView login_title;
    private TextView logOut;
    private SharedPreferences mlogin;
    private SharedPreferences admin;
    private ImageView barcode;
    private  Toolbar toolbar;
    private TextView toolbar_title;
    private onKeyBackPressedListener mOnKeyBackPressedListener;

    public void setOnKeyBackPressedListener(onKeyBackPressedListener onKeyBackPressedListener) {
        mOnKeyBackPressedListener = onKeyBackPressedListener;
    }

    int mStart = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        login = navigationView.getHeaderView(0).findViewById(R.id.login);
        login.setOnClickListener(this);
        //회원정보수정
        info = navigationView.getHeaderView(0).findViewById(R.id.info);
        login_title = navigationView.getHeaderView(0).findViewById(R.id.login_title);
        info.setOnClickListener(this);
        //로그아웃
        logOut = navigationView.getHeaderView(0).findViewById(R.id.logOut);
        logOut.setOnClickListener(this);

        barcode = navigationView.getHeaderView(0).findViewById(R.id.barCode);
        barcode.setOnClickListener(this);

        TestFragment cf = TestFragment.newInstance(mStart);

        getSupportFragmentManager().beginTransaction().add(R.id.frame, cf).commit();

        mlogin = getSharedPreferences("member", MODE_PRIVATE);
        admin = getSharedPreferences("Admin", MODE_PRIVATE);
        getLoginInfo();

        Intent intent = getIntent();
        if(intent.getStringExtra("classboard") !=null){
            ClassBoardFragment tf = new ClassBoardFragment();
            Bundle bundle = new Bundle();
            bundle.putString("cno",intent.getStringExtra("classboard"));
            tf.setArguments(bundle);
            viewFragment(tf);
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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

        } else if (id == R.id.bus) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.clinic) {
            toolbar_title.setText("클리닉");
        } else if (id == R.id.qnaboard) {
            toolbar_title.setText("문의하기");
            QnaInsertFragment qna = QnaInsertFragment.newInstance();
            viewFragment(qna);
        } else if (id == R.id.noticeboard) {

        } else if (id == R.id.classinfo) {
            toolbar_title.setText("반별정보");
            ClassInfoFragment cf = ClassInfoFragment.newInstance();
            viewFragment(cf);
        } else if (id == R.id.reclass) {
            toolbar_title.setText("재등록률");
            ReclassFragment cf = ReclassFragment.newInstance();
            viewFragment(cf);
        } else if (id == R.id.notice_alram) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            overridePendingTransition(R.anim.login, R.anim.login_out);
        }
        if (v.getId() == R.id.logOut) {
            //회원정보 삭제
            SharedPreferences.Editor logOutm = mlogin.edit();
            logOutm.clear();
            logOutm.commit();
            //강사정보 삭제
            SharedPreferences.Editor logOutT = admin.edit();
            logOutT.clear();
            logOutT.commit();
            //main activity 재요청0

            finish();
            startActivity(getIntent());

        }
        if(v.getId()==R.id.barCode){
            Intent intent = new Intent(getApplicationContext(), BarCodeActivity.class);
            startActivity(intent);
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

    private void viewFragment(Fragment fgm) {
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

        //회원,강사일때
        if (mno != 0 || (tno != 0 && !title.equals("사장"))) {
            info.setVisibility(View.VISIBLE);
            login_title.setVisibility(View.GONE);
            logOut.setVisibility(View.VISIBLE);
            barcode.setVisibility(View.VISIBLE);
        }
        //모든 정보가 없을때 로그인 할 수 있는 화면으로
        if (mno == 0 && tno == 0) {
            info.setVisibility(View.GONE);
            logOut.setVisibility(View.GONE);
            login_title.setVisibility(View.VISIBLE);
            barcode.setVisibility(View.GONE);
        }
        //사장님일때
        if (mno == 0 && (title.equals("사장") && tno != 0)) {
            Toast.makeText(this, "사장님 로그인: " + tno, Toast.LENGTH_SHORT).show();
            barcode.setVisibility(View.GONE);
        }

    }

}