package com.planetwalk.ponion.UI;


import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.planetwalk.ponion.R;
import com.planetwalk.ponion.UI.Views.JZMediaExo;
import com.planetwalk.ponion.Utils.L;
import com.planetwalk.ponion.db.Entity.SlideEntity;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class MediaContentFragment extends Fragment {
	private static final String ARG_PARAM_SLIDE = "param_slide";

	private SlideEntity slideEntity;

	private TextView mContentTextView;
	private ImageView mImageContentImageView;
	private JzvdStd jzvdStd = null;
	boolean isCreated = false;

	public MediaContentFragment() {
	}

	public static MediaContentFragment newInstance(SlideEntity slideEntity) {
		MediaContentFragment fragment = new MediaContentFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_PARAM_SLIDE, slideEntity);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			slideEntity = (SlideEntity) getArguments().get(ARG_PARAM_SLIDE);
		}
		isCreated = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.content_layout, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mContentTextView = getView().findViewById(R.id.text_textView);
		mImageContentImageView = getView().findViewById(R.id.image_content);
		jzvdStd = getView().findViewById(R.id.jz_video);
		if (slideEntity.mediaType == 1) {
			mImageContentImageView.setVisibility(View.GONE);

			jzvdStd.setVisibility(View.VISIBLE);
			jzvdStd.setUp(slideEntity.mediaUrl, "", JzvdStd.SCREEN_NORMAL, JZMediaExo.class);
		} else if (slideEntity.mediaType == 0) {
			jzvdStd.setVisibility(View.GONE);
			mImageContentImageView.setVisibility(View.VISIBLE);
			Glide.with(this)
					.load(slideEntity.mediaUrl.startsWith("http") ? slideEntity.mediaUrl : Uri.parse(slideEntity.mediaUrl))
					.fitCenter()
					.placeholder(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.gray)))
					.into(mImageContentImageView);
		}

		mContentTextView.setText(slideEntity.text);
		if (slideEntity.backgroundColor != 0) {
			getView().setBackgroundColor(slideEntity.backgroundColor);
		} else {
			getView().setBackgroundColor(getContext().getResources().getColor(R.color.default_bg));
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		L.d("aaa", "setUserVisibleHint:" + isVisibleToUser);
		super.setUserVisibleHint(isVisibleToUser);
		if (!isCreated) {
			return;
		}
		if (jzvdStd != null && jzvdStd.getVisibility() == View.VISIBLE) {
			if (isVisibleToUser) {
				jzvdStd.startVideo();
			} else {
				Jzvd.resetAllVideos();
			}
		}
	}
}
