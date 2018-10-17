package com.owncloud.android.datamodel;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface UserInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(UserInfo userInfo);

    @Query("SELECT * FROM userInfo WHERE account = :accountName")
    LiveData<UserInfo> load(String accountName);


}
