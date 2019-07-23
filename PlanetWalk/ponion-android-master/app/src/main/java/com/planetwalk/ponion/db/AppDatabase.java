package com.planetwalk.ponion.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.planetwalk.ponion.AppExecutors;
import com.planetwalk.ponion.db.DAO.BuddyDao;
import com.planetwalk.ponion.db.DAO.PostDao;
import com.planetwalk.ponion.db.DAO.ThreadDao;
import com.planetwalk.ponion.db.Entity.BuddyEntity;
import com.planetwalk.ponion.db.Entity.PostEntity;
import com.planetwalk.ponion.db.Entity.ThreadEntity;
import com.planetwalk.ponion.db.converter.DateConverter;

@Database(entities = {ThreadEntity.class, BuddyEntity.class, PostEntity.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "poketalk_database";

    private static volatile AppDatabase sInstance;

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();


    public abstract BuddyDao buddyDao();

    public abstract ThreadDao threadDao();

    public abstract PostDao postDao();

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());

//                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
//                            AppDatabase.class, AppDatabase.DATABASE_NAME)
//                            .fallbackToDestructiveMigration()
//                            .addCallback(sRoomDatabaseCallback)
//                            .build();
                }
            }
        }
        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDatabase buildDatabase(final Context appContext,
                                             final AppExecutors executors) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {
                            // Add a delay to simulate a long-running operation
//                            addDelay();
                            // Generate the data for pre-population
                            AppDatabase database = AppDatabase.getInstance(appContext, executors);

//                            List<FoodEntity> foodEntities = DataGenerator.generateFoods();
//                            List<FoodStorageEntity> foodStorageEntityList = DataGenerator.generateFoodStorage(foodEntities);
//
                            BuddyEntity buddyEntity = DataGenerator.generateMeBuddy();
//
//                            List<PlateEntity> tableEntities = DataGenerator.gengerateTables();
//
                            insertData(database, buddyEntity);
                            // notify that the database was created and it's ready to be used
                            database.setDatabaseCreated();
                        });
                    }
                })
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private static void insertData(final AppDatabase database,
                                   final BuddyEntity buddyEntity
    ) {
        database.runInTransaction(() -> {
            database.buddyDao().insert(buddyEntity);
        });
    }

//    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
//
//        @Override
//        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//            super.onCreate(db);
//            Log.v("food", "populate the food data");
//            new PopulateDbAsync(sInstance).execute();
//        }
//    };

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `productsFts` USING FTS4("
//                    + "`name` TEXT, `description` TEXT, content=`products`)");
//            database.execSQL("INSERT INTO productsFts (`rowid`, `name`, `description`) "
//                    + "SELECT `id`, `name`, `description` FROM products");
        }
    };

}
