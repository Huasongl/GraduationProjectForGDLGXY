package com.gdlgxy.internshipcommunity.module.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gdlgxy.internshipcommunity.BR;
import com.gdlgxy.internshipcommunity.R;
import com.gdlgxy.internshipcommunity.base.BasePagedListAdapter;
import com.gdlgxy.internshipcommunity.databinding.LayoutFeedTypeImageBinding;
import com.gdlgxy.internshipcommunity.databinding.LayoutFeedTypeVideoBinding;
import com.gdlgxy.internshipcommunity.widget.ListPlayerView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class HomePagedListAdapter extends BasePagedListAdapter<HomeTabData, HomePagedListAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    protected Context mContext;
    protected String mCategory;

    public HomePagedListAdapter(Context context, String category) {
        super(new DiffUtil.ItemCallback<HomeTabData>() {
            @Override
            public boolean areItemsTheSame(@NonNull HomeTabData oldItem, @NonNull HomeTabData newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull HomeTabData oldItem, @NonNull HomeTabData newItem) {
                return oldItem.equals(newItem);
            }
        });
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mCategory = category;
    }

    @Override
    public int getItemViewType2(int position) {
        HomeTabData feed = getItem(position);
        if (feed.itemType == HomeTabData.TYPE_IMAGE_TEXT) {
            return R.layout.layout_feed_type_image;
        } else if (feed.itemType == HomeTabData.TYPE_VIDEO) {
            return R.layout.layout_feed_type_video;
        }
        return 0;
    }

    @Override
    protected ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(mInflater, viewType, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        final HomeTabData feed = getItem(position);
        holder.bindData(feed);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void onStartFeedDetailActivity(HomeTabData feed) {

    }

    private FeedObserver mFeedObserver;

    private class FeedObserver implements Observer<HomeTabData> {
        private HomeTabData mFeed;

        @Override
        public void onChanged(HomeTabData newOne) {
            if (mFeed.id != newOne.id)
                return;
            mFeed.author = newOne.author;
            mFeed.ugc = newOne.ugc;
            mFeed.notifyChange();
        }

        public void setFeed(HomeTabData feed) {
            mFeed = feed;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewDataBinding mBinding;
        public ListPlayerView listPlayerView;
        public ImageView feedImage;

        public ViewHolder(@NonNull View itemView, ViewDataBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        public void bindData(HomeTabData item) {
            //这里之所以手动绑定数据的原因是 图片 和视频区域都是需要计算的
            //而dataBinding的执行默认是延迟一帧的。
            //当列表上下滑动的时候 ，会明显的看到宽高尺寸不对称的问题

            mBinding.setVariable(BR.homeTabData, item);
            mBinding.setVariable(BR.lifeCycleOwner, mContext);
            if (mBinding instanceof LayoutFeedTypeImageBinding) {
                LayoutFeedTypeImageBinding imageBinding = (LayoutFeedTypeImageBinding) mBinding;
                feedImage = imageBinding.feedImage;
                imageBinding.feedImage.bindData(item.width, item.height, 16, item.cover);
//                imageBinding.setFeed(item);
//                imageBinding.interactionBinding.setLifeCycleOwner((LifecycleOwner) mContext);
            }
            else if (mBinding instanceof LayoutFeedTypeVideoBinding) {
                LayoutFeedTypeVideoBinding videoBinding = (LayoutFeedTypeVideoBinding) mBinding;
                videoBinding.listPlayerView.bindData(mCategory, item.width, item.height, item.cover, item.url);
                listPlayerView = videoBinding.listPlayerView;
//                videoBinding.setFeed(item);
//                videoBinding.interactionBinding.setLifeCycleOwner((LifecycleOwner) mContext);
            }
        }

        public boolean isVideoItem() {
            return mBinding instanceof LayoutFeedTypeVideoBinding;
        }

        public ListPlayerView getListPlayerView() {
            return listPlayerView;
        }
    }
}
