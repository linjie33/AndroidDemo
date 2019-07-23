package com.planetwalk.ponion.UI;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.planetwalk.ponion.Gsons;
import com.planetwalk.ponion.UI.Views.JazzyViewPager;
import com.planetwalk.ponion.Utils.L;
import com.planetwalk.ponion.db.Entity.SlideEntity;
import com.planetwalk.ponion.db.Entity.ThreadEntity;

import java.util.List;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {
	private List<ThreadEntity> mThreads;
	private int mCurrentThreadIndex = -1;
	private List<SlideEntity> mSlideList;
	private PonionMainActivity.Status mStatus;
	private PonionMainActivity mActivity;
	private JazzyViewPager mJazzy;

	private LayoutInflater mLayoutInflater;

	public MainViewPagerAdapter(PonionMainActivity activity, JazzyViewPager jazzyViewPager) {
		super(activity.getSupportFragmentManager());
		mActivity = activity;
		mJazzy = jazzyViewPager;
		mLayoutInflater = LayoutInflater.from(mActivity);
	}

	public void setData(List<ThreadEntity> threads, int threadIndex) {
		mThreads = threads;
		mCurrentThreadIndex = threadIndex;
		mSlideList = Gsons.value().fromJson(mThreads.get(mCurrentThreadIndex).content, PonionMainActivity.listType);
		mStatus = PonionMainActivity.Status.READ_MODE;

		notifyDataSetChanged();
	}

	public void setEditData(List<SlideEntity> slideEntities) {
		mSlideList = slideEntities;
		notifyDataSetChanged();
	}

	public void setStatus(PonionMainActivity.Status status) {
		mStatus = status;
	}

	private Fragment mCurrentView;

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		mCurrentView = (Fragment) object;
	}

	public Fragment getPrimaryItem() {
		return mCurrentView;
	}

	@Override
	public int getItemPosition(Object object) {
		return FragmentStatePagerAdapter.POSITION_NONE;
	}

	@Override
	public Fragment getItem(int position) {
		L.d("aaa", "getItem:" + position);
		Fragment fragment;
		if (position < mSlideList.size()) {
			SlideEntity currentSlide = mSlideList.get(position);
			if (!currentSlide.isLock) {
				boolean isTextOnly = TextUtils.isEmpty(currentSlide.mediaUrl);
				if (isTextOnly) {
					fragment = TextContentFragment.newInstance(currentSlide);
				} else {
					fragment = MediaContentFragment.newInstance(currentSlide);
				}
			} else {
				fragment = LockContentFragment.newInstance(currentSlide);
			}
		} else {
			fragment = EmojiCommentFragment.newInstance(mThreads.get(mCurrentThreadIndex));
		}

		return fragment;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		L.d("aaa", "instantiateItem:" + position);
		Object obj = super.instantiateItem(container, position);
		mJazzy.setObjectForPosition(obj, position);
		return obj;
	}

	@Override
	public int getCount() {
		if (mSlideList == null) {
			return 0;
		}

		if (mStatus != PonionMainActivity.Status.READ_MODE) {
			return mSlideList.size();
		}

		return mSlideList.size() + 1;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		if (object != null) {
			return ((Fragment) object).getView() == view;
		} else {
			return false;
		}
	}
}
