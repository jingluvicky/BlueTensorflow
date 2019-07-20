package com.albert.uaes.bluetensorflow;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.albert.uaes.bluetensorflow.adapter.TabFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ControlFragment.SendCMD{

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ControlFragment controlFragment;
    private ShareFragment shareFragment;
    private SettingFragment settingFragment;
    private LocationOutputFragment locationOutputFragment;
    private ScanFragment scanFragment;

    private List<Fragment> fragmentList;
    private TabFragmentAdapter tabFragmentAdapter;
    private List<Integer> iconImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);

        initFragment();
        tabViewSetView();
        resetTablayout();
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        iconImg = new ArrayList<>();

        controlFragment = ControlFragment.newInstance();
        shareFragment = ShareFragment.newInstance();
        settingFragment = SettingFragment.newInstance();
        scanFragment = ScanFragment.newInstance();
        locationOutputFragment=LocationOutputFragment.newInstance();

        fragmentList.add(controlFragment);
        fragmentList.add(shareFragment);
        fragmentList.add(settingFragment);
        fragmentList.add(scanFragment);
        fragmentList.add(locationOutputFragment);


        iconImg.add(R.drawable.tab_control_selector);
        iconImg.add(R.drawable.tab_share_selector);
        iconImg.add(R.drawable.tab_setting_selector);
        iconImg.add(R.drawable.tab_scan_selector);
        iconImg.add(R.drawable.tab_locationoutput_selector);

    }

    private void tabViewSetView() {
        tabFragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager(),fragmentList,this,iconImg);
        viewPager.setAdapter(tabFragmentAdapter);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void resetTablayout() {

        for (int i=0;i<tabLayout.getTabCount();i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab!=null){
                tab.setCustomView(tabFragmentAdapter.getTabView(i));
            }
        }
    }

    @Override
    public void sendControlCMD(int cmd) {
        if (scanFragment!=null){
            scanFragment.setCMDCounter(cmd);
        }
    }
}
