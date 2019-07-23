package com.planetwalk.ponion.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PostActivity extends BaseRxActivity {
    public static final String EXTRA_THREAD = "EXTRA_THREAD";
    private ThreadEntity mThread;
    private long mLatestPostId = 0;
    private List<RemoteThreadLike> remoteThreadLikeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mThread = (ThreadEntity) getIntent().getSerializableExtra(EXTRA_THREAD);

        /*
        创建适配器
        加载recyclerview布局
        布局加载适配器
        LinearLayoutManager创建
        列表加载layoutManager

         */

        final MessageListAdapter adapter = new MessageListAdapter(this);
        RecyclerView messageListView = findViewById(R.id.message_list);
        messageListView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        messageListView.setLayoutManager(linearLayoutManager);

/*
创建application
给适配器传值

 */
        PonionApplication application = (PonionApplication) getApplication();
        application.getDatabase().postDao().getAllPosts(mThread.serverId).observe(PostActivity.this, postEntities -> {
            if (postEntities != null && !postEntities.isEmpty()) {
                mLatestPostId = postEntities.get(0).id;
            }

            L.v("ponion", "post is fetched from server" + postEntities.size());
            adapter.setMessageEntityList(postEntities);
        });

        fetchPostData();
//        fetchThreadEmojiInfo();

        findViewById(R.id.add_msg_button).setOnClickListener(view -> {
            Intent intent = new Intent(PostActivity.this, InputTextActivity.class);
            startActivityForResult(intent, InputTextActivity.CODE_REQUEST_CONTENT);
        });
    }

    private static final int PAGE_SIZE = 100;

    public void fetchPostData() {
        Schedulers.io().scheduleDirect(() -> {
            Disposable disposable = PonionSeverApi.api().create(PostService.class)
                    .fetchPostsV1(mThread.serverId, 0, PAGE_SIZE, CommonUtils.getToken(this))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response.getCode() != 200) {
                            Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        PonionApplication application = (PonionApplication) getApplication();
                        application.getAppExecutors().diskIO().execute(() -> {
                            application.getRepository().insertPosts(response.getData());
                        });
                    }, error -> {
                        Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    });
            compositeDisposable.add(disposable);
        });
    }

    private void refreshHotEmojiViews(List<RemoteThreadLike> remoteThreadLikes) {
        RecyclerView listView = findViewById(R.id.emoji_count_list);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        final HotEmojiCommentAdapter adapter = new HotEmojiCommentAdapter(
                this, Gsons.value().fromJson(mThread.emojiCommentInfoList, CommentActivity.EmojiCommentListType));
        adapter.setEmojiCommentList(remoteThreadLikes);
        adapter.setCurrentEmojiId(mThread.myEmojiCommentId);
        listView.setAdapter(adapter);
    }

    private void fetchThreadEmojiInfo() {
        Schedulers.io().scheduleDirect(() -> {
            Disposable disposable = PonionSeverApi.api().create(LikeService.class)
                    .thread(mThread.serverId, CommonUtils.getToken(this))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response.getCode() != 200) {
                            Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        remoteThreadLikeList = response.getData();
                        List<RemoteThreadLike> remoteThreadLikes = Stream.of(remoteThreadLikeList)
                                .filter(r -> r.count > 0)
                                .toList();
                        refreshHotEmojiViews(remoteThreadLikes);
                    }, error -> {
                        Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
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
                    .addPost(postEntity, CommonUtils.getToken(this))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response.getCode() != 200) {
//                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), "发布失败", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "发布失败", Toast.LENGTH_SHORT).show();
                    });
            compositeDisposable.add(disposable);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == InputTextActivity.CODE_REQUEST_CONTENT && data != null) {
            String msg = data.getStringExtra(InputTextActivity.EXTRA_CONTENT);
            if (!TextUtils.isEmpty(msg)) {
                submitPost(msg);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
