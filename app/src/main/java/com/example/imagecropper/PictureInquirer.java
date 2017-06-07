package com.example.imagecropper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * 作者：国富小哥
 * 日期：2017/6/7
 * Created by Administrator
 * 获取图片
 */

public class PictureInquirer {
    static final String TAG = "PictureInquirer";
    static final String IMAGE_FROM_CONTENT = "content";
    static final String IMAGE_FROM_FILE = "file";
    static int REQUEST_CODE_CAPTURE_PICTURE = 412;
    static int REQUEST_CODE_PICK_IMAGE = 413;
    private File fCapture;

    private Activity mHolder;
    private String mTag;


    public PictureInquirer(Activity activity) {
        if (activity == null) throw new IllegalArgumentException("activity is null");

        mHolder = activity;
    }

   /**
    * 根据图库获取照片
    * */
    public void queryPictureFromAlbum(String tag) {
        mTag = tag;
        Intent intentPick = new Intent(Intent.ACTION_PICK);
        intentPick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        try {
            //返回图片
            mHolder.startActivityForResult(intentPick, REQUEST_CODE_PICK_IMAGE);
        } catch (ActivityNotFoundException e) {
            Intent intentGetContent = new Intent(Intent.ACTION_GET_CONTENT);
            intentGetContent.setType("image/*");
            Intent wrapperIntent = Intent.createChooser(intentGetContent, null);
            try {
                //返回图片
                mHolder.startActivityForResult(wrapperIntent, REQUEST_CODE_PICK_IMAGE);
            } catch (ActivityNotFoundException e1) {
                Toast.makeText(mHolder.getApplicationContext(), "没有找到文件浏览器或相册", Toast.LENGTH_SHORT).show();
            }
        }
    }



    /**
     * 从相机中拍照
     * */
    public void queryPictureFromCamera(String tag) {
        mTag = tag;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fCapture = new File(mHolder.getExternalCacheDir(), "PIC" + System.currentTimeMillis() + ".jpg");
        try {
            if (!fCapture.exists()) fCapture.createNewFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fCapture));
            //返回结果
            mHolder.startActivityForResult(intent, REQUEST_CODE_CAPTURE_PICTURE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mHolder.getApplicationContext(), "图片无法保存", Toast.LENGTH_SHORT).show();
        }
    }



    public interface Callback {
        /**
         * 获得了一张新的图片
         *
         * @param path 新图片的路径
         * @param tag  自设标志
         */
        void onPictureQueryOut(String path, String tag);
    }


    /**
     * 接收结果
     * */
    public void onActivityResult(int requestCode, int resultCode, Intent data, Callback cb) {
       //图库选择
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                //imageUri.getScheme()=="file"
                if (imageUri != null && IMAGE_FROM_FILE.equals(imageUri.getScheme())) {
                    Log.d(TAG, "openPhotos pick image " + imageUri.getPath());
                    //返回图片的URI路径和mTag
                    if (cb != null) cb.onPictureQueryOut(imageUri.getPath(), mTag);

                    // //imageUri.getScheme()=="content"
                } else if (imageUri != null && IMAGE_FROM_CONTENT.equals(imageUri.getScheme())) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = mHolder.getContentResolver().query(imageUri, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.d(TAG, "openPhotos pick image " + picturePath);
                        //返回图片的URI路径和mTag
                        if (cb != null) cb.onPictureQueryOut(picturePath, mTag);
                    } else {
                        Log.e(TAG, "openPhotos pick image cursor null");
                    }
                } else {
                    Toast.makeText(mHolder.getApplicationContext(), "图片获取失败，并没有成功拿到图片路径", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, imageUri != null ? ("openPhotos unknown uri\n" + imageUri.getPath()) : "openPhotos imageUri null");
                }
            }
            //相机拍照
        } else if (requestCode == REQUEST_CODE_CAPTURE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "openCamera " + fCapture.getAbsolutePath());
                if (cb != null) cb.onPictureQueryOut(fCapture.getAbsolutePath(), mTag);
            }
        }
    }

}
