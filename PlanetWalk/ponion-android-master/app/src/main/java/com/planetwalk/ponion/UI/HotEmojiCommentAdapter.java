package com.planetwalk.ponion.UI;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.planetwalk.ponion.R;
import com.planetwalk.ponion.rapi.model.EmojiComment;
import com.planetwalk.ponion.rapi.model.RemoteThreadLike;

import java.util.List;

public class HotEmojiCommentAdapter extends RecyclerView.Adapter<HotEmojiCommentAdapter.ProductViewHolder> {
    private final LayoutInflater mInflater;
    private List<RemoteThreadLike> mRemoteThreadLikeList;
    private List<EmojiComment> mEmojiCommentList;
    private Activity mActivity;
    private Integer mCurrentEmojiId;

    public HotEmojiCommentAdapter(Activity activity, List<EmojiComment> emojiCommentList) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mEmojiCommentList = emojiCommentList;
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        mOnItemClickListener = onClickListener;
    }

    public void setCurrentEmojiId(int selectedEmoji) {
        mCurrentEmojiId = selectedEmoji;
    }

    private View.OnClickListener mOnItemClickListener;

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.top_comment_emoji_item, viewGroup, false);
        HotEmojiCommentAdapter.ProductViewHolder productViewHolder = new HotEmojiCommentAdapter.ProductViewHolder(itemView);
        productViewHolder.photoImageView.setOnClickListener(mOnItemClickListener);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int position) {
        if (mRemoteThreadLikeList != null && position < mRemoteThreadLikeList.size()) {
            RemoteThreadLike current = mRemoteThreadLikeList.get(position);
            String name = Stream.of(mEmojiCommentList).filter(emojiComment -> emojiComment.kind == current.kind).findFirst().get().name;
            productViewHolder.nameItemView.setText(name + "(" + current.count + ")");

            if (current.kind == mCurrentEmojiId) {
                productViewHolder.itemView.setBackgroundColor(mActivity.getResources().getColor(R.color.Teal));
            } else {
                productViewHolder.itemView.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
            }
        }

        productViewHolder.photoImageView.setTag(position);
    }

    @Override
    public int getItemCount() {
        if (mRemoteThreadLikeList != null) {
            return mRemoteThreadLikeList.size();
        } else {
            return 0;
        }
    }

    void setEmojiCommentList(List<RemoteThreadLike> emojiCommentList) {
        mRemoteThreadLikeList = emojiCommentList;
        notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameItemView;
        private final ImageView photoImageView;

        private ProductViewHolder(View itemView) {
            super(itemView);
            nameItemView = itemView.findViewById(R.id.name_textView);
            photoImageView = itemView.findViewById(R.id.photo_Imageview);
        }
    }
}
