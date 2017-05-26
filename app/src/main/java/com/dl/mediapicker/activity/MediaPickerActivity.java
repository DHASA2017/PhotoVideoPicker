package com.dl.mediapicker.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dl.mediapicker.R;
import com.dl.mediapicker.adapter.GalleryAdapter;
import com.dl.mediapicker.adapter.PopupDirectoryListAdapter;
import com.dl.mediapicker.decoration.SpaceItemDecoration;
import com.dl.mediapicker.model.Photo;
import com.dl.mediapicker.model.PhotoDirectory;
import com.dl.mediapicker.util.GalleryFinal;
import com.dl.mediapicker.util.MediaManager;
import com.dl.mediapicker.util.MediaStoreHelper;
import com.dl.mediapicker.view.PopupWindowMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MediaPickerActivity extends AppCompatActivity implements MediaManager.OnCheckchangeListener {
    RecyclerView imageRecyclerView;
    GalleryAdapter galleryAdapter;
    Button btnSend;
    TextView tvPreview, tvDirectory;
    PopupWindowMenu menuWindow;
    final int MAX_MOUNTS_DEFALUT = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_picker);
        initManagerAndLimitCount();
        checkPermissionAndGetImages();
    }

    private void initManagerAndLimitCount() {
        MediaManager.getInstance().init();//获取工具并初始化list等必要信息，避免空指针
        readIntentParams();//获取上个页面的数据
        EventBus.getDefault().register(this);//eventbus注册
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendMedia(ArrayList<Photo> photoList) {
        finish();
    }

    private void checkPermissionAndGetImages() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            initUi();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    initUi();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.getInstance().removeOnCheckchangeListener(this);
        EventBus.getDefault().unregister(this);
        MediaManager.getInstance().clear();
    }

    private void initUi() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.select_title));
        tvDirectory = (TextView) findViewById(R.id.tv_dictory);
        tvDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
        Drawable drawable = getResources().getDrawable(R.drawable.btn_dropdown);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        tvDirectory.setCompoundDrawables(null, null, drawable, null);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaManager.getInstance().send();
            }
        });
        tvPreview = (TextView) findViewById(R.id.tv_preview);
        imageRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        imageRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        imageRecyclerView.addItemDecoration(new SpaceItemDecoration(this, 1));
        imageRecyclerView.setHasFixedSize(true);
        imageRecyclerView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        tvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PreviewActivity.class);
                startActivity(intent);
            }
        });
        MediaStoreHelper.getPhotoDirs(this, getIntent().getIntExtra("type", GalleryFinal.TYPE_ALL), new MediaStoreHelper.PhotosResultCallback() {
            @Override
            public void onResultCallback(List<PhotoDirectory> dirs) {
                if (dirs.size() > 0)
                    dirs.get(0).setSelected(true);
                MediaManager.getInstance().setPhotoDirectorys(dirs);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        directoryChanged();
                    }
                });
            }
        });
    }

    /**
     * 获取可选取的最大数量并设置
     * 默认为9张
     */
    private void readIntentParams() {
        Intent intent = getIntent();
        int maxMedia = intent.getIntExtra("maxSum", MAX_MOUNTS_DEFALUT);
        MediaManager.getInstance().setMaxMediaSum(maxMedia);
    }

    /**
     * 展示图片文件夹列表
     */
    private void show() {
        if (menuWindow == null) {
            menuWindow = new PopupWindowMenu(MediaPickerActivity.this, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    directoryChanged();
                    tvDirectory.setText(MediaManager.getInstance().getPhotoDirectorys().get(position).getName());
                }
            });
            menuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    imageRecyclerView.setAlpha(1.0f);
                }
            });
            PopupDirectoryListAdapter popupDirectoryListAdapter = new PopupDirectoryListAdapter(MediaManager.getInstance().getPhotoDirectorys());
            menuWindow.setAdapter(popupDirectoryListAdapter);
            menuWindow.setHeight(calculatePopupWindowHeight(menuWindow.getAdapter().getCount()));
        }
        ObjectAnimator.ofFloat(imageRecyclerView, "alpha", 1.0f, 0.2f).setDuration(600).start();
        menuWindow.showAsDropDown(findViewById(R.id.bottom));
    }

    /**
     * 文件夹路径改变
     */
    private void directoryChanged() {
        if (MediaManager.getInstance().getPhotoDirectorys().isEmpty()) {
            ViewStub emptyStub = ((ViewStub) findViewById(R.id.stub_empty));
            emptyStub.inflate();
            return;
        }
        if (galleryAdapter == null) {
            galleryAdapter = new GalleryAdapter(MediaPickerActivity.this, MediaManager.getInstance().getSelectDirectory());
            galleryAdapter.setImageRecyclerView(imageRecyclerView);
            imageRecyclerView.setAdapter(galleryAdapter);
            MediaManager.getInstance().addOnCheckchangeListener(MediaPickerActivity.this);
            galleryAdapter.setOnItemClickListener(new GalleryItemClickImpl());
        } else {
            galleryAdapter.setImages(MediaManager.getInstance().getSelectDirectory());
            imageRecyclerView.getLayoutManager().scrollToPosition(0);
        }
    }

    /**
     * 不使用匿名内部类，避免出现内存泄漏
     */
    private static class GalleryItemClickImpl implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), PreviewActivity.class);
            intent.putExtra("index", position);
            intent.putExtra("dir", MediaManager.getInstance().getSelectIndex());
            view.getContext().startActivity(intent);
        }
    }

    /**
     *计算popupwindow的高度
     */
    private int calculatePopupWindowHeight(int count) {
        int maxHeight = imageRecyclerView.getMeasuredHeight() - getResources().getDimensionPixelSize(R.dimen.margin_space48);
        int windowHeight = count * getResources().getDimensionPixelOffset(R.dimen.height_space92);
        windowHeight = windowHeight < maxHeight ? windowHeight : maxHeight;
        return windowHeight;
    }


    @Override
    public void onCheckedChanged(Map<Integer, Photo> checkStaus, int changedId, boolean uiUpdated) {
        final int checkSize = checkStaus.size();
        btnSend.setEnabled(checkSize != 0);
        btnSend.setText(checkSize == 0 ? getString(R.string.btn_send) : String.format(getString(R.string.send_multi), checkSize, MediaManager.getInstance().getMaxMediaSum()));

        tvPreview.setEnabled(checkSize != 0);
        tvPreview.setText(checkSize == 0 ? getString(R.string.preview) : getString(R.string.preview_multi, checkSize));
        if (!uiUpdated)
            galleryAdapter.updateCheckbox(changedId);
    }

}
