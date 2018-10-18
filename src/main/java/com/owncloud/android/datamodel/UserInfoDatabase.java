package com.owncloud.android.datamodel;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.owncloud.android.utils.Converters;

@Database(entities = {UserInfo.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class UserInfoDatabase extends RoomDatabase {

    public abstract UserInfoDao userInfoDao();
}
