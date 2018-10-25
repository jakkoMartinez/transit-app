package com.zuk0.gaijinsmash.riderz.data.local.room.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.zuk0.gaijinsmash.riderz.data.local.room.converter.Converters;
import com.zuk0.gaijinsmash.riderz.data.local.room.dao.TripDao;
import com.zuk0.gaijinsmash.riderz.data.local.entity.trip_response.Trip;

@Database(entities = {Trip.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class TripDatabase extends RoomDatabase {

    private static TripDatabase INSTANCE;
    public abstract TripDao getTripDao();

    public static TripDatabase getRoomDB(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TripDatabase.class, "trips")
                    .addMigrations(MIGRATION)
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        if(INSTANCE.isOpen()) {
            INSTANCE.close();
        }
        INSTANCE = null;
    }

    // Edit this to create a new migration for database - and use ".addMigrations(example)
    private static final Migration MIGRATION = new Migration(1,2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE trips ADD COLUMN last_update INTEGER");
        }
    };
}