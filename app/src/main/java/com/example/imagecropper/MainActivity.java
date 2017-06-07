package com.example.imagecropper;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imagecropper.dialog.SheetDialog;
import com.example.imagecropper.util.PermissionUtils;

import ch.ielse.view.imagecropper.ImageCropper;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView_background;
    private TextView mTextView_txthint;
    private ImageView mImageView_avatar;
    private ImageCropper mImageCropper;
    private String mTag;
    private final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mImageView_background= (ImageView) findViewById(R.id.image_background);
        mImageView_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTag = "background";
                //显示对话框
                showSheetDialog(v);
            }
        });
                mTextView_txthint= (TextView) findViewById(R.id.tex_hint);
        mImageView_avatar= (ImageView) findViewById(R.id.image_avatar);
        mImageView_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTag = "avatar";
                //显示对话框
                showSheetDialog(v);
            }
        });
                mImageCropper= (ImageCropper) findViewById(R.id.image_cropper);
        //设置默认值
        // 设置是否按照vImageCropper.crop()方法入参outputWidth和outputHeight来输出，false表示仅只按其比例输出，true为绝对尺寸输出
        mImageCropper.setOutputFixedSize(false);
        //实现裁剪结果回调
        mImageCropper.setCallback(new ImageCropper.Callback() {
            @Override
            public void onPictureCropOut(Bitmap bitmap, String tag) {

            }
        });

    }

    /**
     * 显示对话框
     *
     * @param v*/
    private void showSheetDialog(View v) {
        if (!TextUtils.isEmpty(mTag)){
            //如果Tag不为空
            SheetDialog.Builder sheetDialog=new SheetDialog.Builder(v.getContext());
            sheetDialog.setTitle("更换图片");
            //添加菜单项
            sheetDialog.addMenu("图库选择", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //申请权限
                    if (!PermissionUtils.hasPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        PermissionUtils.requestPermissions(MainActivity.this, PermissionUtils.PERMISSION_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                        Log.d(TAG, "requestPermissions READ_EXTERNAL_STORAGE");
                        return;
                    }
                    //打开图库
                }
            });
            //添加菜单项
            sheetDialog.addMenu("相机拍照", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //申请权限
                    if (!PermissionUtils.hasPermission(MainActivity.this, Manifest.permission.CAMERA)) {
                        PermissionUtils.requestPermissions(MainActivity.this, PermissionUtils.PERMISSION_CAMERA, Manifest.permission.CAMERA);
                        Log.d(TAG, "requestPermissions CAMERA");
                        return;
                    }
                    //打开相机

                }
            });
            sheetDialog.create().show();

        }
    }
}
