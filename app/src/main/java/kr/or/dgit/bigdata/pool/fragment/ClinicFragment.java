package kr.or.dgit.bigdata.pool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import kr.or.dgit.bigdata.pool.R;


public class ClinicFragment extends Fragment implements  TabLayout.OnTabSelectedListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> arrFragment;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.clinic,container,false);
        tabLayout = (TabLayout)root.findViewById(R.id.tabs);
        viewPager = (ViewPager)root.findViewById(R.id.viewPager);


        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);

        return root;
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragment = new ArrayList<Fragment>();
        private String title[] = new String[]{"자유형","배영","평영","접영","스타트&턴"};
        public MyPagerAdapter(FragmentManager fm,ArrayList<Fragment> arrFragment) {
            super(fm);
            this.fragment = arrFragment;

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
