package com.efe.miniproject.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.efe.miniproject.R;
import com.google.android.material.button.MaterialButton;

public class AppDialog extends Dialog {

    AppDialogParam param;

    ImageView imageViewCloseDialog;
    ImageView imageViewIcon;
    TextView textViewTitle;
    TextView textViewSubtitle;
    MaterialButton buttonCancel;
    MaterialButton buttonOk;
    MaterialButton buttonOther;

    public AppDialog(AppDialogParam param) {
        super(param.context);
        this.param = param;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_app_custom);
        getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imageViewCloseDialog = findViewById(R.id.image_view_close_dialog);
        imageViewIcon = findViewById(R.id.image_view_icon);
        textViewTitle = findViewById(R.id.text_view_title);
        textViewSubtitle = findViewById(R.id.text_view_subtitle);
        buttonCancel = findViewById(R.id.button_cancel);
        buttonOk = findViewById(R.id.button_ok);

        imageViewCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDialog.this.dismiss();
            }
        });

        this.setCancelable(false);

        param.apply(this);
    }

    private void setIcon(@DrawableRes int iconId) {
        setIcon(ContextCompat.getDrawable(getContext(), iconId));
    }

    private void setIcon(Drawable value) {
        if (value == null) {
            imageViewIcon.setVisibility(View.GONE);
        } else {
            imageViewIcon.setVisibility(View.VISIBLE);
            imageViewIcon.setImageDrawable(value);
        }
    }

    private void setTitle(String value) {
        textViewTitle.setVisibility(View.VISIBLE);
        textViewTitle.setText(value);
    }

    private void setSubtitle(String value) {
        textViewSubtitle.setVisibility(View.VISIBLE);
        textViewSubtitle.setText(value);
    }

    private void setOkButton(String name, final OnClickListener listener) {
        buttonOk.setVisibility(View.VISIBLE);
        buttonOk.setText(name);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(AppDialog.this);
            }
        });
    }

    private void setCancelButton(String name, final OnClickListener listener) {
        buttonCancel.setVisibility(View.VISIBLE);
        buttonCancel.setText(name);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(AppDialog.this);
            }
        });
    }

    private void setOtherButton(String name, final OnClickListener listener) {
        buttonOther.setVisibility(View.VISIBLE);
        buttonOther.setText(name);
        buttonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(AppDialog.this);
            }
        });
    }

    private void setIconVisibility(boolean isVisible) {
        imageViewIcon.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setOkButtonIcon(Drawable okIcon, int okIconDirection) {
        buttonOk.setIcon(okIcon);
        buttonOk.setIconGravity(okIconDirection);
    }

    private void setCloseButtonVisibility(boolean isVisible) {
        imageViewCloseDialog.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public static class Builder {

        Context context;
        AppDialogParam param;

        public Builder(Context context) {
            this.context = context;
            param = new AppDialogParam(context);
        }

        Builder setIcon(@DrawableRes int iconId) {
            param.iconId = iconId;
            return this;
        }

        Builder setIcon(Drawable icon) {
            param.icon = icon;
            return this;
        }

        Builder setTitle(@StringRes int title) {
            param.title = param.context.getText(title).toString();
            return this;
        }

        public Builder setTitle(String title) {
            param.title = title;
            return this;
        }

        Builder setSubtitle(@StringRes int subtitle) {
            param.subtitle = param.context.getText(subtitle).toString();
            return this;
        }

        public Builder setSubtitle(String subtitle) {
            param.subtitle = subtitle;
            return this;
        }

        Builder setOkButton(@StringRes int textId, OnClickListener listener) {
            return setOkButton(param.context.getText(textId).toString(), listener);
        }

        public Builder setOkButton(String buttonName, OnClickListener listener) {
            param.okButtonText = buttonName;
            param.mOkButtonListener = listener;
            return this;
        }

//        fun setOkButton(buttonName: String, listener: OnClickListener): Builder {
//            param.okButtonText = buttonName
//            param.mOkButtonListener = listener
//            return this
//        }
//
//        fun setOkButtonListener(listener: OnClickListener): Builder {
//            param.mOkButtonListener = listener
//            return this
//        }

        Builder setCancelButton(@StringRes int textId, OnClickListener listener) {
            return setCancelButton(param.context.getText(textId).toString(), listener);
        }

        Builder setCancelButton(String buttonName, OnClickListener listener) {
            param.cancelButtonText = buttonName;
            param.mCancelButtonListener = listener;
            return this;
        }

        Builder setOtherButton(@StringRes int textId, OnClickListener listener) {
            return setOtherButton(param.context.getText(textId).toString(), listener);
        }

        Builder setOtherButton(String buttonName, OnClickListener listener) {
            param.otherButtonText = buttonName;
            param.mOtherButtonListener = listener;
            return this;
        }

        public Builder setIconVisibility(boolean isVisible) {
            param.isIconVisible = isVisible;
            return this;
        }

        Builder setOkButtonIcon(Drawable iconId, int iconDirection) {
            param.okButtonIcon = iconId;
            param.okIconDirection = iconDirection;
            return this;
        }

        Builder setCloseButtonVisibility(boolean isVisible) {
            param.isCloseButtonVisible = isVisible;
            return this;
        }

        public AppDialog create() {
            return new AppDialog(param);
        }

    }

    public interface OnClickListener {
        void onClick(AppDialog dialog);
    }

    static class AppDialogParam {

        Context context;

        public AppDialogParam(Context context) {
            this.context = context;
        }

        int iconId;
        Drawable icon;
        String title;
        String subtitle;
        String okButtonText;
        String cancelButtonText;
        String otherButtonText;
        boolean isIconVisible;
        boolean isCloseButtonVisible = true;

        Drawable okButtonIcon;
        Integer okIconDirection;

        OnClickListener mOkButtonListener;
        OnClickListener mCancelButtonListener;
        OnClickListener mOtherButtonListener;

        void apply(AppDialog dialog) {

            if (iconId != 0) {
                dialog.setIcon(iconId);
            }

            if (icon != null)
                dialog.setIcon(icon);

            if (title != null)
                dialog.setTitle(title);

            if (subtitle != null)
                dialog.setSubtitle(subtitle);

            if (okButtonText != null)
                dialog.setOkButton(okButtonText, mOkButtonListener);

            if (cancelButtonText != null)
                dialog.setCancelButton(cancelButtonText, mCancelButtonListener);

            if (otherButtonText != null)
                dialog.setOtherButton(otherButtonText, mOtherButtonListener);

            dialog.setIconVisibility(isIconVisible);

            if (okButtonIcon != null && okIconDirection != null)
                dialog.setOkButtonIcon(okButtonIcon, okIconDirection);

            dialog.setCloseButtonVisibility(isCloseButtonVisible);

        }
    }
}
