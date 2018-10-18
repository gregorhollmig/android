package com.owncloud.android.ui.viewModel;

import android.accounts.Account;
import android.app.FragmentManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Room;

import com.owncloud.android.MainApp;
import com.owncloud.android.datamodel.UserInfo;
import com.owncloud.android.datamodel.UserInfoDatabase;
import com.owncloud.android.repository.UserInfoRepository;
import com.owncloud.android.ui.dialog.AccountRemovalConfirmationDialog;
import com.owncloud.android.ui.events.TokenPushEvent;
import com.owncloud.android.utils.PushUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Executors;


public class UserInfoViewModel extends ViewModel {

    private UserInfoRepository userInfoRepository;
    private Account account;
    private LiveData<UserInfo> userInfo;

    // @Inject
    public UserInfoViewModel() {
        UserInfoDatabase userInfoDatabase = Room.databaseBuilder(MainApp.getAppContext(), UserInfoDatabase.class, "userInfo.db").build();

        userInfoRepository = new UserInfoRepository(userInfoDatabase.userInfoDao(), Executors.newCachedThreadPool());
    }

    public UserInfoViewModel(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    public void init(Account account) {
        if (this.account != null) {
            return;
        }

        userInfo = userInfoRepository.getUserInfo(account);

        // TODO add here setHeader, registerPush, etc.
    }

    public LiveData<UserInfo> getUserInfo() {
        return userInfo;
    }

    public static void openAccountRemovalConfirmationDialog(Account account, FragmentManager fragmentManager,
                                                            boolean removeDirectly) {
        AccountRemovalConfirmationDialog dialog = AccountRemovalConfirmationDialog.newInstance(account, removeDirectly);
        dialog.show(fragmentManager, "dialog");
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(TokenPushEvent event) {
        PushUtils.pushRegistrationToServer();
    }
}
