package com.example.imagecropper.dialog;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.imagecropper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：国富小哥
 * 日期：2017/6/7
 * Created by Administrator
 * 自定义对话框
 */

public class SheetDialog extends Dialog {


    public SheetDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Params {
        private final List<Sheet> menuList = new ArrayList<>();//菜单列表
        private View.OnClickListener cancelListener;
        private String menuTitle;
        private String cancelText;
        private Context context;
    }

    public static class Builder {
        private boolean canCancel = true;
        private boolean shadow = true;
        private final Params p;

        public Builder(Context context) {
            p = new Params();
            p.context = context;
        }

        public Builder setCanCancel(boolean canCancel) {
            this.canCancel = canCancel;
            return this;
        }

        public Builder setShadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        public Builder setTitle(String title) {
            this.p.menuTitle = title;
            return this;
        }

        public Builder addMenu(String text, DialogInterface.OnClickListener listener) {
            Sheet bm = new Sheet(text, listener);
            this.p.menuList.add(bm);
            return this;
        }

        public Builder setCancelListener(View.OnClickListener cancelListener) {
            p.cancelListener = cancelListener;
            return this;
        }

        public Builder setCancelText(String text) {
            p.cancelText = text;
            return this;
        }

        //create
        public SheetDialog create() {
            //两个参数，1：上下文 2：主题
            final SheetDialog dialog = new SheetDialog(p.context, shadow ? R.style.Theme_Light_NoTitle_Dialog : R.style.Theme_Light_NoTitle_NoShadow_Dialog);
            Window window = dialog.getWindow();
            //设置窗口进出动画
            window.setWindowAnimations(R.style.Animation_Bottom_Rising);
            //间距为0
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setGravity(Gravity.BOTTOM);
            //找到dialog_sheet的布局
            View view = LayoutInflater.from(p.context).inflate(R.layout.dialoa_sheet, null);
            //找到dialog_sheet的布局子项
            TextView btnCancel = (TextView) view.findViewById(R.id.btn_cancel);
            ViewGroup layContainer = (ViewGroup) view.findViewById(R.id.lay_container);

            ViewGroup.LayoutParams lpItem=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
            ViewGroup.LayoutParams lpDivider=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);

            int dip1 = (int) (1 * p.context.getResources().getDisplayMetrics().density + 0.5f);
            int spacing = dip1 * 12;

            boolean hasTitle = !TextUtils.isEmpty(p.menuTitle);
            //menuTitle不为空
            if (hasTitle) {
                TextView tTitle = new TextView(p.context);
                tTitle.setLayoutParams(lpItem);
                tTitle.setGravity(Gravity.CENTER);
                tTitle.setTextColor(0xFF8F8F8F);
                tTitle.setText(p.menuTitle);
                tTitle.setTextSize(17);
                tTitle.setPadding(0, spacing, 0, spacing);
                tTitle.setBackgroundResource(R.drawable.selector_dialog_sheet_top);
                layContainer.addView(tTitle);

                View viewDivider = new View(p.context);
                viewDivider.setLayoutParams(lpDivider);
                viewDivider.setBackgroundColor(0xFFCED2D6);
                layContainer.addView(viewDivider);
            }

            for (int i = 0; i < p.menuList.size(); i++) {
                final Sheet sheet = p.menuList.get(i);
                TextView bbm = new TextView(p.context);
                bbm.setLayoutParams(lpItem);
                int backgroundResId = R.drawable.selector_dialog_sheet_center;
                if (p.menuList.size() > 1) {
                    if (i == 0) {
                        if (hasTitle) {
                            backgroundResId = R.drawable.selector_dialog_sheet_center;
                        } else {
                            backgroundResId = R.drawable.selector_dialog_sheet_top;
                        }
                    } else if (i == p.menuList.size() - 1) {
                        backgroundResId = R.drawable.selector_dialog_sheet_bottom;
                    }
                } else if (p.menuList.size() == 1) {
                    backgroundResId = R.drawable.selector_dialog_sheet_single;
                }
                bbm.setBackgroundResource(backgroundResId);
                bbm.setPadding(0, spacing, 0, spacing);
                bbm.setGravity(Gravity.CENTER);
                bbm.setText(sheet.funName);
                bbm.setTextColor(0xFF007AFF);
                bbm.setTextSize(19);
                bbm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheet.listener.onClick(dialog, 0);
                    }
                });
                layContainer.addView(bbm);

                if (i != p.menuList.size() - 1) {
                    View viewDivider = new View(p.context);
                    viewDivider.setLayoutParams(lpDivider);
                    viewDivider.setBackgroundColor(0xFFCED2D6);
                    layContainer.addView(viewDivider);
                }
            }

            if (!TextUtils.isEmpty(p.cancelText)) {
                btnCancel.setText(p.cancelText);
            }

            if (p.cancelListener != null) {
                btnCancel.setOnClickListener(p.cancelListener);
            } else {
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(canCancel);
            dialog.setCancelable(canCancel);
            return dialog;
        }

    }
}
