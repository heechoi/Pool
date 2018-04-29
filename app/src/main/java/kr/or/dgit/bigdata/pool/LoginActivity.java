package kr.or.dgit.bigdata.pool;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.or.dgit.bigdata.pool.dto.Member;
import kr.or.dgit.bigdata.pool.fragment.MemberLogin;
import kr.or.dgit.bigdata.pool.fragment.TeacherLogin;
import kr.or.dgit.bigdata.pool.util.HttpRequestTack;

public class LoginActivity extends AppCompatActivity implements  TabLayout.OnTabSelectedListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tabLayout = (TabLayout)findViewById(R.id.tabs);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
       viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

       tabLayout.setupWithViewPager(viewPager);
       tabLayout.addOnTabSelectedListener(this);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    class MyPagerAdapter extends FragmentPagerAdapter {

       private List<Fragment> fragment = new ArrayList<Fragment>();
       private String title[] = new String[]{"회원","관리자"};

       public MyPagerAdapter(FragmentManager fm) {
           super(fm);
           fragment.add(new MemberLogin());
           fragment.add(new TeacherLogin());
       }
       @Override
       public Fragment getItem(int position) {
           return this.fragment.get(position);
       }

       @Override
       public int getCount() {
           return this.fragment.size();
       }

       @Override
       public CharSequence getPageTitle(int position) {
           return title[position];
       }
   }

}
