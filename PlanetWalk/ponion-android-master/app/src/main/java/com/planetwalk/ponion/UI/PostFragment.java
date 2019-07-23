package com.planetwalk.ponion.UI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.planetwalk.ponion.Gsons;
import com.planetwalk.ponion.PonionApplication;
import com.planetwalk.ponion.R;
import com.planetwalk.ponion.Utils.CommonUtils;
import com.planetwalk.ponion.Utils.L;
import com.planetwalk.ponion.db.Entity.PostEntity;
import com.planetwalk.ponion.db.Entity.ThreadEntity;
import com.planetwalk.ponion.rapi.PonionSeverApi;
import com.planetwalk.ponion.rapi.model.RemoteThreadLike;
import com.planetwalk.ponion.rapi.service.LikeService;
import com.planetwalk.ponion.rapi.service.PostService;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PostFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM_THREAD = "arg_param_thread";
	private ThreadEntity mThread;
	private List<RemoteThreadLike> remoteThreadLikeList;
	private static final int PAGE_SIZE = 100;
	private long mLatestPostId = 0;

	protected CompositeDisposable compositeDisposable = new CompositeDisposable();

	public PostFragment() {
		// Required empty public constructor
	}

	public static PostFragment newInstance(ThreadEntity thread) {
		PostFragment fragment = new PostFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_PARAM_THREAD, thread);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mThread = (ThreadEntity)getArguments().get(ARG_PARAM_THREAD);
		}
	}

	@Override
	public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(fragmentView, savedInstanceState);
		final MessageListAdapter adapter = new MessageListAdapter(getActivity());
		RecyclerView messageListView = getView().findViewById(R.id.message_list);
		messageListView.setAdapter(adapter);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setStackFromEnd(true);
		linearLayoutManager.setReverseLayout(true);
		messageListView.setLayoutManager(linearLayoutManager);


		PonionApplication application = (PonionApplication) getActivity().getApplication();
		application.getDatabase().postDao().getAllPosts(mThread.serverId).observe(this, postEntities -> {
			if (postEntities != null && !postEntities.isEmpty()) {
				mLatestPostId = postEntities.get(0).id;
			}

			L.v("ponion", "post is fetched from server" + postEntities.size());
			adapter.setMessageEntityList(postEntities);
		});

		fetchPostData();
		fetchThreadEmojiInfo();

		getView().findViewById(R.id.add_msg_button).setOnClickListener(view -> {
			Intent intent = new Intent(getActivity(), InputTextActivity.class);
			startActivityForResult(intent, InputTextActivity.CODE_REQUEST_CONTENT);
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_post, container, false);
	}

	public void fetchPostData() {
		Schedulers.io().scheduleDirect(() -> {
			Disposable disposable = PonionSeverApi.api().create(PostService.class)
					.fetchPostsV1(mThread.serverId, 0, PAGE_SIZE, CommonUtils.getToken(getContext()))
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(response -> {
						if (response.getCode() != 200) {
							Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
							return;
						}
						PonionApplication application = (PonionApplication) getActivity().getApplication();
						application.getAppExecutors().diskIO().execute(() -> {
							application.getRepository().insertPosts(response.getData());
						});
					}, error -> {
						Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
					});
			compositeDisposable.add(disposable);
		});
	}

	private void refreshHotEmojiViews(List<RemoteThreadLike> remoteThreadLikes) {
		RecyclerView listView = getView().findViewById(R.id.emoji_count_list);
		listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
		final HotEmojiCommentAdapter adapter = new HotEmojiCommentAdapter(
				getActivity(), Gsons.value().fromJson(mThread.emojiCommentInfoList, CommentActivity.EmojiCommentListType));
		adapter.setEmojiCommentList(remoteThreadLikes);
		adapter.setCurrentEmojiId(mThread.myEmojiCommentId);
		listView.setAdapter(adapter);
	}

	private void fetchThreadEmojiInfo() {
		Schedulers.io().scheduleDirect(() -> {
			Disposable disposable = PonionSeverApi.api().create(LikeService.class)
					.thread(mThread.serverId, CommonUtils.getToken(getContext()))
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(response -> {
						if (response.getCode() != 200) {
							Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
							return;
						}
						remoteThreadLikeList = response.getData();
//                        List<RemoteThreadLike> remoteThreadLikes = Stream.of(remoteThreadLikeList)
//                                .sortBy(remoteThreadLike -> remoteThreadLike.count)
//                                .limit(3).toList();
						List<RemoteThreadLike> remoteThreadLikes = Stream.of(remoteThreadLikeList)
								.filter(r -> r.count > 0)
								.toList();
						refreshHotEmojiViews(remoteThreadLikes);
					}, error -> {
						Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
					});
			compositeDisposable.add(disposable);
		});
	}

	private void submitPost(String content) {
		Schedulers.io().scheduleDirect(() -> {
			PostEntity postEntity = new PostEntity();
			postEntity.content = content;
			postEntity.tid = mThread.serverId;
			Disposable disposable = PonionSeverApi.api().create(PostService.class)
					.addPost(postEntity, CommonUtils.getToken(getContext()))
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(response -> {
						if (response.getCode() != 200) {
//                            dialog.cancel();
							Toast.makeText(getContext(), "发布失败", Toast.LENGTH_SHORT).show();
							return;
						}

						fetchPostData();

//                        postEntity.id = response.getData();
//                        List<PostEntity> postEntities = new ArrayList<>(1);
//                        postEntities.add(postEntity);
//                        PonionApplication application = (PonionApplication) getApplication();
//                        application.getAppExecutors().diskIO().execute(() -> application.getRepository().insertPosts(postEntities));
					}, error -> {
//                        dialog.cancel();
						Toast.makeText(getContext(), "发布失败", Toast.LENGTH_SHORT).show();
					});
			compositeDisposable.add(disposable);
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == InputTextActivity.CODE_REQUEST_CONTENT && data != null) {
			String msg = data.getStringExtra(InputTextActivity.EXTRA_CONTENT);
			if (!TextUtils.isEmpty(msg)) {
				submitPost(msg);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
