package com.planetwalk.ponion.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.planetwalk.ponion.Gsons.value;

public class CommentActivity extends BaseRxActivity {
    public static final String EXTRA_THREAD = "EXTRA_THREAD";
    public ThreadEntity mThread;
    private EmojiCommentAdapter mAdapter;

    public static Type EmojiCommentListType = new TypeToken<ArrayList<EmojiComment>>() {
    }.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mThread = (ThreadEntity) getIntent().getSerializableExtra(EXTRA_THREAD);
        refreshViews();
        mAdapter = new EmojiCommentAdapter(this);
        mAdapter.setOnItemClickListener(view -> {
            int position = (int) view.getTag();
            List<EmojiComment> emojiCommentList = Gsons.value().fromJson(mThread.emojiCommentInfoList, EmojiCommentListType);
            EmojiComment current = emojiCommentList.get(position);
            emojiCommentThreadAsync(current.kind);
        });

        mAdapter.setCurrentEmojiId(mThread.myEmojiCommentId);
        RecyclerView recyclerView = findViewById(R.id.emoji_list);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        fetchEmojiData();
    }

    private void refreshViews() {
        if (mThread.myEmojiCommentId == 0) {
        } else {
            Intent intent = new Intent(CommentActivity.this, PostActivity.class);
            intent.putExtra(EXTRA_THREAD, mThread);
            startActivity(intent);
            this.finish();
        }
    }

    public void fetchEmojiData() {
        if (mThread.emojiCommentInfoList != null) {
            mAdapter.setEmojiCommentList(value().fromJson(mThread.emojiCommentInfoList, EmojiCommentListType
            ));
            return;
        }

        Schedulers.io().scheduleDirect(() -> {
            Disposable disposable = PonionSeverApi.api().create(ThreadService.class)
                    .getEmojiComments(mThread.serverId, CommonUtils.getToken(this))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response.getCode() != 200) {
                            Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        PonionApplication application = (PonionApplication) getApplication();
                        application.getAppExecutors().diskIO().execute(() -> {
                            mThread.emojiCommentInfoList = value().toJson(response.getData());
                            application.getRepository().updateThread(mThread);
                        });
                        mAdapter.setEmojiCommentList(response.getData());
                    }, error -> {
                        Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    });
            compositeDisposable.add(disposable);
        });
    }

    public void emojiCommentThreadAsync(int emojiId) {
        Schedulers.io().scheduleDirect(() -> {
            Disposable disposable = PonionSeverApi.api().create(LikeService.class)
                    .like(mThread.serverId, emojiId, mThread.myEmojiCommentId, CommonUtils.getToken(this))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response.getCode() != 200) {
                            Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mThread.myEmojiCommentId = emojiId;
                        PonionApplication application = (PonionApplication) getApplication();
                        application.getAppExecutors().diskIO().execute(() -> {
                            application.getRepository().updateThread(mThread);
                        });

                        mAdapter.notifyDataSetChanged();
                        refreshViews();
                    }, error -> {
                        Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    });
            compositeDisposable.add(disposable);
        });
    }
}
