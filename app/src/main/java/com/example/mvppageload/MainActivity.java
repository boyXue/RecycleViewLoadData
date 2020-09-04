package com.example.mvppageload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.mvppageload.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityMainBinding binding;
    private List<String> list = new ArrayList<>();
    private Adapter adapter;
    private final int PAGE_COUNT = 10;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        binding.refreshLayout.setOnRefreshListener(this);
        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));
        initData();
        initRecyclerView();
    }

    private void initRecyclerView() {
        adapter = new Adapter(MainActivity.this, getDatas(0, PAGE_COUNT),
                getDatas(0, PAGE_COUNT).size() > 0 ? true : false);
        binding.recycleView.setAdapter(adapter);
        binding.recycleView.addOnScrollListener(new onLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                // 如果没有隐藏footView，那么最后一个条目的位置就比我们的getItemCount少1，自己可以算一下
                if (adapter.isFadeTips() == false && lastItem + 1 == adapter.getItemCount()) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 然后调用updateRecyclerview方法更新RecyclerView
                            updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                        }
                    }, 500);
                }
                // 如果隐藏了提示条，我们又上拉加载时，那么最后一个条目就要比getItemCount要少2
                if (adapter.isFadeTips() == true && lastItem + 2 == adapter.getItemCount()) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 然后调用updateRecyclerview方法更新RecyclerView
                            updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                        }
                    }, 500);
                }
            }
        });
    }

    private void initData() {
        for (int i = 1; i <= 40; i++) {
            list.add("条目" + i);
        }
    }

    private List<String> getDatas(final int firstIndex, final int lastIndex) {
        List<String> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < list.size()) {
                resList.add(list.get(i));
            }
        }
        return resList;
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<String> newDatas = getDatas(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            adapter.updateList(newDatas, true);
        } else {
            adapter.updateList(null, false);
        }
    }

    @Override
    public void onRefresh() {
        //设置可见
        binding.refreshLayout.setRefreshing(true);
        adapter.resetDatas();
        updateRecyclerView(0, PAGE_COUNT);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.refreshLayout.setRefreshing(false);
            }
        }, 1000);
    }
}
