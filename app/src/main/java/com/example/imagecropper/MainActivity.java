package com.example.imagecropper;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ch.ielse.view.imagecropper.ImageCropper;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView_background;
    private TextView mTextView_txthint;
    private ImageView mImageView_avatar;
    private ImageCropper mImageCropper;
    private String mTag;
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
                showSheetDialog();
            }
        });
                mTextView_txthint= (TextView) findViewById(R.id.tex_hint);
        mImageView_avatar= (ImageView) findViewById(R.id.image_avatar);
        mImageView_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTag = "avatar";
                //显示对话框
                showSheetDialog();
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
     * */
    private void showSheetDialog() {

    }
}
