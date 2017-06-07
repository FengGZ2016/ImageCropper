package com.example.imagecropper.dialog;

import android.content.DialogInterface;

/**
 * 作者：国富小哥
 * 日期：2017/6/7
 * Created by Administrator
 * 菜单项实体类
 */

public class Sheet {
    public String funName;//菜单项名称
    public DialogInterface.OnClickListener listener;//回调函数

    public Sheet(String funName, DialogInterface.OnClickListener listener) {
        this.funName = funName;
        this.listener = listener;
    }
}
