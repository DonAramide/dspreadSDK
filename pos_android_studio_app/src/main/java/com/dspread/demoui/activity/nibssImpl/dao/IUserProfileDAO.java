package com.dspread.demoui.activity.nibssImpl.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

//import com.iips.mbbs_horizon.mbbs.utils.UserInfo;

import com.dspread.demoui.activity.nibssImpl.utils.UserInfo;

import java.util.List;

@Dao
public interface IUserProfileDAO {

    @Query("SELECT * FROM rm_user_profiles")
    List<UserInfo> getAll();

    @Query("SELECT * FROM rm_user_profiles where username = :username")
    UserInfo findByName(String username);

    @Query("SELECT COUNT(*) from rm_user_profiles")
    int countUsers();

    @Insert
    void insertAll(UserInfo... users);

    @Delete
    void delete(UserInfo user);

    @Update
    void update(UserInfo user);

    @Query("SELECT * FROM rm_params where code LIKE :refno ")
    List<ParamInfo> getOfflineTrans(String refno);


    @Query("SELECT * FROM rm_params where code  = :code  ")
    ParamInfo getParamInfo(String code);

    @Insert
    void saveParamInfo(ParamInfo cInfo);

    @Update
    void updateParamInfo(ParamInfo info);

    @Delete
    void deleteParamInfo(ParamInfo info);

    @Insert
    void saveLookupdataList(List<LookupDataInfo> list);

    @Insert
    void saveLookupdata(LookupDataInfo ldf);

    @Update
    void updateLookupdataInfo(LookupDataInfo info);

    @Query("SELECT * FROM rm_lookup_data where lookupCode  = :code ")
    List<LookupDataInfo> getLookupDataList(String code);

    @Query("SELECT * FROM rm_lookup_data where lookupCode  = :lookupCode AND code = :code ")
    LookupDataInfo getLookupData(String lookupCode,String code);

}
