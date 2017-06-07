package com.example.imagecropper;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imagecropper.dialog.SheetDialog;
import com.example.imagecropper.util.PermissionUtils;

import java.util.List;

import ch.ielse.view.imagecropper.ImageCropper;

public class MainActivity extends AppCompatActivity{

    private ImageView mImageView_background;
    private TextView mTextView_txthint;
    private ImageView mImageView_avatar;
    private ImageCropper mImageCropper;
    private String mTag;
    private final String TAG="MainActivity";
    private PictureInquirer pictureInquirer;

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
                /**
                 * 处理裁剪结果
                 * */
                if ("avatar".equals(tag)) {
                    mImageView_avatar.setImageBitmap(transformCropCircle(bitmap));
                } else if ("background".equals(tag)) {
                    mTextView_txthint.setVisibility(View.GONE);
                    mImageView_background.setImageBitmap(bitmap);
                }
            }
        });

        pictureInquirer=new PictureInquirer(this);

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
                    pictureInquirer.queryPictureFromAlbum(mTag);
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
                    pictureInquirer.queryPictureFromCamera(mTag);
                }
            });
            sheetDialog.create().show();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, new PermissionUtils.Callback() {
            @Override
            public void onResult(final int requestCode, List<String> grantPermissions, List<String> deniedPermissions) {
                //被拒绝的权限
                if (PermissionUtils.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("权限提示").setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                            .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, requestCode);
                                }
                            })
                            .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                } else if (!deniedPermissions.isEmpty()) {
                    Log.e(TAG, "一部分权限是暂时拒绝(仍为询问状态)，可以提示权限工作原理来告知用户为什么要此权限。来增加下次用户同意该权限申请的可能性");
                } else {
                    Log.e(TAG, "申请的权限用户全部都允许了");
                    for (String grant : grantPermissions) {
                        if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(grant)) {
                            pictureInquirer.queryPictureFromAlbum(mTag);
                        } else if (Manifest.permission.CAMERA.equals(grant)) {
                            pictureInquirer.queryPictureFromCamera(mTag);
                        }
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        pictureInquirer.onActivityResult(requestCode, resultCode, data, new PictureInquirer.Callback() {
            /**
             * 得到照片的路径的Tags
             * */
            @Override
            public void onPictureQueryOut(String path, String tag) {
                Log.d("onPictureQueryOut","返回Path的路径："+path);
                if ("avatar".equals(tag)) {
                    //执行裁剪

                    mImageCropper.crop(path, mImageView_avatar.getWidth(), mImageView_avatar.getHeight(), true, tag);

                } else if ("background".equals(tag)) {
                    //执行裁剪
                    mImageCropper.crop(path, mImageView_background.getWidth(), mImageView_background.getHeight(), false, tag);
                }

            }
        });

    }



    /**
     * 圆化图片
     * */
    private Bitmap transformCropCircle(Bitmap resource) {
        Bitmap source = resource;
        int size = Math.min(source.getWidth(), source.getHeight());

        int width = (source.getWidth() - size) / 2;
        int height = (source.getHeight() - size) / 2;

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader =
                new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        if (width != 0 || height != 0) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(-width, -height);
            shader.setLocalMatrix(matrix);
        }
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        return bitmap;
    }







}
