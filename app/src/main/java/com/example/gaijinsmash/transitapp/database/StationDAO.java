package com.example.gaijinsmash.transitapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.gaijinsmash.transitapp.model.bart.Station;

import java.util.List;

@Dao
public interface StationDAO {

    // Adds a station object to the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addStation(Station station);

    // Removes a station from the database
    @Delete
    void delete(Station station);

    // Gets all stations from the database
    @Query("SELECT * from stations")
    public List<Station> getAllStations();

    // Selects station with the matching id
    @Query("SELECT * from stations where abbr = :abbr")
    public Station getStation(String abbr);

    // Updates a station
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateStation(Station station);

    // Get count
    @Query("SELECT COUNT(*) from stations")
    int countStations();
}