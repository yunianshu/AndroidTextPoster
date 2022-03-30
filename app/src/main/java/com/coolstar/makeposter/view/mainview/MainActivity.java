package com.coolstar.makeposter.view.mainview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coolstar.makeposter.R;
import com.coolstar.makeposter.beans.PictureInfo;
import com.coolstar.makeposter.presenter.main.MainPresenter;
import com.coolstar.makeposter.view.posterview.PosterActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IMainView {

    private static final int REQUEST_CODE_ASK_CALL_PHONE = 1;
    private MainPresenter presenter;
    private MainPicAdapter adapter;
    private MainPicAdapter.OnItemClickListener itemClickListener = new MainPicAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int postion, PictureInfo item, View view) {
            openPosterActivity(item,view);
        }

        @Override
        public void onItemLongClick(int postion, PictureInfo item, View view) {
//            showToast(postion+"--long---click->"+item.fileName+"");
        }
    };

    private void openPosterActivity(PictureInfo item,View v) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,v,getString(R.string.main_trans_imgname));
        Bundle bundle = optionsCompat.toBundle();
        Intent intent = new Intent(this, PosterActivity.class);
        intent.putExtra("PictureInfo",item);
        ActivityCompat.startActivity(this,intent,bundle);
    }

    private void showToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    //--Activity.start--------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        presenter = new MainPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_ASK_CALL_PHONE);
                return;
            }else{
                //权限允许，调用加载
                presenter.loadAllPictures(this.getApplicationContext());
            }
        } else {
            //低版系统，直接加载
            presenter.loadAllPictures(this.getApplicationContext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_ASK_CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    presenter.loadAllPictures(this.getApplicationContext());
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "权限限制读取相册", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.releasePresenter();
        super.onDestroy();
    }
//--Activity.end--------------------------------------------------------------------------
    RecyclerView listView;
    TextView tvLoading,tvError;
    private void initView() {
        listView = (RecyclerView)findViewById(R.id.main_recylerView);
//        listView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        GridLayoutManager gridLayoutMgr = new GridLayoutManager(this,2);
        gridLayoutMgr.setOrientation(GridLayoutManager.VERTICAL);
        listView.setLayoutManager(new GridLayoutManager(this,2));
        DividerGridItemDecoration decoration = new DividerGridItemDecoration(this);
        listView.addItemDecoration(decoration);
        listView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MainPicAdapter(this,itemClickListener);
        listView.setAdapter(adapter);
        tvLoading = (TextView)findViewById(R.id.main_loadingView);
        tvError = (TextView)findViewById(R.id.main_errView);
    }

    //--IMainView.start--------------------------------------------------------------------------
    @Override
    public void startLoading() {
        listView.setVisibility(View.INVISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.INVISIBLE);
    }

    @Override
    public void stopLoading() {
        listView.setVisibility(View.INVISIBLE);
        tvLoading.setVisibility(View.INVISIBLE);
        tvError.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setPictureList(List<PictureInfo> list) {
        if(adapter!=null){
            adapter.setPicList(list);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showPictureList() {
        listView.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.INVISIBLE);
        tvError.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showErrorInfo() {
        listView.setVisibility(View.INVISIBLE);
        tvLoading.setVisibility(View.INVISIBLE);
        tvError.setVisibility(View.VISIBLE);
    }
    //--IMainView.end--------------------------------------------------------------------------
}
