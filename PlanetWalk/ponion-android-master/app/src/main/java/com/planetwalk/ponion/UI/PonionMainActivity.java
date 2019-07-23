package com.planetwalk.ponion.UI;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.planetwalk.ponion.GlobalData;
import com.planetwalk.ponion.Gsons;
import com.planetwalk.ponion.PonionApplication;
import com.planetwalk.ponion.R;
import com.planetwalk.ponion.Service.SyncThreadService;
import com.planetwalk.ponion.UI.Views.JazzyViewPager;
import com.planetwalk.ponion.Utils.CommonUtils;
import com.planetwalk.ponion.db.Entity.SlideEntity;
import com.planetwalk.ponion.db.Entity.ThreadEntity;
import com.planetwalk.ponion.media.MediaResolver;
import com.planetwalk.ponion.media.SlideImageConverter;
import com.planetwalk.ponion.media.exception.ConvertFailedException;
import com.planetwalk.ponion.rapi.PonionSeverApi;
import com.planetwalk.ponion.rapi.converter.ThreadConverter;
import com.planetwalk.ponion.rapi.service.ThreadService;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.jzvd.Jzvd;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PonionMainActivity extends BaseRxActivity {
	public final static int REQUEST_CODE_PICK_TYPE = 1001;
	private static final int REQUEST_CODE_CHOOSE = 1002;

	private EditText mContentEditText;
	private View mTextEditingGroup;
	private View mEditGroup;
	private View mPreviewGroup;
	private ImageButton mColorBtn;

	private JazzyViewPager mJazzy;
	private MainViewPagerAdapter mAdapter;

	private List<ThreadEntity> mThreads;
	private int mCurrentThreadIndex = -1;
	private List<SlideEntity> mSlideList;
	private int mCurrentSlideIndex = 0;

	private Status mCurrentStatus = Status.READ_MODE;

	private AlertDialog dialog;

	private CompositeDisposable compositeDisposable = new CompositeDisposable();

	public static Type listType = new TypeToken<ArrayList<SlideEntity>>() {
	}.getType();

	private boolean isCreateFirstSlide = false;

	enum Status {
		READ_MODE,
		EDIT_MODE,
		PREVIEW_MODE,
		TEXT_EDITED_MODE,
		TEXT_EDITING_MODE,
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ponion_main);

		initData();
		initViews();
		refreshViews();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Jzvd.resetAllVideos();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void setupJazzyViewPager() {
		setupJazzyViewPager(JazzyViewPager.TransitionEffect.Tablet);
	}

	private void setupJazzyViewPager(JazzyViewPager.TransitionEffect effect) {
		mJazzy = findViewById(R.id.jazzy_pager);
		mJazzy.setTransitionEffect(effect);
		mAdapter = new MainViewPagerAdapter(PonionMainActivity.this, mJazzy);
		mJazzy.setAdapter(mAdapter);
		mJazzy.setPageMargin(30);

		mJazzy.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int i) {
				if (i < mSlideList.size()) {
					mCurrentSlideIndex = i;
				}
			}

			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});
	}

	private void initData() {
		Intent syncIntent = new Intent();
		syncIntent.setClass(this, SyncThreadService.class);
		startService(syncIntent);

		mSlideList = new ArrayList<>();
		PonionApplication application = (PonionApplication) getApplication();
		application.getRepository().getMeBuddy().observe(this, buddyEntity -> {
		});

		application.getRepository().getThreads().observe(this, threadEntities -> {
			mThreads = threadEntities;
			if (mThreads == null || mThreads.isEmpty()) {
				return;
			}

			if (mCurrentThreadIndex == -1) {
				mCurrentThreadIndex = 0;
				mSlideList = Gsons.value().fromJson(mThreads.get(mCurrentThreadIndex).content, listType);
				mCurrentSlideIndex = 0;
				mAdapter.setData(mThreads, mCurrentThreadIndex);
				refreshViews();
			}
		});
	}

	private void setStatus(Status status) {
		mCurrentStatus = status;
		if (mAdapter != null) {
			mAdapter.setStatus(mCurrentStatus);
		}
	}

	private void initViews() {
		setupJazzyViewPager();

		findViewById(R.id.next_thread).setOnClickListener(view -> {
			nextThread();
		});

		mContentEditText = findViewById(R.id.text_editText);
		mTextEditingGroup = findViewById(R.id.text_editing_group);
		mEditGroup = findViewById(R.id.edit_group);
		mPreviewGroup = findViewById(R.id.preview_group);
		mColorBtn = findViewById(R.id.set_slide_color_btn);

		mContentEditText.setOnEditorActionListener(((textView, i, keyEvent) -> {
			if (i == EditorInfo.IME_ACTION_SEND) {
				InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					//响应的事件
					setStatus(Status.TEXT_EDITED_MODE);
					mSlideList.get(mCurrentSlideIndex).text = mContentEditText.getText().toString();
					refreshViews();
				}
				return true;
			}
			return false;
		}));

		findViewById(R.id.create_story_btn).setOnClickListener(view -> {
			isCreateFirstSlide = true;
			Intent intent = new Intent(PonionMainActivity.this, ChooseStoryTypeActivity.class);
			startActivityForResult(intent, REQUEST_CODE_PICK_TYPE);
		});

		findViewById(R.id.add_new_slide_btn).setOnClickListener(view -> {
			Intent intent = new Intent(PonionMainActivity.this, ChooseStoryTypeActivity.class);
			startActivityForResult(intent, REQUEST_CODE_PICK_TYPE);
		});

		findViewById(R.id.text_edit_confirm_btn).setOnClickListener(view -> {
			setStatus(Status.TEXT_EDITED_MODE);
			mSlideList.get(mCurrentSlideIndex).text = mContentEditText.getText().toString();
			mAdapter.notifyDataSetChanged();
			refreshViews();
		});

		findViewById(R.id.text_edit_cancel_btn).setOnClickListener(view -> {
			setStatus(Status.TEXT_EDITED_MODE);
			refreshViews();
		});

		findViewById(R.id.del_slide_btn).setOnClickListener(view -> {
			mSlideList.remove(mCurrentSlideIndex);
			mCurrentSlideIndex--;
			mAdapter.setEditData(mSlideList);
			refreshViews();
		});

		findViewById(R.id.preview_btn).setOnClickListener(view -> {
			setStatus(Status.PREVIEW_MODE);
			mCurrentSlideIndex = 0;
			refreshViews();
		});

		findViewById(R.id.publish_btn).setOnClickListener(view -> {
			if (dialog == null) {
				dialog = new AlertDialog.Builder(this).create();
				dialog.setCancelable(false);
			}
			dialog.setMessage("正在发布..");
			dialog.show();
			Schedulers.io().scheduleDirect(() -> {
				SlideImageConverter converter = new SlideImageConverter();
				try {
					for (SlideEntity entity : mSlideList) {
						converter.convert(this, entity);
					}
				} catch (ConvertFailedException e) {
					AndroidSchedulers.mainThread().scheduleDirect(() -> {
						dialog.cancel();
						Toast.makeText(getApplicationContext(), "发布失败", Toast.LENGTH_SHORT).show();
					});
					return;
				}
				Disposable disposable = PonionSeverApi.api().create(ThreadService.class)
						.addThread(ThreadConverter.convertToRemote(mSlideList), CommonUtils.getToken(this))
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(response -> {
							if (response.getCode() != 200) {
								dialog.cancel();
								Toast.makeText(getApplicationContext(), "发布失败", Toast.LENGTH_SHORT).show();
								return;
							}
							PonionApplication application = (PonionApplication) getApplication();
							application.getAppExecutors().diskIO().execute(() -> {
								application.getRepository().insertThread(mSlideList, response.getData());
							});
							dialog.cancel();
							Toast.makeText(getApplicationContext(), "发布成功", Toast.LENGTH_SHORT).show();
							mAdapter.setData(mThreads, 0);
							mCurrentSlideIndex = 0;
							refreshViews();
						}, error -> {
							dialog.cancel();
							Toast.makeText(getApplicationContext(), "发布失败", Toast.LENGTH_SHORT).show();
						});
				compositeDisposable.add(disposable);
			});

		});

		findViewById(R.id.edit_btn).setOnClickListener(view -> {
			setStatus(Status.TEXT_EDITED_MODE);
			refreshViews();
		});

		mColorBtn.setOnClickListener(view -> {
			changeColor();
			refreshViews();
		});
	}

	private void nextSlide() {
		if (mCurrentSlideIndex < mSlideList.size() - 1) {
			SlideEntity currentSlide = mSlideList.get(mCurrentSlideIndex);
			if (currentSlide.isLock && (Status.READ_MODE == mCurrentStatus || Status.PREVIEW_MODE == mCurrentStatus)) {
				return;
			}
			mCurrentSlideIndex++;
			refreshViews();
		} else {
			boolean canGoPost = Status.READ_MODE == mCurrentStatus
					&& mThreads != null
					&& !mThreads.isEmpty();
			if (canGoPost) {
				ThreadEntity threadEntity = mThreads.get(mCurrentThreadIndex);
				if (threadEntity.serverId != 0) {
					Intent intent = new Intent(PonionMainActivity.this, CommentActivity.class);
					intent.putExtra(CommentActivity.EXTRA_THREAD, threadEntity);
					startActivity(intent);
				}
			}
		}
	}

	private void preSlide() {
		if (mCurrentSlideIndex > 0) {
			mCurrentSlideIndex--;
			refreshViews();
		} else {

		}
	}

	private void nextThread() {
		if (Status.READ_MODE != mCurrentStatus) {
			return;
		}
		if (mCurrentThreadIndex < mThreads.size() - 1) {
			mCurrentThreadIndex++;
			mSlideList = Gsons.value().fromJson(mThreads.get(mCurrentThreadIndex).content, listType);

			mAdapter.setData(mThreads, mCurrentThreadIndex);
			mCurrentSlideIndex = 0;
			refreshViews();
		} else {

		}

	}

	private void preThread() {
		if (Status.READ_MODE != mCurrentStatus) {
			return;
		}

		if (mCurrentThreadIndex > 0) {
			mCurrentThreadIndex--;
			mSlideList = Gsons.value().fromJson(mThreads.get(mCurrentThreadIndex).content, listType);
			mAdapter.setData(mThreads, mCurrentThreadIndex);
			mCurrentSlideIndex = 0;
			refreshViews();
		} else {
		}
	}

	private void refreshViews() {
		mContentEditText.setVisibility(Status.TEXT_EDITING_MODE == mCurrentStatus ? View.VISIBLE : View.GONE);
		findViewById(R.id.create_story_btn).setVisibility(View.GONE);
		mPreviewGroup.setVisibility(Status.PREVIEW_MODE == mCurrentStatus ? View.VISIBLE : View.GONE);
		findViewById(R.id.next_thread).setVisibility(Status.READ_MODE == mCurrentStatus ? View.VISIBLE : View.GONE);
		findViewById(R.id.publish_btn).setEnabled(mSlideList != null && mSlideList.size() > 1);

		if (Status.READ_MODE == mCurrentStatus) {
			findViewById(R.id.create_story_btn).setVisibility(View.VISIBLE);
			findViewById(R.id.add_new_slide_btn).setVisibility(View.GONE);
			mTextEditingGroup.setVisibility(View.GONE);
			mEditGroup.setVisibility(View.GONE);
			mColorBtn.setVisibility(View.GONE);
		} else if (Status.EDIT_MODE == mCurrentStatus) {
			mTextEditingGroup.setVisibility(View.GONE);
			mEditGroup.setVisibility(View.VISIBLE);
			mColorBtn.setVisibility(View.VISIBLE);
			findViewById(R.id.add_new_slide_btn).setVisibility(View.VISIBLE);
		} else if (Status.PREVIEW_MODE == mCurrentStatus) {
			mTextEditingGroup.setVisibility(View.GONE);
			mEditGroup.setVisibility(View.GONE);
			mColorBtn.setVisibility(View.GONE);
			findViewById(R.id.add_new_slide_btn).setVisibility(View.GONE);
		} else if (Status.TEXT_EDITING_MODE == mCurrentStatus) {
			mTextEditingGroup.setVisibility(View.VISIBLE);
			mEditGroup.setVisibility(View.GONE);
			mColorBtn.setVisibility(View.VISIBLE);
			CommonUtils.openKeyboard(mContentEditText);
			findViewById(R.id.add_new_slide_btn).setVisibility(View.GONE);
		} else if (Status.TEXT_EDITED_MODE == mCurrentStatus) {
			mTextEditingGroup.setVisibility(View.GONE);
			mEditGroup.setVisibility(View.VISIBLE);
			mColorBtn.setVisibility(View.VISIBLE);
			CommonUtils.closeKeyboard(mContentEditText);
			findViewById(R.id.add_new_slide_btn).setVisibility(View.VISIBLE);
		}

		refreshContent();
	}

	private void changeColor() {
		Random random = new Random();
		int i = random.nextInt(GlobalData.BKG_COLOR_LIST.length);
		SlideEntity currentSlide = mSlideList.get(mCurrentSlideIndex);
		int color = currentSlide.backgroundColor = ContextCompat.getColor(getApplicationContext(), GlobalData.BKG_COLOR_LIST[i]);
		Drawable drawable = mColorBtn.getDrawable();
		if (drawable instanceof ShapeDrawable) {
			((ShapeDrawable) drawable).getPaint().setColor(color);
		} else if (drawable instanceof GradientDrawable) {
			((GradientDrawable) drawable).setColor(color);
		} else if (drawable instanceof ColorDrawable) {
			((ColorDrawable) drawable).setColor(color);
		}

		mAdapter.getPrimaryItem().getView().setBackgroundColor(color);
	}

	private void refreshContent() {
		if (mSlideList != null && !mSlideList.isEmpty()) {
			SlideEntity currentSlide = mSlideList.get(mCurrentSlideIndex);
			mContentEditText.setText(currentSlide.text);
			mJazzy.setCurrentItem(mCurrentSlideIndex, false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}

		if (isCreateFirstSlide) {
			isCreateFirstSlide = false;
			mSlideList.clear();
			mCurrentSlideIndex = 0;
			mThreads.add(mCurrentThreadIndex, new ThreadEntity());
		}

		switch (requestCode) {
			case REQUEST_CODE_PICK_TYPE:
				if (data != null) {
					int type = data.getIntExtra("type", 0);
					if (type == 0) {
						// text
						SlideEntity slideEntity = new SlideEntity();
						slideEntity.isLock = false;

						mSlideList.add(slideEntity);
						mCurrentSlideIndex = mSlideList.size() - 1;
						setStatus(Status.TEXT_EDITING_MODE);
					} else if (type == 1) {
						RxPermissions rxPermissions = new RxPermissions(this);
						rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
								.subscribe(new Observer<Boolean>() {
									@Override
									public void onSubscribe(Disposable d) {

									}

									@Override
									public void onNext(Boolean aBoolean) {
										if (aBoolean) {
											// image
											Matisse.from(PonionMainActivity.this)
													//.choose(MimeType.ofImage())
													.choose(MimeType.ofAll())
													.capture(true)
													.captureStrategy(
															new CaptureStrategy(true, "com.planetwalk.ponion.fileprovider"))
													.countable(true)
													.maxSelectable(9)
													.addFilter(new GifSizeFilter(100, 100, 50 * Filter.K * Filter.K))
													.gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
													.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
													.thumbnailScale(0.85f)
													.imageEngine(new Glide4Engine())
													.forResult(REQUEST_CODE_CHOOSE);
										} else {
											Toast.makeText(PonionMainActivity.this, R.string.permission_request_denied, Toast.LENGTH_LONG)
													.show();
										}
									}

									@Override
									public void onError(Throwable e) {

									}

									@Override
									public void onComplete() {

									}
								});
					} else if (type == 2) {
						SlideEntity slideEntity = new SlideEntity();
						slideEntity.isLock = true;
						mSlideList.add(slideEntity);
						mCurrentSlideIndex = mSlideList.size() - 1;
					}
				}
				break;
			case PonionMainActivity.REQUEST_CODE_CHOOSE:
				List<Uri> selectedUris = Matisse.obtainResult(data);
				List<String> selectedPaths = Matisse.obtainPathResult(data);
				for (int i = 0; i < selectedUris.size(); i++) {
					Uri uri = selectedUris.get(i);
					String path = selectedPaths.get(i);
					SlideEntity slideEntity = new SlideEntity();
					slideEntity.isLock = false;
					slideEntity.mediaUrl = uri.toString();
					slideEntity.localPath = path;
					slideEntity.mediaType = MediaResolver.isVideo(MediaResolver.guessMimeType(new File(path))) ? 1 : 0;
					mSlideList.add(slideEntity);
				}

				mCurrentSlideIndex = mSlideList.size() - 1;
				setStatus(Status.TEXT_EDITED_MODE);
				break;
		}

		mThreads.get(mCurrentThreadIndex).content = Gsons.value().toJson(mSlideList);
		mAdapter.setEditData(mSlideList);
		refreshViews();
	}
}
