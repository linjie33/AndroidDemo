package com.planetwalk.ponion.UI;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.planetwalk.ponion.R;
import com.planetwalk.ponion.rapi.model.EmojiComment;

import java.util.List;

public class EmojiCommentAdapter extends RecyclerView.Adapter<EmojiCommentAdapter.ProductViewHolder> {
    private final LayoutInflater mInflater;
    private List<EmojiComment> mEmojiCommentList;
    private Activity mActivity;
    private Integer mCurrentEmojiId;

    public EmojiCommentAdapter(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
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
        View itemView = mInflater.inflate(R.layout.comment_emoji_item, viewGroup, false);
        EmojiCommentAdapter.ProductViewHolder productViewHolder = new EmojiCommentAdapter.ProductViewHolder(itemView);
        productViewHolder.photoImageView.setOnClickListener(mOnItemClickListener);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int position) {
        if (mEmojiCommentList != null && position < mEmojiCommentList.size()) {
            EmojiComment current = mEmojiCommentList.get(position);
            productViewHolder.nameItemView.setText(current.name);

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
        if (mEmojiCommentList != null) {
            return mEmojiCommentList.size();
        } else {
            return 0;
        }
    }

    void setEmojiCommentList(List<EmojiComment> emojiCommentList) {
        mEmojiCommentList = emojiCommentList;
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
