package com.owncloud.android.repository;

import android.accounts.Account;
import android.arch.lifecycle.LiveData;

import com.owncloud.android.MainApp;
import com.owncloud.android.datamodel.UserInfoDao;
import com.owncloud.android.lib.common.UserInfo;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.users.GetRemoteUserInfoOperation;

import java.util.concurrent.Executor;

// @Singleton // TODO re-add?
public class UserInfoRepository {

    private final UserInfoDao userInfoDao;
    private final Executor executor;

    // @Inject
    public UserInfoRepository(UserInfoDao userInfoDao, Executor executor) {
        this.userInfoDao = userInfoDao;
        this.executor = executor;
    }

    public LiveData<com.owncloud.android.datamodel.UserInfo> getUserInfo(Account account) {
        refreshUserInfo(account);

        return userInfoDao.load(account.name);
    }

    private void refreshUserInfo(Account account) {
        executor.execute(() -> {
            // TODO load from server only if force refresh or older than 5min
            // userInfoDao.hasUserInfo()

            RemoteOperation getRemoteUserInfoOperation = new GetRemoteUserInfoOperation();

            // TODO how to get client better
            RemoteOperationResult result = getRemoteUserInfoOperation.execute(account, MainApp.getAppContext());

            if (result.isSuccess() && result.getData() != null) {
                userInfoDao.save(parseUserInfo((UserInfo) result.getData().get(0), account));
            }
            // TODO error handling if fetch fails
        });
    }

    private com.owncloud.android.datamodel.UserInfo parseUserInfo(UserInfo remoteUserInfo, Account account) {
        com.owncloud.android.datamodel.UserInfo userInfo = new com.owncloud.android.datamodel.UserInfo();

        userInfo.account = account.name;
        userInfo.displayName = remoteUserInfo.displayName;
        userInfo.email = remoteUserInfo.email;
        userInfo.phone = remoteUserInfo.phone;
        userInfo.address = remoteUserInfo.address;
        userInfo.website = remoteUserInfo.website;
        userInfo.twitter = remoteUserInfo.twitter;
        userInfo.groups = remoteUserInfo.groups;
        userInfo.quota = remoteUserInfo.quota;

        return userInfo;
    }
}
