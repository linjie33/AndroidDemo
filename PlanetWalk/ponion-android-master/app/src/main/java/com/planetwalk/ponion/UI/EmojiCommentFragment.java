package com.planetwalk.ponion.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.planetwalk.ponion.Gsons;
import com.planetwalk.ponion.PonionApplication;
import com.planetwalk.ponion.R;
import com.planetwalk.ponion.Utils.CommonUtils;
import com.planetwalk.ponion.db.Entity.ThreadEntity;
import com.planetwalk.ponion.rapi.PonionSeverApi;
import com.planetwalk.ponion.rapi.model.EmojiComment;
import com.planetwalk.ponion.rapi.service.LikeService;
import com.planetwalk.ponion.rapi.service.ThreadService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import android.support.design.widget.BottomSheetDialog;


import static com.planetwalk.ponion.Gsons.value;

public class EmojiCommentFragment extends Fragment {
	public static final String EXTRA_THREAD = "EXTRA_THREAD";
	public ThreadEntity mThread;
	private EmojiCommentAdapter mAdapter;

	protected CompositeDisposable compositeDisposable = new CompositeDisposable();

	public static Type EmojiCommentListType = new TypeToken<ArrayList<EmojiComment>>() {
	}.getType();

	public EmojiCommentFragment() {
	}

	static EmojiCommentFragment newInstance(ThreadEntity threadEntity) {
		EmojiCommentFragment emojiCommentFragment = new EmojiCommentFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(EXTRA_THREAD, threadEntity);
		emojiCommentFragment.setArguments(bundle);
		return emojiCommentFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_emoj_comment, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(fragmentView, savedInstanceState);

		getView().findViewById(R.id.go_post_btn).setOnClickListener(view -> {
//			Intent intent = new Intent(getActivity(), PostActivity.class);
//			intent.putExtra(PostActivity.EXTRA_THREAD, mThread);
//			startActivity(intent);
			showButtomDialog();
		});

		mAdapter = new EmojiCommentAdapter(getActivity());
		mAdapter.setOnItemClickListener(view -> {
			if (mThread.myEmojiCommentId == 0) {
				int position = (int) view.getTag();
				List<EmojiComment> emojiCommentList = Gsons.value().fromJson(mThread.emojiCommentInfoList, EmojiCommentListType);
				EmojiComment current = emojiCommentList.get(position);
				emojiCommentThreadAsync(current.kind);
			}
		});

		mAdapter.setCurrentEmojiId(mThread.myEmojiCommentId);
		RecyclerView recyclerView = getView().findViewById(R.id.emoji_list);
		recyclerView.setAdapter(mAdapter);
		recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

		fetchEmojiData();
		refreshViews();
	}

	private void showButtomDialog() {
		BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this.getContext());
		View view = LayoutInflater.from(this.getContext()).inflate(R.layout.bottom_sheet_layout,null);

		handleList(view);
		bottomSheetDialog.setContentView(view);
		bottomSheetDialog.setCancelable(true);
		bottomSheetDialog.setCanceledOnTouchOutside(true);
		bottomSheetDialog.show();
	}

	private void handleList(View view) {
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(),DividerItemDecoration.VERTICAL));
		MyRecyclerAdapter adapter = new MyRecyclerAdapter();
		recyclerView.setAdapter(adapter);

		adapter.setData(getDatas());
		adapter.notifyDataSetChanged();
	}
	private List<String> getDatas() {
		List<String> list = new ArrayList<>();
		for (int i = 0 ;i<30 ;i++) {
			list.add("android："+i);
		}
		return list;
	}


	public static class MyRecyclerAdapter extends RecyclerView.Adapter{

		private List<String> mData ;

		public void setData(List<String> list) {
			mData = list ;
		}
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_item,null));
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			MyViewHolder myViewHolder = (MyViewHolder) holder;
			myViewHolder.index.setText(mData.get(position)+"a");
			myViewHolder.name.setText(mData.get(position)+"b");
		}

		@Override
		public int getItemCount() {
			return mData == null ? 0 :mData.size();
		}



		public static class MyViewHolder extends RecyclerView.ViewHolder{

			private TextView name ;
			private TextView index ;
			public MyViewHolder(View itemView) {
				super(itemView);
				name = (TextView) itemView.findViewById(R.id.bottom_sheet_dialog_item_name);
				index = (TextView) itemView.findViewById(R.id.bottom_sheet_dialog_item_index);
			}
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mThread = (ThreadEntity) getArguments().get(EXTRA_THREAD);
		}
	}

	private void refreshViews() {
		View view = getView().findViewById(R.id.go_post_btn);
		if (mThread.myEmojiCommentId == 0) {
			getView().findViewById(R.id.go_post_btn).setVisibility(View.INVISIBLE);
		} else {
			view.setVisibility(View.VISIBLE);
//			view.animate()
//					.translationY(view.getHeight())
//					.alpha(1.0f)
//					.setDuration(300)
//					.setListener(new AnimatorListenerAdapter() {
//						@Override
//						public void onAnimationEnd(Animator animation) {
//							super.onAnimationEnd(animation);
//
//						}
//					});
		}
	}

	public void fetchEmojiData() {
		if (mThread.emojiCommentInfoList != null) {
			mAdapter.setEmojiCommentList(value().fromJson(mThread.emojiCommentInfoList, EmojiCommentListType));
			return;
		}

		Schedulers.io().scheduleDirect(() -> {
			Disposable disposable = PonionSeverApi.api().create(ThreadService.class)
					.getEmojiComments(mThread.serverId, CommonUtils.getToken(getContext()))
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(response -> {
						if (response.getCode() != 200) {
							Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
							return;
						}

						Context context = getContext();
						if (context != null) {
							PonionApplication application = (PonionApplication) context.getApplicationContext();
							application.getAppExecutors().diskIO().execute(() -> {
								mThread.emojiCommentInfoList = value().toJson(response.getData());
								application.getRepository().updateThread(mThread);
							});
							mAdapter.setEmojiCommentList(response.getData());
						}
					}, error -> {
						Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
					});
			compositeDisposable.add(disposable);
		});
	}

	public void emojiCommentThreadAsync(int emojiId) {
		Schedulers.io().scheduleDirect(() -> {
			Disposable disposable = PonionSeverApi.api().create(LikeService.class)
					.like(mThread.serverId, emojiId, mThread.myEmojiCommentId, CommonUtils.getToken(getContext()))
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(response -> {
						if (response.getCode() != 200) {
							Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
							return;
						}

						mThread.myEmojiCommentId = emojiId;
						PonionApplication application = (PonionApplication) getActivity().getApplication();
						application.getAppExecutors().diskIO().execute(() -> {
							application.getRepository().updateThread(mThread);
						});

						mAdapter.setCurrentEmojiId(mThread.myEmojiCommentId);
						mAdapter.notifyDataSetChanged();
						refreshViews();
					}, error -> {
						Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
					});
			compositeDisposable.add(disposable);
		});
	}

}
