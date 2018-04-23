package kr.or.dgit.bigdata.pool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;
import android.widget.Toast;

import kr.or.dgit.bigdata.pool.fragment.ClassBoardFragment;
import kr.or.dgit.bigdata.pool.fragment.ClassInfoFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{

    private TextView login;


    int mStart = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        login = navigationView.getHeaderView(0).findViewById(R.id.login);
        login.setOnClickListener(this);

        TestFragment cf = TestFragment.newInstance(mStart);

        getSupportFragmentManager().beginTransaction().add(R.id.frame,cf).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            ClassBoardFragment cf = ClassBoardFragment.newInstance();
            viewFragment(cf);
        }else if(id ==R.id.user_attendance){

        }else if(id ==R.id.classtime){

        }else if(id ==R.id.bus){

        }else if(id ==R.id.clinic){

        }else if(id ==R.id.classboard){

        }else if(id ==R.id.qnaboard){

        }else if(id ==R.id.noticeboard){

        }else if(id ==R.id.classinfo){
            ClassInfoFragment cf = ClassInfoFragment.newInstance();
            viewFragment(cf);
        }else if(id ==R.id.reclass){

        }else if(id ==R.id.notice_alram){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.login){
            Intent loginIntent = new Intent(this,LoginActivity.class);
            startActivity(loginIntent);
            overridePendingTransition(R.anim.login,R.anim.login_out);
        }
    }

    private void viewFragment(Fragment fgm){
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.exit);
        tr.addToBackStack(null);
        tr.replace(R.id.frame,fgm);
        tr.commit();
    }

}