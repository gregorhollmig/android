package com.owncloud.android.datamodel;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {UserInfo.class}, version = 1)
public abstract class UserInfoDatabase extends RoomDatabase {

    public abstract UserInfoDao userInfoDao();
}
