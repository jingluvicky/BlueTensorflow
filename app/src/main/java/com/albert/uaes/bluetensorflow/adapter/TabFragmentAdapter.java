package com.albert.uaes.bluetensorflow.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.albert.uaes.bluetensorflow.R;

import java.util.List;

public class TabFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> list;
    private Context context;
    private List<Integer> iconImg;

    public TabFragmentAdapter(FragmentManager fm, List<Fragment> list,
                              Context context, List<Integer> iconImg) {
        super(fm);
        this.list = list;
        this.context = context;
        this.iconImg = iconImg;
    }

    public View getTabView(int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.ms_custom_tab, null);
        ImageView img = (ImageView) v.findViewById(R.id.ms_img_tab);
        img.setImageResource(iconImg.get(position));
        return v;
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
    }
}
