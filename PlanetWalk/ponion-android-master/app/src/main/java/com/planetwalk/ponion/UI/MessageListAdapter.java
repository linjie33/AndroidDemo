package com.planetwalk.ponion.UI;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.planetwalk.ponion.GlobalData;
import com.planetwalk.ponion.R;
import com.planetwalk.ponion.db.Entity.PostEntity;
import com.planetwalk.ponion.rapi.model.EmojiComment;

import java.util.ArrayList;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mInflater;
    private List<PostEntity> mMessageEntityList;
    private Activity mActivity;

    public MessageListAdapter(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = mInflater.inflate(R.layout.message_item_layout, viewGroup, false);
        MessageListAdapter.TextMessageViewHolder productViewHolder = new MessageListAdapter.TextMessageViewHolder(itemView);
//        productViewHolder.photoImageView.setOnClickListener(view -> {
//            int position = (int) view.getTag();
//            EmojiComment current = mEmojiCommentList.get(position);
//            mActivity.emojiCommentThreadAsync(current.kind);
//        });
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (mMessageEntityList == null || mMessageEntityList.isEmpty()) {
            return;
        }

        TextMessageViewHolder textMessageViewHolder = (TextMessageViewHolder) viewHolder;
        PostEntity current = mMessageEntityList.get(position);
        textMessageViewHolder.photoImageView.setImageResource(getImageByUser(current.uid));
        textMessageViewHolder.msgItemView.setText(current.content);
    }

    private int getImageByUser(long userId) {
        int index = (int) userId % GlobalData.avatars.length;
        return GlobalData.avatars[index];
    }

    @Override
    public int getItemCount() {
        if (mMessageEntityList == null || mMessageEntityList.isEmpty()) {
            return 0;
        }

        return mMessageEntityList.size();
    }

    public void setMessageEntityList(List<PostEntity> messageEntityList) {
        mMessageEntityList = messageEntityList;
        notifyDataSetChanged();
    }

    class TextMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView msgItemView;
        private final ImageView photoImageView;

        private TextMessageViewHolder(View itemView) {
            super(itemView);
            msgItemView = itemView.findViewById(R.id.content_textView);
            photoImageView = itemView.findViewById(R.id.avatar_imageView);
        }
    }

}
