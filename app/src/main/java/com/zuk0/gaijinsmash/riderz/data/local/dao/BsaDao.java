package com.zuk0.gaijinsmash.riderz.data.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.zuk0.gaijinsmash.riderz.data.local.entity.bsa_response.BsaXmlResponse;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface BsaDao {
    @Insert(onConflict = REPLACE)
    void save(BsaXmlResponse bsa);

    @Query("SELECT * FROM advisories where id = :bsaId")
    LiveData<BsaXmlResponse> load(int bsaId);

    @Query("SELECT * From advisories where id = :bsaId")
    boolean bsaExists(int bsaId);
}