package com.planetwalk.ponion.UI;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.planetwalk.ponion.R;
import com.planetwalk.ponion.UI.Views.JazzyViewPager;
import com.planetwalk.ponion.db.Entity.SlideEntity;

public class LockContentFragment extends Fragment {
	private static final String ARG_PARAM_SLIDE = "param_slide";

	private SlideEntity slideEntity;

	public LockContentFragment() {
		// Required empty public constructor
	}

	public static LockContentFragment newInstance(SlideEntity slideEntity) {
		LockContentFragment fragment = new LockContentFragment();
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.lock_content_layout, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (slideEntity.backgroundColor != 0) {
			getView().setBackgroundColor(slideEntity.backgroundColor);
		} else {
			getView().setBackgroundColor(getContext().getResources().getColor(R.color.default_bg));
		}

		TextView textView = getView().findViewById(R.id.text_textView);
		textView.setText(slideEntity.text);

		view.setOnClickListener(v -> {
			JazzyViewPager viewPager = getActivity().findViewById(R.id.jazzy_pager);
			viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
		});
	}
}
