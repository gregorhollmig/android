package com.owncloud.android.di;

import android.app.Application;

import com.owncloud.android.ui.activity.UserInfoActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(dependencies = {}, modules = {AppModule.class, UserInfoModule.class})
public interface UserInfoComponent {

    // UserInfoViewModel getViewModel();

    void inject(UserInfoActivity activity);

    Application application();
}
