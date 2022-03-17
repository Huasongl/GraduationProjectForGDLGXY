package com.gdlgxy.internshipcommunity.module.mine;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gdlgxy.internshipcommunity.R;
import com.gdlgxy.internshipcommunity.login.UserManager;
import com.gdlgxy.internshipcommunity.module.basepaging.HomePagedListAdapter;
import com.gdlgxy.internshipcommunity.module.basepaging.InteractionPresenter;
import com.gdlgxy.internshipcommunity.module.basepaging.MutableItemKeyedDataSource;
import com.gdlgxy.internshipcommunity.module.basepaging.PagingTabData;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

public class ProfileListAdapter extends HomePagedListAdapter {
    public ProfileListAdapter(Context context, String category) {
        super(context, category);
    }

    @Override
    public int getItemViewType2(int position) {
        if (TextUtils.equals(mCategory, ProfileActivity.TAB_TYPE_COMMENT)) {
            return R.layout.layout_feed_type_comment;
        } else if (TextUtils.equals(mCategory, ProfileActivity.TAB_TYPE_ALL)) {
            PagingTabData feed = getItem(position);
            if (feed.topComment != null && feed.topComment.userId == UserManager.get().getUserId()) {
                return R.layout.layout_feed_type_comment;
            }
        }
        return super.getItemViewType2(position);
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        super.onBindViewHolder2(holder, position);
        View deleteView = holder.itemView.findViewById(R.id.feed_delete);
        TextView createTime = holder.itemView.findViewById(R.id.create_time);

        PagingTabData feed = getItem(position);
        createTime.setVisibility(View.VISIBLE);
        createTime.setText(TimeUtils.calculate(feed.createTime));

        boolean isCommentTab = TextUtils.equals(mCategory, ProfileActivity.TAB_TYPE_COMMENT);
        deleteView.setVisibility(View.VISIBLE);
        deleteView.setOnClickListener(v -> {
            //如果是个人主页的评论tab，删除的时候，实际上是删除帖子的评论。
            if (isCommentTab) {
                InteractionPresenter.deleteFeedComment(mContext, feed.itemId, feed.topComment.commentId)
                        .observe((LifecycleOwner) mContext, success -> {
                            refreshList(feed);
                        });
            } else {
                InteractionPresenter.deleteFeed(mContext, feed.itemId)
                        .observe((LifecycleOwner) mContext, success -> {
                            refreshList(feed);
                        });
            }
        });
    }

    private void refreshList(PagingTabData delete) {
        //实际上这个方法 可以再封装一下
        PagedList<PagingTabData> currentList = getCurrentList();
        MutableItemKeyedDataSource<Integer, PagingTabData> dataSource = new MutableItemKeyedDataSource<Integer, PagingTabData>((ItemKeyedDataSource) currentList.getDataSource()) {
            @NonNull
            @Override
            public Integer getKey(@NonNull PagingTabData item) {
                return item.id;
            }
        };
        //for循环一遍,过滤掉被删除的这个帖子
        for (PagingTabData feed : currentList) {
            if (feed != delete) {
                dataSource.data.add(feed);
            }
        }
        PagedList<PagingTabData> pagedList = dataSource.buildNewPagedList(currentList.getConfig());
        submitList(pagedList);
    }
}
