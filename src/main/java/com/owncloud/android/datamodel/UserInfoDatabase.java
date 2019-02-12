package com.owncloud.android.datamodel;

import com.owncloud.android.utils.Converters;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {UserInfo.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class UserInfoDatabase extends RoomDatabase {

    public abstract UserInfoDao userInfoDao();
}
