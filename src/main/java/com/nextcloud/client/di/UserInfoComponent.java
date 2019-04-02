package com.nextcloud.client.di;

import android.app.Application;

import com.owncloud.android.ui.activity.UserInfoActivity;
import com.owncloud.android.ui.viewModel.UserInfoViewModel;

//@Singleton
// @Component(dependencies = {}, modules = {AppModule.class, UserInfoModule.class})
public interface UserInfoComponent {

    UserInfoViewModel getViewModel();

    void inject(UserInfoActivity activity);

    Application application();
}
