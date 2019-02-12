package com.owncloud.android.datamodel;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(UserInfo userInfo);

    @Query("SELECT * FROM userInfo WHERE account = :accountName")
    LiveData<UserInfo> load(String accountName);


}
