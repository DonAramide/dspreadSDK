package com.dspread.demoui.activity.nibssImpl.dao;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dspread.demoui.activity.nibssImpl.utils.UserInfo;

//import com.iips.mbbs_horizon.mbbs.utils.UserInfo;


@Database(entities = {UserInfo.class, MasterCategoryInfo.class,  LookupDataInfo.class, ParamInfo.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract IUserProfileDAO userProfileDAO();
    //public abstract IRetailDAO userProfileDAO();

 static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
             //database.execSQL("DROP TABLE rm_user_profile ");
           // database.execSQL("DROP TABLE rm_sale_order_items ");
                   // +"ADD COLUMN address String");

        }
    };

    public static AppDatabase getAppDatabase(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "cpoint-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            //.fallbackToDestructiveMigration()
                           //.addMigrations(MIGRATION_1_2)

                            .allowMainThreadQueries()
                            .build();


        }
        return INSTANCE;
    }

    public static String deleteDatabase(Context ctx)
    {
        String result ="";
        try {
            ctx.deleteDatabase("cpoint-database");
            result = "00";
        }
        catch (Exception ex)
        {
            result = ex.getMessage();
            ex.printStackTrace();
        }

        return  result;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}