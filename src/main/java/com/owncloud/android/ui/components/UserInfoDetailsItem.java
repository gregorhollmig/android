package com.owncloud.android.ui.components;

import android.support.annotation.DrawableRes;

public class UserInfoDetailsItem {
    @DrawableRes
    public int icon;
    public String text;
    public String iconContentDescription;
    public int tintColor;

    public UserInfoDetailsItem(@DrawableRes int icon, String text, String iconContentDescription, int tintColor) {
        this.icon = icon;
        this.text = text;
        this.iconContentDescription = iconContentDescription;
        this.tintColor = tintColor;
    }
}
