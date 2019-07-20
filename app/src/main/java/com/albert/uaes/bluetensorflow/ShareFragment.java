package com.albert.uaes.bluetensorflow;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Albert
 */
public class ShareFragment extends BaseFragment {

    private RecyclerView recyclerView;

    public static ShareFragment newInstance() {

        Bundle args = new Bundle();

        ShareFragment fragment = new ShareFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerview);

        initRecyclerView();
    }

    private void initRecyclerView() {

    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_share;
    }
}
