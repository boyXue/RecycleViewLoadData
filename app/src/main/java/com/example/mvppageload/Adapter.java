package com.example.mvppageload;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> mList; // 数据源
    private Context mContext;    // 上下文Context

    private int normalType = 0;     // 第一种ViewType，正常的item
    private int footType = 1;       // 第二种ViewType，底部的提示View

    private boolean hasMore = true;   // 变量，是否有更多数据
    private boolean fadeTips = false; // 变量，是否隐藏了底部的提示

    private Handler mHandler = new Handler(Looper.getMainLooper()); //获取主线程的Handler

    public Adapter(Context mContext, List<String> mList, boolean hasMore) {
        this.mContext = mContext;
        this.hasMore = hasMore;
        this.mList = mList;
    }

    // 获取条目数量，之所以要加1是因为增加了一条footView
    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    // 自定义方法，获取列表中数据源的最后一个位置，比getItemCount少1，因为不计上footView
    public int getRealLastPosition() {
        return mList.size();
    }

    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        if (position == (getItemCount() - 1)) {
            return footType;
        } else {
            return normalType;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == normalType) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item, null);
            ViewHolder vh = new ViewHolder(view);
            //使用代码设置宽高（xml布局设置无效时）
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return vh;
//            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, null));
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_foot, null);
            FootViewHolder footViewHolder = new FootViewHolder(view);
            //使用代码设置宽高（xml布局设置无效时）
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return footViewHolder;
//            return new FootViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foot, null));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).textView.setText(mList.get(position));
        } else {
            final FootViewHolder footViewHolder = (FootViewHolder) holder;
            footViewHolder.llLayout.setVisibility(View.VISIBLE);
            if (hasMore == true) {
                fadeTips = false;
                if (mList.size() > 0) {
                    footViewHolder.textView.setText("正在加载");
                }
            } else {
                if (mList.size() > 0) {
                    footViewHolder.textView.setText("没有更多数据了");
                    // 然后通过延时加载模拟网络请求的时间，在500ms后执行
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 隐藏提示条
                            footViewHolder.llLayout.setVisibility(View.GONE);
                            // 将fadeTips设置true
                            fadeTips = true;
                            // hasMore设为true是为了让再次拉到底时，会先显示正在加载更多
                            hasMore = true;
                        }
                    }, 500);
                }
            }
        }
    }


    // 暴露接口，改变fadeTips的方法
    public boolean isFadeTips() {
        return fadeTips;
    }

    // 暴露接口，下拉刷新时，通过暴露方法将数据源置为空
    public void resetDatas() {
        mList = new ArrayList<>();
    }

    // 暴露接口，更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void updateList(List<String> newDatas, boolean hasMore) {
        // 在原有的数据之上增加新数据
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv);
        }
    }

    public static class FootViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ProgressBar progressBar;
        LinearLayout llLayout;

        public FootViewHolder(@NonNull View itemView) {
            super(itemView);
            llLayout = itemView.findViewById(R.id.ll_layout);
            textView = itemView.findViewById(R.id.textView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
